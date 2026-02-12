package com.jgoodies.common.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/base/Objects.class */
public final class Objects {
    private Objects() {
    }

    public static <T extends Serializable> T deepCopy(T original) {
        if (original == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            ObjectOutputStream oas = new ObjectOutputStream(baos);
            oas.writeObject(original);
            oas.flush();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Throwable e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }
}
