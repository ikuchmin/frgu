package ru.osslabs.frgu.dao;

import java.util.List;

/**
 * Created by ikuchmin on 24.07.15.
 */
public interface FrguGenericDao<T extends FrguIdentified<PK>, PK> {

    /**
     *
     * @param object
     */
    void create(T object) throws PersistenceException;

    /**
     *
     * @param objects
     */
    void create(List<T> objects) throws PersistenceException;

    /**
     *
     * @param objects
     */
    void create(T... objects) throws PersistenceException;

    /**
     *
     * @param id
     * @return
     */
    T read(PK id) throws PersistenceException;

    /**
     *
     */
    List<T> readAll() throws PersistenceException;

    /**
     *
     * @param object
     */
    void update(T object) throws PersistenceException;

    /**
     *
     * @param object
     */
    void delete(T object) throws PersistenceException;
}
