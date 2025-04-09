package lecture.spring.batch.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
public class DatasourceConfig {
//    @Bean
//    public DataSource dataSourceH2(@Value("${spring.datasource2.url}") String url) {
//        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.driverClassName("org.h2.Driver");
//        dataSourceBuilder.url(url);
//        dataSourceBuilder.username("SA");
//        dataSourceBuilder.password("");
//        return dataSourceBuilder.build();
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManagerH2(@Qualifier("dataSourceH2") DataSource dataSourceH2) {
//        return new DataSourceTransactionManager(dataSourceH2);
//    }
}
