package ru.osslabs.frgu.mongodb.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import ru.osslabs.frgu.domain.FrguObject;
import ru.osslabs.frgu.domain.FrguObjectBuilder;
import ru.osslabs.frgu.domain.ObjectType;

/**
 * Created by ilyalyakin on 04.08.15.
 */
public class FrguObjectCodec implements Codec<FrguObject> {
    @Override
    public FrguObject decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        FrguObject frguObject = new FrguObjectBuilder()
                .withId(reader.readObjectId("_id").toString())
                .withFrguId(reader.readString("frgu_id"))
                .withShortName(reader.readString("short_name"))
                .withFullName(reader.readString("full_name"))
                .withSsn(reader.readInt64("ssn"))
                .withData(reader.readString("data"))
                .withObjectType(Enum.valueOf(ObjectType.class, reader.readString("object_type")))
                .withChangeDate(reader.readDateTime("change_date"))
                .withTimestamp(reader.readDateTime("timestamp")).build();

        reader.readEndDocument();
        return frguObject;
    }

    @Override
    public void encode(BsonWriter writer, FrguObject value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", new ObjectId(value.getId().get()));
        writer.writeString("frgu_id", value.getFrguId());
        writer.writeString("short_name", value.getShortName());
        writer.writeString("full_name", value.getFullName());
        writer.writeInt64("ssn", value.getSsn());
        writer.writeString("data", value.getData());
        writer.writeString("object_type", value.getObjectType().name());
        writer.writeDateTime("change_date", value.getChangeDate());
        writer.writeDateTime("timestamp", value.getTimestamp());
        writer.writeEndDocument();
    }

    @Override
    public Class<FrguObject> getEncoderClass() {
        return FrguObject.class;
    }
}
