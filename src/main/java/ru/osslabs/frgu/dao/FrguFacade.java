package ru.osslabs.frgu.dao;

import java.util.ArrayList;

/**
 * Created by ikuchmin on 03.08.15.
 */
public interface FrguFacade<PK, T extends FrguIdentified<IPK>, IPK> extends FrguIdentified<PK> {
    String getFrguId();

    T getCurrent();

    ArrayList<IPK> getRefs();

    void addRef(IPK newRef);
}
