package com.icix.configuration;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories("com.icix.repository")
public class PersistenceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws URISyntaxException, InterruptedException {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource());
        lef.setJpaPropertyMap(this.jpaProperties());
        lef.setJpaVendorAdapter(this.jpaVendorAdapter());
        lef.setPackagesToScan("com.icix.model");

        return lef;
    }

    @Bean
    public Map<String, Object> jpaProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.dialect", PostgreSQL94Dialect.class.getName());
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.hbm2ddl.auto", "validate");

        return properties;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setDatabase(Database.POSTGRESQL);
        return hibernateJpaVendorAdapter;
    }

}
