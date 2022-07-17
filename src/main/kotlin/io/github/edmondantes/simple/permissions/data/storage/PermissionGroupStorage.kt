package io.github.edmondantes.simple.permissions.data.storage

import io.github.edmondantes.simple.permissions.data.entity.PermissionGroup

/**
 * This interface describes an object with store object of class [PermissionGroup]
 * @see BaseStorage
 */
interface PermissionGroupStorage : BaseStorage<String, PermissionGroup> {
    /**
     * Save or update a group with [name]
     * @param name name of group
     * @param rootNodeId id of root node. If null, method will create it
     * @return created permission group
     */
    fun save(name: String, rootNodeId: Int? = null): PermissionGroup
}