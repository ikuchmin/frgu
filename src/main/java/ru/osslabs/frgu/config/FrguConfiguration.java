package ru.osslabs.frgu.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.osslabs.frgu.mongodb.MongoDBClientFactory;
import ru.osslabs.frgu.mongodb.codecs.FrguFacadeObjectCodec;
import ru.osslabs.frgu.mongodb.codecs.FrguObjectCodec;
import ru.osslabs.frgu.mongodb.codecs.OptionalCodec;
import ru.osslabs.frgu.providers.SoapBuilder;
import ru.osslabs.frgu.services.FrguService;

//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by ikuchmin on 22.06.15.
 */
@Configuration
//@EnableMongoRepositories
public class FrguConfiguration {

    @Bean
    public SoapBuilder builder(){
        return new SoapBuilder("test_code","test_name","test_code","test_name");
    }

    @Bean
    @Autowired
    public FrguService frguClient(SoapBuilder builder) {
        return new FrguService(builder);
    }

    @Bean
    public MongoClient mongoClient() {
        CodecRegistry codecRegistry =
                CodecRegistries.fromRegistries(
                        CodecRegistries.fromCodecs(
                                new FrguObjectCodec(),
                                new FrguFacadeObjectCodec(),
                                new OptionalCodec()),
                                MongoClient.getDefaultCodecRegistry()
                );
        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(codecRegistry).serverSelectionTimeout(10000).build();
        return new MongoClient("localhost", options);
    }

    @Bean
    String dbName() {
        return "frgu";
    }

    @Bean
    @Autowired
    public MongoDBClientFactory mongoFactory(
            MongoClient mongoClient,
            String dbName) {
        return new MongoDBClientFactory(mongoClient, dbName);
    }

}
