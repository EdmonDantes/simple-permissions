package io.github.edmondantes.simple.permissions.data.storage.impl

import io.github.edmondantes.simple.permissions.data.entity.PermissionNode
import io.github.edmondantes.simple.permissions.data.entity.impl.DefaultPermissionNode
import io.github.edmondantes.simple.permissions.data.storage.PermissionGroupStorage
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of [PermissionGroupStorage] which save all data in memory
 *
 * @see AbstractInMemoryBaseStorage
 */
@Suppress("unused")
class InMemoryPermissionNodeStorage : AbstractInMemoryBaseStorage<Int, PermissionNode>(), PermissionNodeStorage {

    private val nextId = AtomicInteger(1)

    override fun save(value: String?, excluded: Boolean, parentId: Int?, childrenIds: List<Int>): PermissionNode {
        val id = nextId.getAndIncrement()

        return DefaultPermissionNode(id, value, excluded, parentId, childrenIds).also {
            save(id, it)
        }
    }

    override fun save(
        value: String?,
        excluded: Boolean,
        parentId: Int?,
        childrenBuilder: PermissionNodeStorage.(PermissionNode) -> List<PermissionNode>
    ): PermissionNode {
        val id = nextId.getAndIncrement()

        val children = childrenBuilder(this, DefaultPermissionNode(id, value, excluded, parentId, emptyList()))

        return DefaultPermissionNode(id, value, excluded, parentId, children.map { it.id }).also {
            save(id, it)
        }
    }

    override fun update(
        id: Int,
        value: String?,
        excluded: Boolean?,
        parentId: Int?,
        childrenIds: List<Int>?
    ): PermissionNode? =
        storage.computeIfPresent(id) { _, prevState ->
            if (value == null && excluded == null && parentId == null && childrenIds == null) {
                prevState
            } else {
                DefaultPermissionNode(
                    id,
                    value ?: prevState.value,
                    excluded ?: prevState.excluded,
                    parentId ?: prevState.parentId,
                    childrenIds ?: prevState.childrenIds
                )
            }
        }

    override fun update(
        id: Int,
        value: String?,
        excluded: Boolean?,
        parentId: Int?,
        childrenBuilder: PermissionNodeStorage.(PermissionNode) -> List<PermissionNode>?
    ): PermissionNode? =
        storage.computeIfPresent(id) { _, prevState ->
            if (value == null && excluded == null && parentId == null) {
                prevState
            } else {
                val node = DefaultPermissionNode(
                    id,
                    value ?: prevState.value,
                    excluded ?: prevState.excluded,
                    parentId ?: prevState.parentId,
                    emptyList()
                )

                DefaultPermissionNode(
                    id,
                    value ?: prevState.value,
                    excluded ?: prevState.excluded,
                    parentId ?: prevState.parentId,
                    childrenBuilder(node)?.map { it.id } ?: prevState.childrenIds
                )
            }
        }

}