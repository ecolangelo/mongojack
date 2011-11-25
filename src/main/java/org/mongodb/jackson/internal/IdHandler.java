package org.mongodb.jackson.internal;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.BeanProperty;

/**
 * Handler for ids.  Converts them between the objects type and the database type
 */
public abstract class IdHandler<K, D> {
    /**
     * Convert the given database id to the java objects id
     *
     * @param dbId The database id to convert from
     * @return The converted id
     */
    public abstract K fromDbId(D dbId);

    /**
     * Convert the given java object id to the databases id
     *
     * @param id The java object id to convert from
     * @return The converted database id
     */
    public abstract D toDbId(K id);

    public static <K> IdHandler<K, ?> create(BeanProperty beanProperty, Class<K> keyType) {
        if (beanProperty != null && beanProperty.getAnnotation(org.mongodb.jackson.ObjectId.class) != null) {
            if (beanProperty.getType().getRawClass() == String.class) {
                return (IdHandler) new StringToObjectIdHandler();
            } else if (beanProperty.getType().getRawClass() == byte[].class) {
                return (IdHandler) new ByteArrayToObjectIdHandler();
            }
        }
        return new NoopIdHandler<K>();
    }

    public static class NoopIdHandler<K> extends IdHandler<K, Object> {
        @Override
        public K fromDbId(Object dbId) {
            return (K) dbId;
        }

        @Override
        public Object toDbId(K id) {
            return id;
        }
    }

    public static class StringToObjectIdHandler extends IdHandler<String, ObjectId> {
        @Override
        public String fromDbId(ObjectId dbId) {
            if (dbId != null) {
                return dbId.toString();
            } else {
                return null;
            }
        }

        @Override
        public ObjectId toDbId(String id) {
            if (id != null) {
                return new ObjectId(id);
            } else {
                return null;
            }
        }
    }

    public static class ByteArrayToObjectIdHandler extends IdHandler<byte[], ObjectId> {
        @Override
        public byte[] fromDbId(ObjectId dbId) {
            if (dbId != null) {
            return dbId.toByteArray();
            } else {
                return null;
            }
        }

        @Override
        public ObjectId toDbId(byte[] id) {
            if (id != null) {
                return new ObjectId(id);
            } else {
                return null;
            }
        }
    }
}