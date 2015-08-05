package ru.osslabs.frgu.mongodb;

import ru.osslabs.frgu.dao.AbstractFrguDaoFactory;
import ru.osslabs.frgu.dao.FrguGenericDao;
import ru.osslabs.frgu.domain.AbstractFrguFacade;
import ru.osslabs.frgu.domain.FrguObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by ikuchmin on 24.07.15.
 */
public class MongoDBFrguDaoFactory extends AbstractFrguDaoFactory<MongoDBClientFactory> {

    private final Map<Class, Function<MongoDBClientFactory, FrguGenericDao>> creators;

    public MongoDBFrguDaoFactory(MongoDBClientFactory mongoDBClientFactory) {
        super(mongoDBClientFactory);

        creators = new HashMap<>();
        creators.put(FrguObject.class, MongoDBFrguObjectDao::new);
        creators.put(AbstractFrguFacade.class, MongoDBFrguFacadeObjectDao::new);
    }

    @Override
    protected Map<Class, Function<MongoDBClientFactory, FrguGenericDao>> getCreators() {
        return creators;
    }
}
