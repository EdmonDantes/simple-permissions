package io.github.edmondantes.simple.permissions.manager.impl


import io.github.edmondantes.simple.permissions.PermissionOwner
import io.github.edmondantes.simple.permissions.data.entity.PermissionGroup
import io.github.edmondantes.simple.permissions.data.storage.GroupReplacer
import io.github.edmondantes.simple.permissions.data.storage.PermissionGroupStorage
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage
import io.github.edmondantes.simple.permissions.data.storage.PermissionStorage
import io.github.edmondantes.simple.permissions.data.storage.impl.EmptyGroupReplacer
import io.github.edmondantes.simple.permissions.manager.PermissionManager
import io.github.edmondantes.simple.permissions.tree.PermissionTree
import io.github.edmondantes.simple.permissions.tree.impl.DefaultPermissionTree
import io.github.edmondantes.simple.permissions.tree.mapper.PermissionMapperToNodeValue
import io.github.edmondantes.simple.permissions.tree.mapper.impl.DefaultPermissionMapperToNodeValue
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Default implementation of [PermissionManager]. Thread-safe
 * @param permissionGroupStorage object of class [PermissionGroupStorage]
 * @param permissionNodeStorage object of class [PermissionNodeStorage],
 * @param permissionStorage object of class [PermissionStorage]
 * @param groupReplacer object of class [GroupReplacer] (default using [EmptyGroupReplacer])
 * @param permissionMapperToNodeValue object of class [PermissionMapperToNodeValue] (default using [DefaultPermissionMapperToNodeValue])
 * @param defaultGroupName name of group which will be used by default
 * @param superAdminGroupName name of group which will be used for admins
 *
 * @see PermissionManager
 */
