package ru.osslabs.frgu.mongodb.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.Optional;

/**
 * Created by ilyalyakin on 05.08.15.
 */
public class OptionalCodec implements Codec<Optional> {

    @Override
    public Optional decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return Optional.ofNullable(bsonReader.readString());
    }

    @Override
    public void encode(BsonWriter bsonWriter, Optional optional, EncoderContext encoderContext) {
        bsonWriter.writeString((String) optional.get());
    }

    @Override
    public Class<Optional> getEncoderClass() {
        return Optional.class;
    }
}
