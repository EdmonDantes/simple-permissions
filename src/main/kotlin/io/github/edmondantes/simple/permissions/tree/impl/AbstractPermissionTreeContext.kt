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

abstract class AbstractPermissionTreeContext<T : PermissionTreeNode, B : PermissionTreeNode.Builder<T>>(
    startNode: T
) : PermissionTreeContext<B> {

    @Suppress("unchecked_cast")
    protected val startNode: B = startNode.toBuilder() as B
    protected var currentNode: B = this.startNode

    override fun addNode(id: Int, value: String, excluded: Boolean): B {
        val builder = createNodeBuilder()
        builder.id = id
        builder.value = value
        builder.excluded = excluded

        addNodeTo(currentNode, builder)
        currentNode = builder

        return builder
    }

    override fun addNode(value: String, excluded: Boolean): B {
        val builder = createNodeBuilder()
        builder.value = value
        builder.excluded = excluded

        addNodeTo(currentNode, builder)
        currentNode = builder

        return builder
    }

    override fun clearChildren() {
        val list = ArrayList(currentNode.children.values)
        currentNode.children.clear()

        list.forEach {
            if (it.id != null) {
                remove(it.id!!)
            }
        }
    }

    protected abstract fun createNodeBuilder(): B
    protected abstract fun addNodeTo(node: B, nodeForAdd: B)
}