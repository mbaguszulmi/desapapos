package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import com.desapabandara.pos.local_db.entity.ProductCategoryEntity

@Dao
abstract class ProductCategoryDao: BaseDao<ProductCategoryEntity>("ProductCategoryEntity")