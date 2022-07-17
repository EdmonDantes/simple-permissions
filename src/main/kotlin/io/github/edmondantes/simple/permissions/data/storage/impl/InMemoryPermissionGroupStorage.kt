package io.github.edmondantes.simple.permissions.data.storage.impl

import io.github.edmondantes.simple.permissions.data.entity.PermissionGroup
import io.github.edmondantes.simple.permissions.data.entity.impl.DefaultPermissionGroup
import io.github.edmondantes.simple.permissions.data.storage.PermissionGroupStorage
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage

/**
 * Implementation of [PermissionGroupStorage] which save all data in memory
 * @param permissionNodeStorage object of class [PermissionNodeStorage]
 * @see AbstractInMemoryBaseStorage
 */
@Suppress("unused")
class InMemoryPermissionGroupStorage(private val permissionNodeStorage: PermissionNodeStorage) :
    AbstractInMemoryBaseStorage<String, PermissionGroup>(), PermissionGroupStorage {

    override fun save(name: String, rootNodeId: Int?): PermissionGroup {
        val realRootNodeId = try {
            rootNodeId ?: permissionNodeStorage.save(excluded = true).id
        } catch (e: Exception) {
            throw IllegalStateException("Can not create root node for new group with name '$name'", e)
        }

        return DefaultPermissionGroup(name, realRootNodeId).also {
            save(name, it)
        }
    }

}