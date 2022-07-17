package io.github.edmondantes.simple.permissions.tree

import io.github.edmondantes.simple.permissions.PermissionOwner
import io.github.edmondantes.simple.permissions.exception.NotValidPermissionFormatException

/**
 * This interface describes an object which store all permissions and help to manager they.
 */
interface PermissionTree : PermissionOwner {

    /**
     * Add [permission] to this
     * @param permission string represents of permission
     *
     * @throws NotValidPermissionFormatException if [permission] has wrong format
     */
    @Throws(NotValidPermissionFormatException::class)
    fun addPermission(permission: String)

    /**
     * Delete [permission] from this
     * @param permission string represents of permission
     *
     * @throws NotValidPermissionFormatException if [permission] has wrong format
     */
    @Throws(NotValidPermissionFormatException::class)
    fun deletePermission(permission: String)

}