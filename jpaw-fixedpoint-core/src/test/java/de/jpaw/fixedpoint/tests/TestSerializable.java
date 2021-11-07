package de.jpaw.fixedpoint.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.fixedpoint.types.MicroUnits;


public class TestSerializable {

    private void run(int value, boolean expectIdentity) throws Exception {
        final MicroUnits org = MicroUnits.valueOf(value);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos);
        objectOutputStream.writeObject(org);
        objectOutputStream.flush();
        objectOutputStream.close();
        final byte[] result = baos.toByteArray();


        final ByteArrayInputStream bais = new ByteArrayInputStream(result);
        final ObjectInputStream objectInputStream = new ObjectInputStream(bais);
        MicroUnits deser = (MicroUnits) objectInputStream.readObject();
        objectInputStream.close();

        Assertions.assertEquals(org, deser, "Deserialized value must be equal original " + value);
        Assertions.assertEquals(expectIdentity, org == deser, "Deserialized value must have expected identity property to " + value);
    }

    @Test
    public void testFixedPointIsSerializable() throws Exception {
        run(2, false);
        run(1, true);
        run(0, true);
        run(-1, false);
    }
}
