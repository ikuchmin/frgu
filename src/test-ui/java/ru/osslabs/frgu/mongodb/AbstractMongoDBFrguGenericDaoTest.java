package ru.osslabs.frgu.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.junit.Test;
import ru.osslabs.frgu.dao.FrguObjectDao;
import ru.osslabs.frgu.domain.FrguObject;
import ru.osslabs.frgu.domain.FrguObjectBuilder;
import ru.osslabs.frgu.domain.ObjectType;

import java.time.Clock;
import java.util.List;

import static com.mongodb.client.model.Filters.gt;
import static javaslang.collection.List.ofAll;
import static org.junit.Assert.*;

/**
 * Created by ikuchmin on 01.08.15.
 */
public class AbstractMongoDBFrguGenericDaoTest {

    private class MongoDBFrguObjectTestDao extends AbstractMongoDBFrguGenericDao<FrguObject, String>
            implements FrguObjectDao {

        private final String COLLECTION = "objects";

        public MongoDBFrguObjectTestDao(MongoDBClientFactory mongoDBClientFactory) {
            super(mongoDBClientFactory);
        }

        @Override
        public List<FrguObject> changes(long from) {
            FindIterable<FrguObject> iterable = getMongoCollection().find(gt("timestamp", from));
            return ofAll(iterable).toJavaList();
        }

        @Override
        protected MongoCollection<FrguObject> getMongoCollection() {
            return getMongoDBClientFactory()
                    .getDatabase()
                    .getCollection(COLLECTION, FrguObject.class)
                    .withCodecRegistry(CodecRegistries.fromCodecs(new FrguObjectCodec()));
        }

        private class FrguObjectCodec implements Codec<FrguObject> {
            @Override
            public FrguObject decode(BsonReader reader, DecoderContext decoderContext) {
                reader.readStartDocument();

                FrguObject frguObject = new FrguObjectBuilder()
                        .withId(reader.readString("_id"))
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
    }

    MongoDBFrguObjectTestDao mongoDBFrguObjectTestDao = new MongoDBFrguObjectTestDao(new MongoDBClientFactory("frgu"));

    @Test
    public void testRead() throws Exception {

    }

    @Test
    public void testReadAll() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {
        mongoDBFrguObjectTestDao.create(new FrguObjectBuilder()
                .withFrguId("FRGU service id")
                .withShortName("Short name")
                .withFullName("Full name")
                .withSsn(100L)
                .withData("XML stream")
                .withObjectType(ObjectType.PsPassport)
                .withChangeDate(Clock.systemUTC().millis())
                .withTimestamp(Clock.systemUTC().millis())
                .build());

        assertTrue(true);
    }

    @Test
    public void testCreateMany() throws Exception {
        mongoDBFrguObjectTestDao.create(
                new FrguObjectBuilder()
                        .withFrguId("FRGU_id2")
                        .withShortName("Short name")
                        .withFullName("Full name")
                        .withSsn(100L)
                        .withData("XML stream")
                        .withObjectType(ObjectType.PsPassport)
                        .withChangeDate(Clock.systemUTC().millis())
                        .withTimestamp(Clock.systemUTC().millis())
                        .build(),
                new FrguObjectBuilder()
                        .withFrguId("FRGU_id2")
                        .withShortName("Short name")
                        .withFullName("Full name")
                        .withSsn(100L)
                        .withData("XML stream")
                        .withObjectType(ObjectType.PsPassport)
                        .withChangeDate(Clock.systemUTC().millis())
                        .withTimestamp(Clock.systemUTC().millis())
                        .build());

        assertTrue(true);
    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }
}