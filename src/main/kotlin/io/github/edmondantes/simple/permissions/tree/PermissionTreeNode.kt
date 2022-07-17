package io.github.edmondantes.simple.permissions.tree

import io.github.edmondantes.simple.permissions.data.entity.PermissionNode
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage


interface PermissionTreeNode {

    val id: Int
    val value: String?
    val excluded: Boolean

    val parent: PermissionTreeNode?
    val children: Map<String, PermissionTreeNode>

    val isAllPermissionNode: Boolean

    fun tryToGetNextNode(node: String): PermissionTreeNode? =
        children[node]

    fun tryToGetAllPermissionNode(): PermissionTreeNode?

    fun toBuilder(): Builder<out PermissionTreeNode>

    interface Builder<T : PermissionTreeNode> {
        var id: Int?
        var value: String?
        var excluded: Boolean?
        var parent: T?
        val children: MutableMap<String, out Builder<T>>

        fun addChild(name: String, builder: Builder<T>)
        fun build(): T
    }
}

@Suppress("unchecked_cast")
fun <T : PermissionTreeNode> T.dfs(func: T.() -> Unit) {
    children.forEach { (_, node) ->
        (node as T).dfs(func)
    }

    func()
}

internal fun PermissionNodeStorage.saveTreeNodeBuilder(
    builder: PermissionTreeNode.Builder<*>,
    parentId: Int?
): PermissionNode {
    return if (builder.id != null && builder.id!! > 0) {
        update(builder.id!!, builder.value, builder.excluded, parentId) { parent ->
            builder.children.map { (_, child) ->
                saveTreeNodeBuilder(child, parent.id)
            }
        } ?: error("Can not update node with id '${builder.id}'")
    } else {
        save(builder.value, builder.excluded ?: true, parentId) { parent ->
            builder.children.map { (_, child) ->
                saveTreeNodeBuilder(child, parent.id)
            }
        }
    }
}