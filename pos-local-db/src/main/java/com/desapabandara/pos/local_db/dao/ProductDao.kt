package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao: BaseDao<ProductEntity>("ProductEntity") {
    @Query("SELECT * FROM ProductEntity WHERE categoryId = :categoryId")
    abstract fun getProductByCategoryId(categoryId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM ProductEntity WHERE name LIKE '%' || :keyword || '%'")
    abstract fun getProductByKeyword(keyword: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM ProductEntity WHERE id = :id")
    abstract suspend fun getProductById(id: String): ProductEntity?
}