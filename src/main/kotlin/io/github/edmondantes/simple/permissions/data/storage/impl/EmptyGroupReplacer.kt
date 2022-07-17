package io.github.edmondantes.simple.permissions.data.storage.impl

import io.github.edmondantes.simple.permissions.data.storage.GroupReplacer

/**
 * Implementation of [GroupReplacer] which does nothing on replace groups
 */
class EmptyGroupReplacer : GroupReplacer {
    override fun replace(old: String, new: String): Boolean = false
}