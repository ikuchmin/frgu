package ru.osslabs.frgu.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import ru.osslabs.frgu.dao.FrguGenericDao;
import ru.osslabs.frgu.dao.FrguIdentified;
import ru.osslabs.frgu.dao.PersistenceException;
import ru.osslabs.frgu.domain.FrguObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;
import static javaslang.collection.List.ofAll;

/**
 * Created by ikuchmin on 24.07.15.
 */
public abstract class AbstractMongoDBFrguGenericDao<T extends FrguIdentified<PK>, PK> implements FrguGenericDao<T, PK> {
    private final MongoDBClientFactory mongoDBClientFactory;

    protected AbstractMongoDBFrguGenericDao(MongoDBClientFactory mongoDBClientFactory) {
        this.mongoDBClientFactory = mongoDBClientFactory;
    }

    @Override
    public T read(PK id) throws PersistenceException {
        FindIterable<T> iterable = getMongoCollection()
                .find(eq("frgu_id", id));
        return iterable.first();
    }

    @Override
    public List<T> readAll() throws PersistenceException {
        FindIterable<T> iterable = getMongoCollection()
                .find();

        return ofAll(iterable).toJavaList();
    }

    @Override
    public void create(T object) throws PersistenceException {
        getMongoCollection()
                .insertOne(object);
    }

    @Override
    public void create(List<T> objects) throws PersistenceException {
        getMongoCollection()
                .insertMany(objects);
    }

    @Override
    public void create(T... objects) throws PersistenceException {
        getMongoCollection()
                .insertMany(Arrays.asList(objects));
    }

    @Override
    public void update(T object) throws PersistenceException {
        throw new UnsupportedOperationException("update");
    }

    @Override
    public void delete(T object) throws PersistenceException {
        throw new UnsupportedOperationException("delete");
    }

    public MongoDBClientFactory getMongoDBClientFactory() {
        return mongoDBClientFactory;
    }

    protected abstract MongoCollection<T> getMongoCollection();
}
