package com.github.rhaera.project.pocketbank.config.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;

import lombok.NonNull;

import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Override
    public @NonNull String getDatabaseName() {
        return "Clients";
    }

    @Override
    public @NonNull MongoClient mongoClient() {
        return MongoClients.create(
                MongoClientSettings
                        .builder()
                        .applyConnectionString(new ConnectionString("mongodb+srv://admin:admin@contaspix.hqbpdxt.mongodb.net/?retryWrites=true&w=majority"))
                        .build()
                        );
    }
}
