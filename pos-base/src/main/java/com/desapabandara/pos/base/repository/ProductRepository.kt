package com.desapabandara.pos.base.repository

import com.desapabandara.pos.local_db.dao.ProductCategoryDao
import com.desapabandara.pos.local_db.dao.ProductDao
import com.desapabandara.pos.local_db.entity.ProductCategoryEntity
import com.desapabandara.pos.local_db.entity.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val productCategoryDao: ProductCategoryDao,
) {
    suspend fun saveProducts(products: List<ProductEntity>) {
        productDao.insertMany(products)
    }

    fun getProducts() = productDao.getAll()

    fun getProductsByCategoryId(categoryId: String) = productDao.getProductByCategoryId(categoryId)

    suspend fun saveProductCategories(productCategories: List<ProductCategoryEntity>) {
        productCategoryDao.insertMany(productCategories)
    }

    fun getProductsByKeyword(keyword: String) = productDao.getProductByKeyword(keyword)
}