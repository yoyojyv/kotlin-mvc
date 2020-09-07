package me.jerry.example.webmvc.controller

import me.jerry.example.webmvc.domain.Product
import me.jerry.example.webmvc.service.ProductService
import me.jerry.example.webmvc.type.ProductCategoryType
import me.jerry.example.webmvc.type.YesNoType
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
class ProductController(private val productService: ProductService) {

    @GetMapping("/products/{id}")
    fun product(@PathVariable id: Long) = productService.getProduct(id)

    @GetMapping("/products/byIds")
    fun productsByIds(@RequestParam ids: List<Long>) = productService.getProducts(ids)

//    @GetMapping("/products/saveExample")
//    fun saveExampleProducts() = productService.saveExamplesProducts()

    @GetMapping("/products/saveExample")
    fun saveExampleProduct(): Product {
        val p = Product(1L, ProductCategoryType.PROPERTY, 1L,
                "1", 1, "1", YesNoType.Y,
                LocalDateTime.now(), LocalDateTime.now())
        return productService.saveProduct(p)
    }

    @PostMapping("/products")
    fun saveProduct(@RequestBody product: Product): Product {
        return productService.saveProduct(product)
    }

}
