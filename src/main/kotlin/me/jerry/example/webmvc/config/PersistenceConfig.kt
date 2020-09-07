package me.jerry.example.webmvc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJdbcRepositories(basePackages = ["me.jerry.example.webmvc.repository"])
class PersistenceConfig(private val dataSource: DataSource) : AbstractJdbcConfiguration() {

    @Primary
    @Bean
    fun transactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

//    @Bean("readTransactionManager")
//    fun readTransactionManager(): PlatformTransactionManager {
//        val transactionManager = DataSourceTransactionManager(readDataSource)
//        transactionManager.isEnforceReadOnly = true
//        return DataSourceTransactionManager(readDataSource)
//    }
//
//    @Bean("writeTransactionManager")
//    fun writeTransactionManager(): PlatformTransactionManager {
//        return DataSourceTransactionManager(writeDataSource)
//    }

}
