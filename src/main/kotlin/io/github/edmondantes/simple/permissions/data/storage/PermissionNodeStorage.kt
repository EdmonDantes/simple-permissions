package io.github.edmondantes.simple.permissions.data.storage

import io.github.edmondantes.simple.permissions.data.entity.PermissionNode

/**
 * This interface describes an object with store object of class [PermissionNode]
 * @see BaseStorage
 */
interface PermissionNodeStorage : BaseStorage<Int, PermissionNode> {

    /**
     * Save a new [PermissionNode]
     * @param value value of [PermissionNode]
     * @param excluded excluded of [PermissionNode]
     * @param parentId parentId of [PermissionNode]
     * @param childrenIds children of [PermissionNode]
     *
     * @return a new [PermissionNode]
     */
    fun save(
        value: String? = null,
        excluded: Boolean,
        parentId: Int? = null,
        childrenIds: List<Int> = emptyList()
    ): PermissionNode

    /**
     * Save a new [PermissionNode]
     * @param value value of [PermissionNode]
     * @param excluded excluded of [PermissionNode]
     * @param parentId parentId of [PermissionNode]
     * @param childrenBuilder builder for children
     *
     * @return a new [PermissionNode]
     */
    fun save(
        value: String? = null,
        excluded: Boolean,
        parentId: Int? = null,
        childrenBuilder: PermissionNodeStorage.(PermissionNode) -> List<PermissionNode>
    ): PermissionNode


    /**
     * Update a [PermissionNode]
     * @param id id of [PermissionNode]
     * @param value value of [PermissionNode]
     * @param excluded excluded of [PermissionNode]
     * @param parentId parentId of [PermissionNode]
     * @param childrenIds children of [PermissionNode]
     *
     * @return If [PermissionNode] with [id] exists, returns updated [PermissionNode], else returns null
     */
    fun update(
        id: Int, value: String? = null,
        excluded: Boolean? = null,
        parentId: Int? = null,
        childrenIds: List<Int>? = null
    ): PermissionNode?

    /**
     * Update a [PermissionNode]
     * @param id id of [PermissionNode]
     * @param value value of [PermissionNode]
     * @param excluded excluded of [PermissionNode]
     * @param parentId parentId of [PermissionNode]
     * @param childrenBuilder builder for children
     *
     * @return If [PermissionNode] with [id] exists, returns updated [PermissionNode], else returns null
     */
    fun update(
        id: Int, value: String? = null,
        excluded: Boolean? = null,
        parentId: Int? = null,
        childrenBuilder: PermissionNodeStorage.(PermissionNode) -> List<PermissionNode>? = { null }
    ): PermissionNode?
}
