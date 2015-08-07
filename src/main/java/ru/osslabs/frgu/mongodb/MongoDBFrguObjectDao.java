package ru.osslabs.frgu.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;
import ru.osslabs.frgu.dao.FrguObjectDao;
import ru.osslabs.frgu.dao.PersistenceException;
import ru.osslabs.frgu.domain.FrguObject;
import ru.osslabs.frgu.mongodb.codecs.FrguObjectCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Filters.eq;
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
    public FrguObject read(String id) throws PersistenceException {
        return  getMongoCollection().find(eq("_id", new ObjectId(id))).first();
    }

    @Override
    public List<FrguObject> changes(long from) {
        FindIterable<FrguObject> iterable = getMongoCollection().find(gt("timestamp", from));
        return ofAll(iterable).toJavaList();
    }

    public List<String> changesIds(long from) {
        List<String> result = new ArrayList<>();
        getMongoCollection().find(gt("timestamp", from)).forEach(
                (Block<FrguObject>) frguObject ->
                        result.add(frguObject.getFrguId())
        );
        return result;
    }

    public List<String> getIds() {
        List<String> result = new ArrayList<>();
        getMongoCollection().distinct("frgu_id", String.class).into(result);
        return result;
    }

    protected MongoCollection<FrguObject> getMongoCollection() {
        return getMongoDBClientFactory()
                .getDatabase()
                .getCollection(COLLECTION, FrguObject.class);
    }
}
