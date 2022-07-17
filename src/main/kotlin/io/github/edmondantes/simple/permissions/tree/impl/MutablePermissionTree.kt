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
import io.github.edmondantes.simple.permissions.tree.dfs
import io.github.edmondantes.simple.permissions.tree.mapper.PermissionMapperToNodeValue
import io.github.edmondantes.simple.permissions.tree.saveTreeNodeBuilder
import org.slf4j.LoggerFactory

class MutablePermissionTree(
    mapper: PermissionMapperToNodeValue,
    private val permissionNodeStorage: PermissionNodeStorage,
    rootNode: PermissionNode
) : AbstractPermissionTree<MutablePermissionTreeNode, MutablePermissionTreeNode.Builder>(
    mapper,
    permissionNodeStorage,
    rootNode
) {

    override fun createMutationContext(startNode: MutablePermissionTreeNode): PermissionTreeContext<MutablePermissionTreeNode.Builder> {
        return MutablePermissionTreeContext(startNode)
    }

    override fun createNodeBuilder(): MutablePermissionTreeNode.Builder =
        MutablePermissionTreeNode.Builder()


    private inner class MutablePermissionTreeContext(startNode: MutablePermissionTreeNode) :
        AbstractPermissionTreeContext<MutablePermissionTreeNode, MutablePermissionTreeNode.Builder>(startNode) {

        override fun createNodeBuilder(): MutablePermissionTreeNode.Builder =
            this@MutablePermissionTree.createNodeBuilder()

        override fun addNodeTo(node: MutablePermissionTreeNode.Builder, nodeForAdd: MutablePermissionTreeNode.Builder) {
            node.addChild(nodeForAdd.value ?: error("Can not add new node without value"), nodeForAdd)
        }

        override fun remove(id: Int) {
            nodeIds.remove(id)?.dfs {
                nodeIds.remove(id)
                parent?.children?.remove(value)

                try {
                    permissionNodeStorage.deleteById(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not delete node with id '$id'", e)
                }
            }
        }

        override fun close() {
            permissionNodeStorage.saveTreeNodeBuilder(startNode, startNode.parent?.id)
        }

    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MutablePermissionTree::class.java)
    }
}