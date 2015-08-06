package ru.osslabs.frgu.mongodb.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import ru.osslabs.frgu.domain.FrguFacadeObject;
import ru.osslabs.frgu.domain.FrguObject;
import ru.osslabs.frgu.domain.FrguFacadeObjectBuilder;
import ru.osslabs.frgu.domain.FrguObjectBuilder;
import ru.osslabs.frgu.domain.ObjectType;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by ilyalyakin on 04.08.15.
 */
public class FrguFacadeObjectCodec implements Codec<FrguFacadeObject> {
        @Override
        public FrguFacadeObject decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            String id = reader.readObjectId("_id").toString();
            String frguId = reader.readString("frgu_id");
                reader.readStartDocument();
            //TODO check if _id is present
                FrguObject frguObject = new FrguObjectBuilder()
                        .withId(reader.readObjectId("_id").toString())
                        .withFrguId(reader.readString("frgu_id"))
                        .withShortName(reader.readString("short_name"))
                        .withFullName(reader.readString("full_name"))
                        .withSsn(reader.readInt64("ssn"))
                        .withData(reader.readString("data"))
                        .withObjectType(Enum.valueOf(ObjectType.class, reader.readString("object_type")))
                        .withChangeDate(reader.readInt64("change_date"))
                        .withTimestamp(reader.readInt64("timestamp")).build();
                reader.readEndDocument();
            ArrayList<String> refs = new ArrayList<>();
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                refs.add(reader.readString());
            }
            reader.readEndArray();
            reader.readEndDocument();

            //Костыль для привязки id к объекту. Иначе _id в поддокумент не пишется.
            frguObject.setId(Optional.of(new ObjectId(refs.get(refs.size() - 1)).toString()));

            FrguFacadeObject frguFacade = new FrguFacadeObjectBuilder()
                    .withId(id)
                    .withFrguId(frguId)
                    .withCurrent(frguObject)
                    .withRefs(refs).build();
            return frguFacade;
        }

        @Override
        public void encode(BsonWriter writer, FrguFacadeObject value, EncoderContext encoderContext) {
            writer.writeStartDocument();
                writer.writeString("frgu_id", value.getFrguId());
                writer.writeStartDocument("current");
                    writer.writeObjectId("_id", new ObjectId(value.getCurrent().getId().get()));
                    writer.writeString("frgu_id", value.getCurrent().getFrguId());
                    writer.writeString("short_name", value.getCurrent().getShortName());
                    writer.writeString("full_name", value.getCurrent().getFullName());
                    writer.writeInt64("ssn", value.getCurrent().getSsn());
                    writer.writeString("data", value.getCurrent().getData());
                    writer.writeString("object_type", value.getCurrent().getObjectType().name());
                    writer.writeInt64("change_date", value.getCurrent().getChangeDate());
                    writer.writeInt64("timestamp", value.getCurrent().getTimestamp());
                writer.writeEndDocument();
                writer.writeStartArray("refs");
                    value.getRefs().forEach(writer::writeString);
                writer.writeEndArray();
            writer.writeEndDocument();
        }

        @Override
        public Class<FrguFacadeObject> getEncoderClass() {
            return FrguFacadeObject.class;
        }
    }
