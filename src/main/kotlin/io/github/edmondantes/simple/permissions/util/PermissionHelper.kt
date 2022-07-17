package io.github.edmondantes.simple.permissions.util

import io.github.edmondantes.simple.permissions.exception.NotValidPermissionFormatException

internal object PermissionHelper {
    internal const val SPLIT_STRING = "."
    internal const val ALL_PERMISSION_STRING = "*"

    internal fun splitPermissionToParts(str: String): List<String>? =
            if (str.isBlank()) null else str.split(SPLIT_STRING)

    internal fun splitPermissionToPartsOrThrow(str: String): List<String> =
            splitPermissionToParts(str)?.also {
                throwIfIsNotValidParts(it)
            } ?: throw NotValidPermissionFormatException("Permission string can not be empty or blank")

    internal fun isValidPermission(permission: String): Boolean = splitPermissionToParts(permission)
            ?.let { isValidParts(it) }
            ?: false

    internal fun isValidParts(parts: List<String>): Boolean {
        parts.forEachIndexed { partIndex, part ->
            if (part.isBlank() || part == ALL_PERMISSION_STRING && partIndex != parts.lastIndex) {
                return false
            } else if (part == ALL_PERMISSION_STRING) {
                if (partIndex != parts.lastIndex) {
                    return false
                }
            } else if (part.find { ch -> ch !in 'a'..'z' && ch !in 'A'..'Z' } != null) {
                return false
            }
        }
        return true
    }

    internal fun throwIfIsNotValidParts(parts: List<String>) {
        if (!isValidParts(parts)) {
            throw NotValidPermissionFormatException.notValidPermission(parts.joinToString(SPLIT_STRING))
        }
    }
}