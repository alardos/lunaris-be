package com.alardos.lunaris.core

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.spi.JdbiPlugin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.function.Consumer
import javax.sql.DataSource


@Configuration
class CoreConf {

    @Bean
    fun jdbi(ds: DataSource, jdbiPlugins: MutableList<JdbiPlugin?>, rowMappers: MutableList<RowMapper<*>?>): Jdbi {
        val proxy = TransactionAwareDataSourceProxy(ds)
        val jdbi = Jdbi.create(proxy)
        jdbiPlugins.forEach(Consumer { plugin: JdbiPlugin? -> jdbi.installPlugin(plugin) })
        rowMappers.forEach(jdbi::registerRowMapper)
        return jdbi
    }

    @Bean
    fun encoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}