package io.github.edmondantes.simple.permissions.tree.impl

import io.github.edmondantes.simple.permissions.tree.PermissionTreeNode
import io.github.edmondantes.simple.permissions.util.PermissionHelper

/**
 * Node for [DefaultPermissionTree]
 */
@Suppress("unused")
class DefaultPermissionTreeNode : PermissionTreeNode {

    override val parent: DefaultPermissionTreeNode?
    override val children: Map<String, DefaultPermissionTreeNode>
    override val id: Int
    override val value: String?
    override val isAllPermissionNode: Boolean
        get() = value == PermissionHelper.ALL_PERMISSION_STRING
    override val excluded: Boolean

    constructor(
        id: Int,
        value: String?,
        parent: DefaultPermissionTreeNode?,
        children: Map<String, DefaultPermissionTreeNode>,
        excluded: Boolean
    ) {
        this.id = id
        this.value = value
        this.parent = parent
        this.children = children
        this.excluded = excluded
    }


    private constructor(
        value: String?,
        id: Int,
        parent: DefaultPermissionTreeNode?,
        children: Map<String, PermissionTreeNode.Builder<DefaultPermissionTreeNode>>,
        excluded: Boolean
    ) {
        this.id = id
        this.value = value
        this.parent = parent
        this.children = children.mapValues { it.value.also { it.parent = this }.build() }
        this.excluded = excluded
    }


    override fun tryToGetNextNode(node: String): PermissionTreeNode? =
        children[node]

    override fun tryToGetAllPermissionNode(): PermissionTreeNode? =
        children[PermissionHelper.ALL_PERMISSION_STRING]

    override fun toBuilder(): Builder = Builder().also {
        it.id = id
        it.value = value
        it.parent = parent
        it.children = children.mapValues { it.value.toBuilder() }.toMutableMap()
        it.excluded = excluded
    }

    class Builder : PermissionTreeNode.Builder<DefaultPermissionTreeNode> {
        override var id: Int? = null
        override var value: String? = null
        override var excluded: Boolean? = false

        override var parent: DefaultPermissionTreeNode? = null
        override var children: MutableMap<String, PermissionTreeNode.Builder<DefaultPermissionTreeNode>> = HashMap()


        override fun addChild(name: String, builder: PermissionTreeNode.Builder<DefaultPermissionTreeNode>) {
            children[name] = builder
        }

        override fun build(): DefaultPermissionTreeNode {
            return DefaultPermissionTreeNode(
                value,
                id ?: error("Id can not be null"),
                parent,
                children,
                excluded ?: error("excluded can not be null"),
            )
        }
    }
}
