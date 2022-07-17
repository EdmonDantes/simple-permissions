package io.github.edmondantes.simple.permissions.data.storage

/**
 * This interface describes an object which store information
 * about objects of class [T] which associated with objects of class [ID]
 *
 * @see PermissionNodeStorage
 * @see PermissionGroupStorage
 */
interface BaseStorage<ID, T> {

    /**
     * Try to find object with [id]
     * @param id id of object
     * @return If found object which associated with [id], else null
     */
    fun findById(id: ID): T?

    /**
     * Try to get all stored objects
     * @return All stored objects
     */
    fun findAll(): List<T>

    /**
     * Delete object with [id]
     * @param id id of object
     */
    fun deleteById(id: ID)

    /**
     * Delete all objects which associated with [ids]
     * @param ids ids of objects
     */
    fun deleteAllByIds(ids: List<ID>)

}