package ru.osslabs.frgu.dao;

import ru.osslabs.frgu.domain.FrguObject;

import java.util.List;

/**
 * Created by ikuchmin on 24.07.15.
 */
public interface FrguObjectDao extends FrguGenericDao<FrguObject, String> {

    List<FrguObject> changes(long from);

}
