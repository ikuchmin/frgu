package ru.osslabs.frgu.domain;

import ru.osslabs.frgu.dao.FrguFacade;
import ru.osslabs.frgu.dao.FrguIdentified;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by ilyalyakin on 24.07.15.
 */
public abstract class AbstractFrguFacade<T extends FrguIdentified<PK>, PK>
        implements FrguFacade<String, T, PK> {

    private Optional<String> id;
    private String frguId;
    private T current;
    private ArrayList<PK> refs;

    public AbstractFrguFacade(String id, String frguId, T current, ArrayList<PK> refs) {
        this.id = Optional.ofNullable(id);
        this.frguId = frguId;
        this.current = current;
        this.refs = refs;
    }

    @Override
    public Optional<String> getId() {
        return id;
    }

    @Override
    public String getFrguId() {
        return frguId;
    }

    @Override
    public T getCurrent() {
        return current;
    }

    @Override
    public ArrayList<PK> getRefs() {
        return refs;
    }

    @Override
    public void addRef(PK newRef) {
        refs.add(newRef);
    }


}
