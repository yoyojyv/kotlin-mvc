package me.jerry.example.webmvc.repository

import me.jerry.example.webmvc.domain.Product
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface ProductRepository : CrudRepository<Product, Long> {

    @Query("""SELECT * FROM product 
        WHERE supplier_id = :supplierId 
        AND supplier_product_id = :supplierProductId""")
    fun findBySupplierIdAndSupplierProductId(@Param("supplierId") supplierId: Long,
                                             @Param("supplierProductId") supplierProductId: String): Product?

}
