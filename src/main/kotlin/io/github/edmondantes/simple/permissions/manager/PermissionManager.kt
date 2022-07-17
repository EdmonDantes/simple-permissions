package io.github.edmondantes.simple.permissions.manager

import io.github.edmondantes.simple.permissions.PermissionOwner
import io.github.edmondantes.simple.permissions.data.storage.PermissionStorage
import java.util.concurrent.TimeUnit

/**
 * This interface represents an object which manage permissions and their groups
 */
interface PermissionManager : PermissionStorage {

    /**
     * List of all groups' names
     */
    val groups: List<String>

    /**
     * Default group's name
     */
    val defaultGroup: String?

    /**
     * Name of group which was granted max permissions
     */
    val adminGroup: String?

    /**
     * Check if [PermissionManager] has a group with [name]
     * @return If found - true, else false
     */
    fun hasGroup(name: String): Boolean

    /**
     * Try to get [PermissionOwner] for group with [groupName]
     * @param groupName name of permissions group
     * @return If found it returns [PermissionOwner] for group with [groupName], else null
     */
    fun group(groupName: String): PermissionOwner?

    /**
     * Try to get [PermissionOwner] for group with [groupName], or try to return [PermissionOwner] for default group
     * @param groupName name of permissions group
     * @return If found method returns [PermissionOwner] for group with [groupName],
     * else if default group is exists method returns [PermissionOwner] for default group, else null
     */
    fun groupOrDefault(groupName: String?): PermissionOwner?

    /**
     * Grant [permission] to group with [groupName]
     * @param groupName name of permissions group
     * @param permission string representation of permission
     * @return If granted - true, else false
     */
    fun grant(groupName: String, permission: String): Boolean

    /**
     * Deprive [permission] from group with [groupName]
     * @param groupName name of permissions group
     * @param permission string representation of permission
     * @return If deprived - true, else false
     */
    fun deprive(groupName: String, permission: String): Boolean

    /**
     * Create new group with [name]
     * @param name name of permissions group
     * @return If created - true, else false
     */
    fun createGroup(name: String): Boolean

    /**
     * Delete group with [name]
     * @param name name of permissions group
     * @return If deleted - true, else false
     */
    fun deleteGroup(name: String, forReplace: String?): Boolean

    /**
     * Force update data about permissions and permissions groups
     * @param timeout timeout
     * @param unit timeouts unit
     */
    fun update(timeout: Long = 5, unit: TimeUnit = TimeUnit.SECONDS)
}