package ru.osslabs.frgu.dao;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;


/**
 * @author Ilya Kuchmin
 * @since 1.0.0
 */

abstract public class AbstractFrguDaoFactory<T> implements FrguDaoFactory<T> {

    private final T factory;

    public AbstractFrguDaoFactory(T factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <GD extends FrguGenericDao<R, PK>, R extends FrguIdentified<PK>, PK extends Serializable> GD getDao(Class<R> dtoClass)
            throws PersistenceException {
        GD dao = null;
        try {
            Map<Class, Function<T, FrguGenericDao>> creators = getCreators();
            if (creators != null) {
                Function<T, FrguGenericDao> tFrguGenericDaoFunction = creators.get(dtoClass);
                if (tFrguGenericDaoFunction != null)
                    dao = (GD) tFrguGenericDaoFunction.apply(factory);

            }
        } catch (Exception exception) {
            throw new PersistenceException(exception);
        }
        return dao;
    }

    protected abstract Map<Class, Function<T, FrguGenericDao>> getCreators();

    public T getFactory() {
        return factory;
    }
}
