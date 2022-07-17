package io.github.edmondantes.simple.permissions.tree.mapper

import io.github.edmondantes.simple.permissions.exception.NotValidPermissionFormatException

interface PermissionMapperToNodeValue {

    @Throws(NotValidPermissionFormatException::class)
    fun transform(permission: String): List<String>

}