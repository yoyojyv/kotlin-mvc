package me.jerry.example.webmvc.service.impl

import me.jerry.example.webmvc.domain.Product
import me.jerry.example.webmvc.repository.ProductRepository
import me.jerry.example.webmvc.service.ProductService
import me.jerry.example.webmvc.type.ProductCategoryType
import me.jerry.example.webmvc.type.YesNoType
import org.slf4j.Logger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class ProductServiceImpl(private val productRepository: ProductRepository, private val logger: Logger) : ProductService {

    @Transactional(readOnly = true)
    override fun getProduct(id: Long): Product? {

        logger.info("getProduct(). TransactionSynchronizationManager.getCurrentTransactionName() : " + TransactionSynchronizationManager.getCurrentTransactionName())
        logger.info("> isActualTransactionActive: ${TransactionSynchronizationManager.isActualTransactionActive()}")
        logger.info("> isCurrentTransactionReadOnly: ${TransactionSynchronizationManager.isCurrentTransactionReadOnly()}")
//        return productRepository.findById(id).orElse(null)
        return productRepository.findByIdOrNull(id)
    }

    override fun getProducts(ids: List<Long>): List<Product> {
        return productRepository.findAllById(ids) as List<Product>
    }

    @Transactional
    override fun saveProduct(product: Product): Product {

        logger.info("saveProduct(). TransactionSynchronizationManager.getCurrentTransactionName() : " + TransactionSynchronizationManager.getCurrentTransactionName())
        logger.info("> isActualTransactionActive: ${TransactionSynchronizationManager.isActualTransactionActive()}")
        logger.info("> isCurrentTransactionReadOnly: ${TransactionSynchronizationManager.isCurrentTransactionReadOnly()}")

//        val exist = productRepository.findBySupplierIdAndSupplierProductId(supplierId = product.supplierId,
//                supplierProductId = product.supplierProductId)
//        if (exist?.id != null) {
//            return exist
//        }
//        product.id = null
//        return productRepository.save(product)

        val exist = if (product.id != null) productRepository.findByIdOrNull(product.id!!) else null
        if (exist?.id != null) {
            return exist
        }
        product.id = null
        return productRepository.save(product)
    }

    @Transactional
    override fun saveExamplesProducts(): List<Product> {
        return (1..10)
                .map {
                    Product(null, ProductCategoryType.PROPERTY, 1L,
                            it.toString(), (it % 6 + 1).toLong(), it.toString(), YesNoType.Y,
                            LocalDateTime.now(), LocalDateTime.now())
                }
                .map { p ->
                    productRepository.findBySupplierIdAndSupplierProductId(supplierId = p.supplierId,
                            supplierProductId = p.supplierProductId) ?: productRepository.save(p)
                }
    }

}
