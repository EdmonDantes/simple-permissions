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
import io.github.edmondantes.simple.permissions.data.storage.impl.InMemoryPermissionNodeStorage
import io.github.edmondantes.simple.permissions.exception.NotValidPermissionFormatException
import io.github.edmondantes.simple.permissions.tree.CheckNodeChildren
import io.github.edmondantes.simple.permissions.tree.entityNode
import io.github.edmondantes.simple.permissions.tree.mapper.impl.DefaultPermissionMapperToNodeValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestDefaultPermissionTree {

    private val permissionNodeStorage = InMemoryPermissionNodeStorage()
    private val mapper = DefaultPermissionMapperToNodeValue()

    @Test
    fun testAddSimplePermissionToEmptyTree() {
        val rootNode = entityNode(permissionNodeStorage) {}
        val tree = DefaultPermissionTree(mapper, permissionNodeStorage, rootNode)

        tree.addPermission("a.b.c")

        val nodes = permissionNodeStorage.findAll()
        assertEquals(4, nodes.size)

        permissionNodeStorage.checkTreeFromStore(rootNode.id) {
            exists("a", childrenCount = 1) {
                exists("b", childrenCount = 1) {
                    exists("c", false, 0) {}
                }
            }
        }
    }

    @Test
    fun testAddPermissionWithAllAndGetConflictInTree() {

        val rootNode = entityNode(permissionNodeStorage) {
            add("a", true) {
                add("b", true) {
                    add("d", true)
                }
                add("c", false)
            }
        }
        val tree = DefaultPermissionTree(mapper, permissionNodeStorage, rootNode)

        tree.addPermission("a.b.*")

        permissionNodeStorage.checkTreeFromStore(rootNode.id) {
            exists("a", childrenCount = 2) {
                exists("b", childrenCount = 1) {
                    notExists("d")
                    exists("*", false, 0)
                }
                exists("c", false, 0)
            }
        }
    }

    @Test
    fun testAddSimplePermissionToTreeWithIgnoreAll() {
        val rootNode = entityNode(permissionNodeStorage) {
            add("a", true) {
                add("b", true) {
                    add("*", true)
                    add("f", false)
                }
                add("c", false)
            }
        }
        val tree = DefaultPermissionTree(mapper, permissionNodeStorage, rootNode)

        tree.addPermission("a.b.d.e")

        permissionNodeStorage.checkTreeFromStore(rootNode.id) {
            exists("a", childrenCount = 2) {
                exists("b", childrenCount = 3) {
                    exists("*", true, 0)
                    exists("f", false, 0)

                    exists("d", childrenCount = 1) {
                        exists("e", false, 0)
                    }
                }
                exists("c", false, 0)
            }
        }
    }

    @Test
    fun testWrongPermissions() {
        val tree = DefaultPermissionTree(mapper, permissionNodeStorage, entityNode(permissionNodeStorage) {})

        fun checkPermissionStr(permission: String) {
            assertThrows<NotValidPermissionFormatException>("Not valid permission format for '$permission'") {
                tree.addPermission(permission)
            }
        }

        checkPermissionStr("")
        checkPermissionStr("a.b.c.4")
        checkPermissionStr("6.g")
        checkPermissionStr("*.w")
        checkPermissionStr("ad5.*")
        checkPermissionStr("s.**")
        checkPermissionStr("abcd.*.as")

    }


    private fun BaseStorage<Int, PermissionNode>.checkTreeFromStore(rootId: Int, func: CheckNodeChildren.() -> Unit) {
        val rootNode = findById(rootId)

        assertNotNull(rootNode)
        assertNull(rootNode!!.value)
        assertTrue(rootNode.excluded)

        func(CheckNodeChildren(this, rootNode))
    }
}

