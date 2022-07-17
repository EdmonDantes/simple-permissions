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

package io.github.edmondantes.simple.permissions.data.entity

import io.github.edmondantes.simple.permissions.tree.PermissionTree
import io.github.edmondantes.simple.permissions.tree.PermissionTreeNode

/**
 * This interface describes an object which store information about permissions node
 * @see PermissionTree
 * @see PermissionTreeNode
 */
interface PermissionNode {
    /**
     * Id of permissions node
     */
    val id: Int

    /**
     * The value of permissions node
     * @see PermissionTreeNode
     */
    val value: String?

    /**
     * It's true if [PermissionTree] can use this node like permission
     */
    val excluded: Boolean

    /**
     * Id of parent permissions node
     */
    val parentId: Int?

    /**
     * Ids of children permissions nodes
     */
    val childrenIds: List<Int>
}