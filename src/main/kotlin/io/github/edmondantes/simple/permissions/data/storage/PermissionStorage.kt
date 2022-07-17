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

package io.github.edmondantes.simple.permissions.data.storage

/**
 * This interface describes an object which store information about possible permissions
 */
interface PermissionStorage {

    /**
     * All possible permissions
     */
    val permissions: Collection<String>

    /**
     * Check if you can use [permission]
     * @return True if you can use [permission] , else false
     */
    fun hasPermission(permission: String): Boolean


}