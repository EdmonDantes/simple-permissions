package io.github.edmondantes.simple.permissions.validator.impl

import io.github.edmondantes.simple.permissions.validator.PermissionValidator
import io.github.edmondantes.simple.permissions.util.PermissionHelper

/**
 * Default implementation for [PermissionValidator]
 */
@Suppress("unused")
class DefaultPermissionValidator : PermissionValidator {

    /**
     * Checks if the [permission] has valid format
     *
     * Permission string is valid if contains only latin lowercase and uppercase alphabet
     * and '*' isn't used or is last part
     *
     * Examples:
     *
     * - Valid:
     *     - `a.b.c`
     *     - `a.b.c.*`
     * - Not valid:
     *     - `a.b.c.`
     *     - `a.*.b.c`
     *     - `12.b.c`
     *     - `a.b.c.*.`
     *     - `a.b.c*`
     */
    override fun validate(permission: String): Boolean =
            PermissionHelper.isValidPermission(permission)
}