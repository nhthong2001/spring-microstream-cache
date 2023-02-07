package vn.elca.demo.util;

import one.microstream.persistence.binary.util.Serializer;
import one.microstream.persistence.binary.util.SerializerFoundation;
import vn.elca.demo.model.Dto;

import java.util.List;
import java.util.Set;

public class MicroStreamSerializer {
    public static long getSize(Object data) {
        if (data instanceof List) {
            SerializerFoundation<?> foundation = SerializerFoundation.New()
                                                                     .registerEntityTypes(List.class);
            final Serializer<byte[]> serializer = Serializer.Bytes(foundation);
            byte[] result = serializer.serialize(data);
            return result.length;
        } else if (data instanceof Set) {
            SerializerFoundation<?> foundation = SerializerFoundation.New()
                                                                     .registerEntityTypes(Set.class);
            final Serializer<byte[]> serializer = Serializer.Bytes(foundation);
            byte[] result = serializer.serialize(data);
            return result.length;
        }
        SerializerFoundation<?> foundation = SerializerFoundation.New()
                                                                 .registerEntityTypes(Dto.class);
        final Serializer<byte[]> serializer = Serializer.Bytes(foundation);
        byte[] result = serializer.serialize(data);
        return result.length;
    }
}
