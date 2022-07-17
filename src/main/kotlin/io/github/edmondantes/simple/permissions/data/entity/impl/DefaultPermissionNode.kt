package io.github.edmondantes.simple.permissions.data.entity.impl

import io.github.edmondantes.simple.permissions.data.entity.PermissionNode

/**
 * Default implementation of [PermissionNode]
 */
class DefaultPermissionNode(
    override val id: Int,
    override val value: String?,
    override val excluded: Boolean,
    override val parentId: Int?,
    override val childrenIds: List<Int>
) : PermissionNode {
    override fun toString(): String {
        return "PermissionNode(id=$id, value=$value, excluded=$excluded, parentId=$parentId, childrenIds=$childrenIds)"
    }
}