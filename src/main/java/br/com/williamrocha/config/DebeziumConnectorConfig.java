package br.com.williamrocha.config;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DebeziumConnectorConfig {

    @Bean
    public io.debezium.config.Configuration customerConnector(Environment env) throws IOException {
        var offsetStorageTempFile = File.createTempFile("offsets_", ".dat");
        var dbHistoryTempFile = File.createTempFile("dbhistory_", ".dat");
        return io.debezium.config.Configuration.create()
                .with("name", "store_mysql_connector")
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("tasks.max","1")
                .with("database.server.id", env.getProperty("debezium.database.server.id"))
                .with("database.hostname", env.getProperty("debezium.database.hostname"))
                .with("database.port", env.getProperty("debezium.database.port")) //defaults to 3306
                .with("database.dbname", env.getProperty("debezium.database.dbname"))
                .with("database.user", env.getProperty("debezium.database.user"))
                .with("database.password", env.getProperty("debezium.database.password"))
                .with("topic.prefix","dbserver1")
                .with("schema.history.internal.kafka.bootstrap.servers","127.0.0.1:9092")
                .with("schema.history.internal.kafka.topic","schema-changes.inventory")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
                .with("table.include.list","storeDB.customer,storeDB.product")
                .with("column.include.list","storeDB.customer.*,storeDB.product.(id|price)")
            .build();
    }
}
