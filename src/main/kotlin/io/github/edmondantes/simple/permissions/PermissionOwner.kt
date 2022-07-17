package io.github.edmondantes.simple.permissions

import io.github.edmondantes.simple.permissions.exception.NotValidPermissionFormatException

/**
 * This interface represents an object which store information about granted permissions
 */
@Suppress("unused")
interface PermissionOwner {

    /**
     * Check if [permission] is granted, or not
     * @return True if [permission] was granted, else false
     */
    @Throws(NotValidPermissionFormatException::class)
    fun checkPermission(permission: String): Boolean

    /**
     * Check if all [permissions] are granted, or not
     * @return True if all [permissions] are granted, else false
     */
    @Throws(NotValidPermissionFormatException::class)
    fun checkAllPermission(permissions: List<String>): Boolean

    companion object {
        /**
         * This represents [PermissionOwner] that has been granted all permissions
         */
        val ALL_PERMISSION = object : PermissionOwner {
            override fun checkPermission(permission: String): Boolean = true
            override fun checkAllPermission(permissions: List<String>): Boolean = true
        }

        /**
         * This represents [PermissionOwner] that hasn't been granted any permission
         */
        val NO_PERMISSION = object : PermissionOwner {
            override fun checkPermission(permission: String): Boolean = false
            override fun checkAllPermission(permissions: List<String>): Boolean = false
        }
    }
}