package me.jerry.example.webmvc.service

import me.jerry.example.webmvc.domain.Product

interface ProductService {

    fun getProduct(id: Long): Product?

    fun getProducts(ids: List<Long>): List<Product>

    fun saveProduct(product: Product): Product

    fun saveExamplesProducts(): List<Product>

}
