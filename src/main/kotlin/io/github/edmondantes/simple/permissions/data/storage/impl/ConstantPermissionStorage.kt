package io.github.edmondantes.simple.permissions.data.storage.impl

import io.github.edmondantes.simple.permissions.data.storage.PermissionStorage

/**
 * Implementation of [PermissionStorage] which store constant information about permissions
 * @param permissions List of possible permissions. It will be coped.
 */
class ConstantPermissionStorage(permissions: List<String>) : PermissionStorage {
    override val permissions: Collection<String> = HashSet(permissions)
    override fun hasPermission(permission: String): Boolean = permissions.contains(permission)
}