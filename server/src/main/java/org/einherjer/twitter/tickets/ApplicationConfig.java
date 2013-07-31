package org.einherjer.twitter.tickets;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackageClasses = ApplicationConfig.class)
@EnableJpaRepositories
@EnableTransactionManagement
public class ApplicationConfig {

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.HSQL).build();

        //For external access (e.g. Squirrel) HSQL has to be started in a separate process
        //(java -cp /Users/einherjer/.m3/repository/org/hsqldb/hsqldb/2.2.9/hsqldb-2.2.9.jar org.hsqldb.Server --database.0 mem:test --dbname.0 test)
        //        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        //        try {
        //            dataSource.setDriverClass("org.hsqldb.jdbcDriver");
        //        }
        //        catch (PropertyVetoException e) {
        //            throw new RuntimeException("Unexpected error", e);
        //        }
        //        dataSource.setJdbcUrl("jdbc:hsqldb:mem:test");
        //        dataSource.setUser("sa");
        //        dataSource.setPassword("");
        //        dataSource.setAcquireIncrement(1);
        //        dataSource.setMaxPoolSize(1);
        //        dataSource.setMinPoolSize(1);
        //        dataSource.setMaxStatements(0);
        //        dataSource.setIdleConnectionTestPeriod(100);
        //        return dataSource;
    }

	@Bean
    public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.HSQL);
		vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan(getClass().getPackage().getName());
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.show_sql", Boolean.TRUE.toString());
        jpaProperties.setProperty("hbm2ddl.auto", "create-drop");
        factory.setJpaProperties(jpaProperties);
		factory.setDataSource(dataSource());

		factory.afterPropertiesSet();

		return factory.getObject();
	}

	@Bean
	public JpaDialect jpaDialect() {
		return new HibernateJpaDialect();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}

    @Bean
    public ServiceLocator serviceLocator() {
        return ServiceLocator.getInstance();
    }
}
