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
import io.github.edmondantes.simple.permissions.data.storage.BaseStorage
import io.github.edmondantes.simple.permissions.tree.PermissionTree
import io.github.edmondantes.simple.permissions.tree.PermissionTreeNode
import io.github.edmondantes.simple.permissions.tree.mapper.PermissionMapperToNodeValue
import io.github.edmondantes.simple.permissions.util.PermissionHelper
import java.util.LinkedList

@Suppress("unchecked_cast")
abstract class AbstractPermissionTree<N : PermissionTreeNode, T : PermissionTreeNode.Builder<N>>(
    private val mapper: PermissionMapperToNodeValue,
    nodeStorage: BaseStorage<Int, PermissionNode>,
    rootNode: PermissionNode
) : PermissionTree {

    protected val nodeIds = HashMap<Int, N>()
    protected val root: N

    init {
        val rootBuilder = createNodeBuilder()
        fillBuilder(rootBuilder, nodeStorage, rootNode)
        root = rootBuilder.build()
        registerAllIds(root)
    }

    override fun addPermission(permission: String) {
        val parts = mapper.transform(permission)
        createNodesBy(parts, false)
    }

    override fun deletePermission(permission: String) {
        val parts = mapper.transform(permission)
        createNodesBy(parts, true)
    }

    override fun checkPermission(permission: String): Boolean {
        val parts = PermissionHelper.splitPermissionToPartsOrThrow(permission)
        val (index, node) = findLastExistingNode(parts)
        return (node.isAllPermissionNode || index >= parts.size) && !node.excluded
    }

    //TODO: Rewrite to optimize search permission (search only unique parts)
    override fun checkAllPermission(permissions: List<String>): Boolean = permissions.all { checkPermission(it) }

    protected abstract fun createMutationContext(startNode: N): PermissionTreeContext<T>
    protected abstract fun createNodeBuilder(): T

    protected fun registerAllIds(startNode: N) {
        nodeIds[startNode.id] = startNode
        startNode.children.forEach { (_, child) ->
            registerAllIds(child as N)
        }
    }

    private fun fillBuilder(
        rootBuilder: T,
        nodeStorage: BaseStorage<Int, PermissionNode>,
        rootNode: PermissionNode
    ) {
        val childQueue = LinkedList<Pair<T, Int>>()
        rootBuilder.id = rootNode.id
        rootBuilder.excluded = true
        rootBuilder.value = null
        rootBuilder.parent = null

        rootNode.childrenIds.forEach {
            childQueue.addLast(rootBuilder to it)
        }

        while (childQueue.isNotEmpty()) {
            val (node, childId) = childQueue.pop()
            val child = nodeStorage.findById(childId)
            if (child?.value != null) {
                val childBuilder = createNodeBuilder()
                childBuilder.id = child.id
                childBuilder.value = child.value
                childBuilder.excluded = child.excluded
                node.addChild(child.value!!, childBuilder)
                child.childrenIds.forEach {
                    childQueue.addLast(childBuilder to it)
                }
            }
        }
    }

    private fun findLastExistingNode(parts: List<String>): Pair<Int, N> {
        var index = 0
        var currentNode: N = root

        while (index < parts.size) {
            val part = parts[index]

            val nextNode = currentNode.tryToGetNextNode(part)
            if (nextNode == null) {
                currentNode = currentNode.tryToGetAllPermissionNode() as? N ?: break
                index++
                break
            }

            currentNode = nextNode as N
            index++
        }

        return index to currentNode
    }

    private fun createNodesBy(parts: List<String>, isExcluded: Boolean) {
        var (index, startNode) = findLastExistingNode(parts)

        val context: PermissionTreeContext<T>
        var currentNode: PermissionTreeNode.Builder<*>

        if (startNode.isAllPermissionNode) {
            if (startNode.excluded == isExcluded) {
                context = createMutationContext(startNode)

                context.use {
                    startNode.parent?.children
                        ?.filter { it.key != "*" }
                        ?.mapNotNull { it.value.id }
                        ?.forEach {
                            context.remove(it)
                        }
                }
                return
            }

            context = createMutationContext(
                startNode.parent as? N ?: error(
                    "Can not find parent for start node with value '${startNode.value}' " +
                            "and id '${startNode.id}'"
                )
            )

            val value = parts[index - 1]
            currentNode = context.addNode(value, true)
        } else {
            context = createMutationContext(startNode)
            currentNode = startNode.toBuilder()
        }

        while (index < parts.size) {
            val value = parts[index++]

            currentNode = if (value == PermissionHelper.ALL_PERMISSION_STRING) {
                val prevValue = currentNode.children.values.find { it.value == "*" }

                context.clearChildren()

                if (prevValue?.id != null) {
                    context.addNode(prevValue.id!!, prevValue.value ?: "*", prevValue.excluded ?: true)
                } else {
                    context.addNode("*", isExcluded)
                }
            } else {
                context.addNode(value, if (index >= parts.size) isExcluded else true)
            }
        }
        context.close()
    }
}