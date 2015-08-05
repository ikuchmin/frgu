package ru.osslabs.frgu.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.karneim.pojobuilder.GeneratePojoBuilder;
import ru.osslabs.frgu.dao.FrguIdentified;
import ru.osslabs.frgu.services.FrguObjectJsonSerializer;

import java.util.Objects;
import java.util.Optional;


@JsonSerialize(using = FrguObjectJsonSerializer.class)
public class FrguObject implements FrguIdentified<String> {

    private Optional<String> id;
    private String frguId;
    private String shortName;
    private String fullName;
    private Long ssn;
    private String data;
    private ObjectType objectType;
    private Long changeDate;
    private Long timestamp;

    @GeneratePojoBuilder
    public FrguObject(String id, String frguId, String shortName, String fullName, Long ssn, String data, ObjectType objectType, Long changeDate, Long timestamp) {
        this.id = Optional.ofNullable(id);
        this.frguId = frguId;
        this.shortName = shortName;
        this.fullName = fullName;
        this.ssn = ssn;
        this.data = data;
        this.objectType = objectType;
        this.changeDate = changeDate;
        this.timestamp = timestamp;
    }

    @Override
    public Optional<String> getId() {
        return id;
    }

    public String getFrguId() {
        return frguId;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getSsn() {
        return ssn;
    }

    public String getData() {
        return data;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public Long getChangeDate() {
        return changeDate;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "FrguObject{" +
                "id='" + id + '\'' +
                ", frgu_id='" + frguId + '\'' +
                ", short_name='" + shortName + '\'' +
                ", full_name='" + fullName + '\'' +
                ", ssn=" + ssn +
                ", data='" + data + '\'' +
                ", object_type='" + objectType + '\'' +
                ", change_date=" + changeDate +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrguObject that = (FrguObject) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(frguId, that.frguId) &&
                Objects.equals(shortName, that.shortName) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(ssn, that.ssn) &&
                Objects.equals(data, that.data) &&
                Objects.equals(objectType, that.objectType) &&
                Objects.equals(changeDate, that.changeDate) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frguId, shortName, fullName, ssn, data, objectType, changeDate, timestamp);
    }

    public void setId(Optional<String> id) {
        this.id = id;
    }
}
