package ru.osslabs.frgu.dao;

import java.util.Optional;

/**
 * Created by ikuchmin on 24.07.15.
 */
public interface FrguIdentified<PK> {

    Optional<PK> getId();
}
