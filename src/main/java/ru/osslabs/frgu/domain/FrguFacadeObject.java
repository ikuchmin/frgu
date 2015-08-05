package ru.osslabs.frgu.domain;

import net.karneim.pojobuilder.GeneratePojoBuilder;

import java.util.ArrayList;

/**
 * Created by ikuchmin on 03.08.15.
 */
public class FrguFacadeObject extends AbstractFrguFacade<FrguObject, String> {

    @GeneratePojoBuilder
    public FrguFacadeObject(String id, String frguId, FrguObject current, ArrayList<String> refs) {
        super(id, frguId, current, refs);
    }
}
