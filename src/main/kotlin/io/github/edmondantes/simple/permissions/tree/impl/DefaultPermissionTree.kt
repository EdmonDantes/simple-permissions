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

import io.github.edmondantes.simple.permissions.data.entity.PermissionNode
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage
import io.github.edmondantes.simple.permissions.tree.mapper.PermissionMapperToNodeValue
import io.github.edmondantes.simple.permissions.tree.saveTreeNodeBuilder

class DefaultPermissionTree(
    mapper: PermissionMapperToNodeValue,
    private val permissionNodeStorage: PermissionNodeStorage,
    rootNode: PermissionNode
) : AbstractPermissionTree<DefaultPermissionTreeNode, DefaultPermissionTreeNode.Builder>(
    mapper,
    permissionNodeStorage,
    rootNode
) {

    override fun createMutationContext(startNode: DefaultPermissionTreeNode): PermissionTreeContext<DefaultPermissionTreeNode.Builder> {
        return DefaultPermissionTreeContext(startNode)
    }

    override fun createNodeBuilder(): DefaultPermissionTreeNode.Builder =
        DefaultPermissionTreeNode.Builder()

    private inner class DefaultPermissionTreeContext(startNode: DefaultPermissionTreeNode) :
        AbstractPermissionTreeContext<DefaultPermissionTreeNode, DefaultPermissionTreeNode.Builder>(startNode) {

        private val forDelete = ArrayList<Int>()

        override fun createNodeBuilder(): DefaultPermissionTreeNode.Builder =
            this@DefaultPermissionTree.createNodeBuilder()

        override fun addNodeTo(node: DefaultPermissionTreeNode.Builder, nodeForAdd: DefaultPermissionTreeNode.Builder) {
            node.addChild(nodeForAdd.value ?: error("Can not add new node without value"), nodeForAdd)
            if (nodeForAdd.id != null) {
                forDelete.add(nodeForAdd.id!!)
            }
        }

        override fun remove(id: Int) {
            forDelete.add(id)
        }

        override fun close() {
            permissionNodeStorage.deleteAllByIds(forDelete)
            permissionNodeStorage.saveTreeNodeBuilder(startNode, startNode.parent?.id)
        }

    }
}