class DefaultPermissionManager(
    private val permissionGroupStorage: PermissionGroupStorage,
    private val permissionNodeStorage: PermissionNodeStorage,
    private val permissionStorage: PermissionStorage,
    private val groupReplacer: GroupReplacer = EmptyGroupReplacer(),
    private val permissionMapperToNodeValue: PermissionMapperToNodeValue = DefaultPermissionMapperToNodeValue(),
    defaultGroupName: String? = null,
    superAdminGroupName: String? = null
) : PermissionManager {

    private val readWriteLock = ReentrantReadWriteLock()
    private val groupPermissions: MutableMap<String, PermissionTree> = HashMap()
    private val _groups: MutableSet<String> = HashSet()

    override val adminGroup: String? = superAdminGroupName?.lowercase()

    override val groups: List<String>
        get() = readWriteLock.read {
            ArrayList(_groups)
        }

    override val permissions: Collection<String>
        get() = permissionStorage.permissions

    override val defaultGroup: String?

    init {
        defaultGroup = initDefaultGroupId(defaultGroupName)

        permissionGroupStorage.findAll().forEach {
            it.name.also { name ->
                _groups.add(name.lowercase())
            }
        }
    }

    override fun hasPermission(permission: String): Boolean = permissionStorage.hasPermission(permission)

    override fun grant(groupName: String, permission: String): Boolean {
        val prepareGroupName = groupName.lowercase()
        val preparePermission = permission.lowercase()

        if (prepareGroupName == adminGroup) {
            return true
        }

        if (!permissionStorage.hasPermission(preparePermission)
            || readWriteLock.read { !_groups.contains(prepareGroupName) }
        ) {
            return false
        }

        readWriteLock.write {
            reloadGroup(prepareGroupName)
            val tree = groupPermissions[prepareGroupName] ?: return false
            return try {
                tree.addPermission(preparePermission)
                LOGGER.debug("Successfully added permission '$preparePermission' to group '$prepareGroupName'")
                true
            } catch (e: Exception) {
                LOGGER.warn("Failed to added permission '$preparePermission' to group '$prepareGroupName'")
                false
            } finally {
                reloadGroup(prepareGroupName)
            }
        }
    }

    override fun deprive(groupName: String, permission: String): Boolean {
        val prepareGroupName = groupName.lowercase()
        val preparePermission = permission.lowercase()

        if (prepareGroupName == adminGroup) {
            return false
        }

        if (!permissionStorage.hasPermission(preparePermission)
            || readWriteLock.read { !_groups.contains(prepareGroupName) }
        ) {
            return false
        }

        readWriteLock.write {
            reloadGroup(prepareGroupName)
            val tree = groupPermissions[prepareGroupName] ?: return false
            return try {
                tree.deletePermission(preparePermission)
                LOGGER.debug("Successfully delete permission '$preparePermission' to group '$prepareGroupName'")
                true
            } catch (e: Exception) {
                LOGGER.warn("Failed to delete permission '$preparePermission' to group '$prepareGroupName'")
                false
            } finally {
                reloadGroup(prepareGroupName)
            }
        }
    }

    override fun hasGroup(name: String): Boolean =
        name.lowercase() == adminGroup?.lowercase()
                ||
                readWriteLock.read {
                    groupPermissions.containsKey(name.lowercase())
                }

    override fun group(groupName: String): PermissionOwner? =
        if (groupName.lowercase() == adminGroup?.lowercase()) {
            PermissionOwner.ALL_PERMISSION
        } else {
            readWriteLock.read {
                groupPermissions[groupName.lowercase()]
            }
        }

    override fun groupOrDefault(groupName: String?): PermissionOwner? =
        groupName?.let { group(it) } ?: defaultGroup?.let { group(it) }


    override fun createGroup(name: String): Boolean {
        val prepareGroupName = name.lowercase()

        if (prepareGroupName == adminGroup || prepareGroupName == defaultGroup) {
            return false
        }

        readWriteLock.write {
            reloadGroup(prepareGroupName)
            if (groupPermissions.containsKey(prepareGroupName)) {
                return false
            }

            return try {
                permissionGroupStorage.save(prepareGroupName)
                LOGGER.debug("Successfully created new group with name '$prepareGroupName'")
                true
            } catch (e: Exception) {
                LOGGER.warn("Can not create group with name '$prepareGroupName'", e)
                false
            } finally {
                reloadGroup(prepareGroupName)
            }
        }
    }

    override fun deleteGroup(name: String, forReplace: String?): Boolean {
        val prepareGroupName = name.lowercase()
        val prepareForReplace = forReplace?.lowercase()

        if (prepareGroupName == defaultGroup || prepareGroupName == adminGroup) {
            return false
        }

        readWriteLock.write {
            val replaced = if (prepareForReplace != null) {
                try {
                    groupReplacer.replace(prepareGroupName, prepareForReplace)
                } catch (e: Exception) {
                    LOGGER.warn(
                        "Can not replace group with name '$name' to '$forReplace'." +
                                if (defaultGroup != null) "Will try to replace by default group with name '${defaultGroup}'" else "",
                        e
                    )
                    false
                }
            } else false

            if (!replaced && defaultGroup != null) {
                try {
                    groupReplacer.replace(prepareGroupName, defaultGroup)
                } catch (e: Exception) {
                    LOGGER.error(
                        "Can not replace group with name '$name' to default group with name '${defaultGroup}'",
                        e
                    )
                }
            }

            return try {
                permissionGroupStorage.deleteById(prepareGroupName)
                LOGGER.debug("Successfully deleted group with name '${prepareGroupName}'")
                true
            } catch (e: Exception) {
                LOGGER.warn("Can not delete group with name '${prepareGroupName}'", e)
                false
            } finally {
                reloadGroup(prepareGroupName)
            }
        }
    }

    override fun update(timeout: Long, unit: TimeUnit) {
        val writeLock = readWriteLock.writeLock()
        if (writeLock.tryLock() || writeLock.tryLock(timeout, unit)) {
            try {
                permissionGroupStorage.findAll().forEach { reloadGroup(it) }
            } catch (e: Exception) {
                LOGGER.warn("Can not force update DefaultPermissionManager", e)
            } finally {
                writeLock.unlock()
            }
        }

    }

    private fun initDefaultGroupId(defaultGroupId: String?): String? {
        if (defaultGroupId.isNullOrBlank()) {
            return null
        }

        LOGGER.debug("Using group with name '$defaultGroupId' like default group")
        val group = try {
            permissionGroupStorage.findById(defaultGroupId.lowercase())
        } catch (e: Exception) {
            LOGGER.warn("Can not find group with name '$defaultGroupId'", e)
            return null
        }

        if (group != null) {
            return defaultGroupId
        }

        LOGGER.trace("Can not find group with name '$defaultGroupId' in storage. Will create")

        return try {
            permissionGroupStorage.save(defaultGroupId.lowercase())
            LOGGER.debug("Group with name '$defaultGroupId' was be created")
            defaultGroupId
        } catch (e: Exception) {
            LOGGER.warn("Can not create new group with name '${defaultGroupId.lowercase()}'", e)
            null
        }
    }

    private fun reloadGroup(name: String): Boolean {
        val groupName = name.lowercase()

        readWriteLock.write {
            val group = permissionGroupStorage.findById(groupName) ?: return false
            return reloadGroup(group).also {
                if (!it)
                    _groups.remove(groupName)
            }
        }
    }

    private fun reloadGroup(group: PermissionGroup): Boolean {
        val groupName = group.name.lowercase()

        readWriteLock.write {
            val rootNode = permissionNodeStorage.findById(group.rootNodeId) ?: return false
            groupPermissions[groupName] =
                DefaultPermissionTree(permissionMapperToNodeValue, permissionNodeStorage, rootNode)
            _groups.add(groupName)
            return true
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultPermissionManager::class.java)
    }
}