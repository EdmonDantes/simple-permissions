package io.github.edmondantes.simple.permissions.validator

/**
 * This object helps to check if the permission has valid format
 */
interface PermissionValidator {

    /**
     * Checks if the [permission] has valid format
     */
    fun validate(permission: String): Boolean
}