package io.github.edmondantes.simple.permissions.tree.mapper.impl

import io.github.edmondantes.simple.permissions.tree.mapper.PermissionMapperToNodeValue
import io.github.edmondantes.simple.permissions.util.PermissionHelper

class DefaultPermissionMapperToNodeValue : PermissionMapperToNodeValue {
    override fun transform(permission: String): List<String> =
        PermissionHelper.splitPermissionToPartsOrThrow(permission)
}