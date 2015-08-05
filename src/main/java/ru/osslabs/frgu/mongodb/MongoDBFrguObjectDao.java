package ru.osslabs.frgu.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.codecs.configuration.CodecRegistries;
import ru.osslabs.frgu.dao.FrguObjectDao;
import ru.osslabs.frgu.domain.FrguObject;
import ru.osslabs.frgu.mongodb.codecs.FrguObjectCodec;

import java.util.List;

import static com.mongodb.client.model.Filters.gt;
import static javaslang.collection.List.ofAll;

/**
 * Created by ikuchmin on 24.07.15.
 */
public class MongoDBFrguObjectDao extends AbstractMongoDBFrguGenericDao<FrguObject, String>
        implements FrguObjectDao {

    private final String COLLECTION = "frgu_objects";

    public MongoDBFrguObjectDao(MongoDBClientFactory mongoDBClientFactory) {
        super(mongoDBClientFactory);
    }

    @Override
    public List<FrguObject> changes(long from) {
        FindIterable<FrguObject> iterable = getMongoCollection().find(gt("timestamp", from));
        return ofAll(iterable).toJavaList();
    }

    protected MongoCollection<FrguObject> getMongoCollection() {
        return getMongoDBClientFactory()
                .getDatabase()
                .getCollection(COLLECTION, FrguObject.class);
    }
}
