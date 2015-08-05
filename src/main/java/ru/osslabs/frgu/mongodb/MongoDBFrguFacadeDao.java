package ru.osslabs.frgu.mongodb;

import ru.osslabs.frgu.dao.FrguFacade;
import ru.osslabs.frgu.dao.FrguFacadeDao;
import ru.osslabs.frgu.dao.FrguIdentified;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by ikuchmin on 24.07.15.
 */

public abstract class MongoDBFrguFacadeDao<FF extends FrguFacade<PK, T, IPK>, PK, T extends FrguIdentified<IPK>, IPK>
        extends AbstractMongoDBFrguGenericDao<FF, PK> implements FrguFacadeDao<FF, PK, T, IPK> {

    protected MongoDBFrguFacadeDao(MongoDBClientFactory mongoDBClientFactory) {
        super(mongoDBClientFactory);
    }


    @Override
    public T current(PK frguId) {
        return getMongoCollection().find(eq("frgu_id", frguId)).first().getCurrent();
    }

    @Override
    public void rebuild(PK frguId) {

    }

    @Override
    public void rebuild(PK frguId, T object) {

    }

    @Override
    public void rebuildAll() {

    }

    @Override
    public void rebuildAll(List<PK> ids) {
        ids.forEach(this::rebuild);
    }
}