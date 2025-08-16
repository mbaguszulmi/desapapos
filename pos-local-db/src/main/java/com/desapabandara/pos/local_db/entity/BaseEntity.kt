package com.desapabandara.pos.local_db.entity

sealed class BaseEntity {
    open var createdAt: Long = System.currentTimeMillis()
    open var deletedAt: Long? = null
    open var updatedAt: Long = System.currentTimeMillis()
}
