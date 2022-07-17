/*
 * Copyright (c) 2022. Ilia Loginov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.edmondantes.simple.permissions.tree.impl

import io.github.edmondantes.simple.permissions.tree.PermissionTreeNode
import io.github.edmondantes.simple.permissions.util.PermissionHelper

@Suppress("unused")
class MutablePermissionTreeNode : PermissionTreeNode {
    override var id: Int = -1
    override var value: String? = null
    override var excluded: Boolean = true
    override var parent: MutablePermissionTreeNode? = null
    override var children: MutableMap<String, MutablePermissionTreeNode> = HashMap()

    override val isAllPermissionNode: Boolean
        get() = value == PermissionHelper.ALL_PERMISSION_STRING

    constructor() {}

    constructor(
        id: Int,
        value: String?,
        parent: MutablePermissionTreeNode?,
        children: MutableMap<String, MutablePermissionTreeNode>,
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
        parent: MutablePermissionTreeNode?,
        children: Map<String, PermissionTreeNode.Builder<MutablePermissionTreeNode>>,
        excluded: Boolean
    ) {
        this.id = id
        this.value = value
        this.parent = parent
        this.children = children.mapValues { it.value.also { it.parent = this }.build() }.toMutableMap()
        this.excluded = excluded
    }

    override fun tryToGetAllPermissionNode(): MutablePermissionTreeNode? =
        children[PermissionHelper.ALL_PERMISSION_STRING]

    override fun toBuilder(): Builder = Builder(this)

    class Builder(val node: MutablePermissionTreeNode = MutablePermissionTreeNode()) :
        PermissionTreeNode.Builder<MutablePermissionTreeNode> {
        override var id: Int?
            get() = node.id
            set(value) {
                node.id = value ?: -1
            }
        override var value: String?
            get() = node.value
            set(value) {
                node.value = value
            }
        override var excluded: Boolean?
            get() = node.excluded
            set(value) {
                node.excluded = value ?: true
            }
        override var parent: MutablePermissionTreeNode?
            get() = node.parent
            set(value) {
                node.parent = value
            }
        override val children: MutableMap<String, PermissionTreeNode.Builder<MutablePermissionTreeNode>>
            get() = node.children.mapValues { it.value.toBuilder() }.toMutableMap()

        override fun addChild(name: String, builder: PermissionTreeNode.Builder<MutablePermissionTreeNode>) {
            builder.parent = node
            node.children[name] = builder.build()
        }

        override fun build(): MutablePermissionTreeNode =
            node

    }
}