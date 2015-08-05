package ru.osslabs.frgu.dao;


import java.util.List;

public interface FrguFacadeDao<FF extends FrguFacade<PK, T, IPK>, PK, T extends FrguIdentified<IPK>, IPK>
        extends FrguGenericDao<FF, PK> {

    T current(PK frguId);

    void rebuild(PK frguId);

    void rebuild(PK frguId, T object);

    void rebuildAll();

    void rebuildAll(List<PK> ids);

}
