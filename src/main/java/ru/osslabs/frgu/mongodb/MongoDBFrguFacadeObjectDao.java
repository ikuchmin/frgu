package ru.osslabs.frgu.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import ru.osslabs.frgu.domain.FrguFacadeObject;
import ru.osslabs.frgu.domain.FrguFacadeObjectBuilder;
import ru.osslabs.frgu.domain.FrguObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

/**
 * Created by ikuchmin on 03.08.15.
 */
public class MongoDBFrguFacadeObjectDao extends MongoDBFrguFacadeDao<FrguFacadeObject, String, FrguObject, String> {
    private final String COLLECTION = "frgu_facade";

    public MongoDBFrguFacadeObjectDao(MongoDBClientFactory mongoDBClientFactory) {
        super(mongoDBClientFactory);
    }

    @Override
    public void rebuild(String frguId) {
        MongoCollection<FrguObject> objColl = new MongoDBFrguObjectDao(this.getMongoDBClientFactory()).getMongoCollection();
        List<FrguObject> objects = new ArrayList<>();
        ArrayList<String> refs = new ArrayList<>();
        objColl.find(eq("frgu_id", frguId)).into(objects);
        refs.addAll(objects.stream().map(object -> object.getId().get()).collect(Collectors.toList()));
        FrguObject current = objColl.find(eq("frgu_id", frguId)).sort(descending("timestamp")).first();
        if (getMongoCollection().find(eq("frgu_id", frguId)).first() == null) {
            getMongoCollection().insertOne(new FrguFacadeObjectBuilder().
                    withFrguId(frguId).
                    withCurrent(current).
                    withRefs(refs).build());
        }
        else {
            getMongoCollection().findOneAndReplace(
                    eq("frgu_id", frguId),
                    new FrguFacadeObjectBuilder().
                            withFrguId(frguId).
                            withCurrent(current).
                            withRefs(refs).build()
            );
        }
    }

    @Override
    public void rebuild(String frguId, FrguObject object) {
        if (getMongoCollection().find(eq("frgu_id", frguId)).first() == null) {
            ArrayList<String> refs = new ArrayList<>();
            refs.add(object.getId().get());
            getMongoCollection().insertOne(new FrguFacadeObjectBuilder().
                    withFrguId(frguId).
                    withCurrent(object).
                    withRefs(refs).build());
        }
        else {
            getMongoCollection().updateOne(eq("frgu_id", frguId),
                    new BasicDBObject().
                            append("$set", new Document("current", object)).
                            append("$push", new Document("refs", object.getId().get()))
            );
        }
    }

    @Override
    public void rebuildAll() {
        MongoCollection<FrguObject> objColl = new MongoDBFrguObjectDao(this.getMongoDBClientFactory()).getMongoCollection();
        List<String> frguIds = new ArrayList<>();
        objColl.distinct("frgu_id", String.class).into(frguIds);
        //TODO check if possible to update in bulk
        frguIds.forEach(this::rebuild);
    }

    //TODO Test method for performance. Check if always works.
    public void rebuildAll(List<String> ids, List<FrguObject> objects) {
        if (ids.size() == objects.size()) {
            for (int i = 0; i < ids.size(); i++) {
                rebuild(ids.get(i), objects.get(i));
            }
        }
    }

    @Override
    protected MongoCollection<FrguFacadeObject> getMongoCollection() {
        return getMongoDBClientFactory()
                .getDatabase()
                .getCollection(COLLECTION, FrguFacadeObject.class);
    }
}
