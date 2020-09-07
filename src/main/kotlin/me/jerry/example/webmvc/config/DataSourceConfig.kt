package me.jerry.example.webmvc.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Bean("readDataSource")
    @ConfigurationProperties("datasource.read")
    fun readDataSource(): DataSource {
        return DataSourceBuilder.create().type(HikariDataSource::class.java).build()
    }

    @Bean("writeDataSource")
    @ConfigurationProperties("datasource.write")
    fun writeDataSource(): DataSource {
        return DataSourceBuilder.create().type(HikariDataSource::class.java).build()
    }

    /**
     * routingDataSource 를 바로 전달하는 경우 transaction 이 readonly 일때도 write 로 붙음
     * 이때 LazyConnectionDataSourceProxy 로 감싸주면 해결됨
     */
    // @Primary
    @Bean("routingDataSource")
    fun routingDataSource(): DataSource {

        val dataSources = mapOf<Any, Any>(
                DataSourceType.READ to writeDataSource(),
                DataSourceType.WRITE to writeDataSource()
        )

        val routingDataSource = ReplicationRoutingDataSource()
        with(routingDataSource) {
            setTargetDataSources(dataSources)
            setDefaultTargetDataSource(readDataSource())
        }
        return routingDataSource
    }

    @Primary
    @Bean("defaultDataSource")
    fun dataSource(): DataSource {
        return LazyConnectionDataSourceProxy(routingDataSource())
    }

    class ReplicationRoutingDataSource : AbstractRoutingDataSource() {

//        private var dataSources: Map<Any, Any> = HashMap()
//
//        override fun setTargetDataSources(targetDataSources: Map<Any, Any>) {
//            this.dataSources = targetDataSources
//            super.setTargetDataSources(targetDataSources)
//        }

        override fun determineCurrentLookupKey(): DataSourceType {
            val dataSourceType = if (TransactionSynchronizationManager.isActualTransactionActive() && TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                DataSourceType.READ
            } else {
                DataSourceType.WRITE
            }
            logger.info("determineCurrentLookupKey() getCurrentTransactionName: ${TransactionSynchronizationManager.getCurrentTransactionName()}")
            logger.info("determineCurrentLookupKey() isActualTransactionActive: ${TransactionSynchronizationManager.isActualTransactionActive()}")
            logger.info("determineCurrentLookupKey() isCurrentTransactionReadOnly: ${TransactionSynchronizationManager.isCurrentTransactionReadOnly()}")
            logger.info("> current dataSourceType : $dataSourceType")
            return dataSourceType
        }

//        override fun determineTargetDataSource(): DataSource {
//            return dataSources[determineCurrentLookupKey()] as DataSource
//        }

    }

    enum class DataSourceType {
        READ, WRITE
    }

}
