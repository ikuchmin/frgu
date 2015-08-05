package ru.osslabs.frgu.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.StringCodec;
import org.bson.codecs.configuration.CodecRegistries;
import ru.osslabs.frgu.mongodb.codecs.FrguFacadeObjectCodec;
import ru.osslabs.frgu.mongodb.codecs.FrguObjectCodec;

/**
 * Created by ikuchmin on 30.07.15.
 */

public class MongoDBClientFactory {

    private MongoClient mongoClient;
    private String databaseName;

    public MongoDBClientFactory(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    public MongoDBClientFactory(String databaseName) {
        this(new MongoClient("localhost", MongoClientOptions.builder()
                        .serverSelectionTimeout(10000)
                        .build()),
                databaseName);
    }

    public MongoDBClientFactory() {
    }

    public MongoClient getClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return mongoClient.getDatabase(databaseName);
//                .withCodecRegistry(
//                    CodecRegistries.fromCodecs(
//                            new FrguObjectCodec(), //Для объектов
//                            new FrguFacadeObjectCodec(), //Для фасадов
//                            new StringCodec(), //Для фильтров по id
//                            new DocumentCodec() //Для update
//                    )
//                );
    }
}
