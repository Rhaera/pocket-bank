package com.github.rhaera.project.pocketbank.config.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;

import lombok.NonNull;

import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    public @NonNull String getDatabaseName() {
        return "Clients";
    }

    @Override
    public @NonNull MongoClient mongoClient() {
        return MongoClients.create(MongoClientSettings.builder()
                            .readPreference(ReadPreference.primaryPreferred())
                            .retryWrites(true)
                            .applyConnectionString(new ConnectionString("mongodb+srv://admin:admin@contaspix.hqbpdxt.mongodb.net/?retryWrites=true&w=majority"))
                            .applyToConnectionPoolSettings(connectionPoolSettings -> connectionPoolSettings.minSize(5)
                                                                                                    .maxSize(200)
                                                                                                    .maxConnectionIdleTime(0, TimeUnit.MILLISECONDS))
                            .applyToSocketSettings(socketSettings -> socketSettings.connectTimeout(10, TimeUnit.MINUTES)
                                                                                    .readTimeout(10, TimeUnit.MINUTES))
                            .applyToSslSettings(sslSettings -> sslSettings.enabled(true))
                            .build());
    }

}
