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

package io.github.edmondantes.simple.permissions.tree

import io.github.edmondantes.simple.permissions.data.entity.PermissionNode
import io.github.edmondantes.simple.permissions.data.storage.BaseStorage
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage
import org.junit.jupiter.api.Assertions

class PermissionNodeBuilder(
    private val permissionNodeStorage: PermissionNodeStorage,
    private val parentId: Int? = null
) {
    private val children = ArrayList<PermissionNode>()

    fun add(value: String?, excluded: Boolean = false, func: PermissionNodeBuilder.() -> Unit = {}): Int {
        val node = permissionNodeStorage.save(value, excluded, parentId) {
            PermissionNodeBuilder(permissionNodeStorage, it.id).also(func).build()
        }
        children.add(node)
        return node.id
    }

    fun build(): List<PermissionNode> = children
}

class CheckNodeChildren(
    private val nodeStorage: BaseStorage<Int, PermissionNode>,
    private val node: PermissionNode
) {
    fun exists(
        name: String,
        excluded: Boolean = true,
        childrenCount: Int? = null,
        func: CheckNodeChildren.() -> Unit = {}
    ) {
        val nextNode = node.childrenIds.mapNotNull { nodeStorage.findById(it) }.find { it.value == name }

        Assertions.assertNotNull(
            nextNode,
            "Node with name '$name' and parent with name '${node.value}' and id '${node.id}' is not exists"
        )

        Assertions.assertEquals(
            excluded,
            nextNode!!.excluded,
            "Node with name '$name' and parent with name '${node.value}' and id '${node.id}' have wrong exclude value"
        )
        if (childrenCount != null) {
            Assertions.assertEquals(
                childrenCount,
                nextNode.childrenIds.size,
                "Node with name '$name' and parent with name '${node.value}' and id '${node.id}' have wrong children count"
            )
        }

        func(CheckNodeChildren(nodeStorage, nextNode))
    }

    fun notExists(name: String) {
        val nextNode = node.childrenIds.mapNotNull { nodeStorage.findById(it) }.find { it.value == name }

        Assertions.assertNull(
            nextNode,
            "Node with name '$name' and parent with name '${node.value}' and id '${node.id}' is exists"
        )
    }
}

fun entityNode(
    permissionNodeStorage: PermissionNodeStorage,
    func: PermissionNodeBuilder.() -> Unit
): PermissionNode =
    PermissionNodeBuilder(permissionNodeStorage).also {
        it.add(null, true, func)
    }.build()[0]
