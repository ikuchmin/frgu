package ru.osslabs.frgu.dao;

import java.io.Serializable;

/**
 * @author Ilya Kuchmin
 * @since 1.0.0
 */

public interface FrguDaoFactory<T> {
    <GD extends FrguGenericDao<R, PK>, R extends FrguIdentified<PK>, PK extends Serializable> GD getDao(Class<R> dtoClass)
            throws PersistenceException;
}
