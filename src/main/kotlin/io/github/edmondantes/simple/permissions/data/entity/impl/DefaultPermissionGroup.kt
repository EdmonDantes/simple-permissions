package io.github.edmondantes.simple.permissions.data.entity.impl

import io.github.edmondantes.simple.permissions.data.entity.PermissionGroup

/**
 * Default implementation of [PermissionGroup]
 */
class DefaultPermissionGroup(override val name: String, override val rootNodeId: Int) : PermissionGroup {
    override fun toString(): String {
        return "DefaultPermissionGroup(name='$name', rootNodeId=$rootNodeId)"
    }
}