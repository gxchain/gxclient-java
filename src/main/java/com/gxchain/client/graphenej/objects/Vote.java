package com.gxchain.client.graphenej.objects;

import com.gxchain.client.graphenej.interfaces.ByteSerializable;
import lombok.Getter;

/**
 * Created by nelson on 12/5/16.
 */
public class Vote implements ByteSerializable {
    @Getter
    private int type;
    @Getter
    private int instance;

    public Vote(String vote) {
        String[] parts = vote.split(":");
        assert (parts.length == 2);
        this.type = Integer.valueOf(parts[0]);
        this.instance = Integer.valueOf(parts[1]);
    }

    public Vote(int type, int instance) {
        this.type = type;
        this.instance = instance;
    }

    @Override
    public String toString() {
        return String.format("%d:%d", this.type, this.instance);
    }

    @Override
    public byte[] toBytes() {
        return new byte[]{(byte) this.instance, (byte) this.type};
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        final Vote vote = (Vote) obj;
        if (this == vote) {
            return true;
        } else {
            return (this.type == vote.type && this.instance == vote.instance);
        }
    }

    public int hashCode() {
        return type * 10000000 + instance;
    }
}
