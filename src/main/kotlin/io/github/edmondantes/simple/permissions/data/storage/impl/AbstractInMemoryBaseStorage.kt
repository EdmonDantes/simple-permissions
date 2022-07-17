package io.github.edmondantes.simple.permissions.data.storage.impl

import io.github.edmondantes.simple.permissions.data.storage.BaseStorage
import java.util.concurrent.ConcurrentHashMap

/**
 * Common implementation of [BaseStorage] which store data in application memory
 */
abstract class AbstractInMemoryBaseStorage<ID : Any, T> : BaseStorage<ID, T> {

    protected val storage = ConcurrentHashMap<ID, T>()

    override fun findById(id: ID): T? =
        storage[id]

    override fun findAll(): List<T> =
        storage.values.toList()

    override fun deleteById(id: ID) {
        storage.remove(id)
    }

    override fun deleteAllByIds(ids: List<ID>) {
        ids.forEach {
            deleteById(it)
        }
    }

    /**
     * Save or update [obj] in storage with [id]
     * @param id id of object
     * @param obj object for saving
     */
    protected fun save(id: ID, obj: T) {
        storage[id] = obj
    }
}