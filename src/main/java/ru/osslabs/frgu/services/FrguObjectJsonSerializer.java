package ru.osslabs.frgu.services;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.osslabs.frgu.domain.FrguObject;

import java.io.IOException;

/**
 * Created by ilyalyakin on 05.08.15.
 */
public class FrguObjectJsonSerializer extends JsonSerializer<FrguObject>{

    @Override
    public void serialize(FrguObject value,
                          JsonGenerator jGen,
                          SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException
    {
        jGen.writeStartObject();
        jGen.writeStringField("_id", value.getId().get());
        jGen.writeStringField("frgu_id", value.getFrguId());
        jGen.writeStringField("short_name", value.getShortName());
        jGen.writeStringField("full_name", value.getFullName());
        jGen.writeNumberField("ssn", value.getSsn());
        jGen.writeStringField("data", value.getData());
        jGen.writeStringField("object_type", value.getObjectType().name());
        jGen.writeNumberField("change_date", value.getChangeDate());
        jGen.writeNumberField("timestamp", value.getTimestamp());
        jGen.writeEndObject();
    }
}
