
package io.pivotal.pal.tracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.sql.DataSource;

@SpringBootApplication
public class PalTrackerApplication {
    @Autowired DataSource dataSource;
    @Autowired
    Logger logger;

    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);
    }

    @Bean
    Logger logger() {
        return LoggerFactory.getLogger(PalTrackerApplication.class);
    }

    @Bean
    @ConditionalOnProperty(name = "repositoryType", havingValue = "jdbc")
    TimeEntryRepository jdbcTimeEntryRepository() {
        logger.info("Using JDBC repository");
        return new JdbcTimeEntryRepository(dataSource);
    }

    @Bean
    @ConditionalOnProperty(name = "repositoryType", havingValue = "jpa")
    TimeEntryRepository jpaTimeEntryRepository(@Autowired TimeEntryRecordRepository recordRepository) {
        logger.info("Using JPA repository");
        return new JpaTimeEntryRepository(recordRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "repositoryType", havingValue = "memory", matchIfMissing = true)
    TimeEntryRepository inMemoryTimeEntryRepository() {
        logger.info("Using in-memory repository");
        return new InMemoryTimeEntryRepository();
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}