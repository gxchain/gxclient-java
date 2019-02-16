package com.gxchain.client.graphenej.objects;

import com.google.common.primitives.Bytes;
import com.google.gson.JsonElement;
import com.gxchain.client.graphenej.interfaces.ByteSerializable;
import com.gxchain.client.graphenej.interfaces.GrapheneSerializable;

/**
 * Container template class used whenever we have an optional field.
 * <p>
 * The idea here is that the binary serialization of this field should be performed
 * in a specific way determined by the field implementing the {@link ByteSerializable}
 * interface, more specifically using the {@link ByteSerializable#toBytes()} method.
 * <p>
 * However, if the field is missing, the Optional class should be able to know how
 * to serialize it, as this is always done by placing an zero byte.
 */
public class Optional<T extends GrapheneSerializable> implements GrapheneSerializable {
    private T optionalField;

    public Optional(T field) {
        optionalField = field;
    }

    @Override
    public byte[] toBytes() {
        if (optionalField == null)
            return new byte[]{(byte) 0};
        else
            return Bytes.concat(new byte[]{(byte) 1}, optionalField.toBytes());
    }

    public boolean isSet() {
        return this.optionalField != null;
    }

    @Override
    public String toJsonString() {
        return optionalField.toJsonString();
    }

    @Override
    public JsonElement toJsonObject() {
        return optionalField.toJsonObject();
    }

    public T value(){
        return optionalField;
    }
}
