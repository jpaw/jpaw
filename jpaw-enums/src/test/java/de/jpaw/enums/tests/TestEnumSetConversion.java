package de.jpaw.enums.tests;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.enums.AbstractByteEnumSet;
import de.jpaw.enums.AbstractIntEnumSet;
import de.jpaw.enums.AbstractLongEnumSet;
import de.jpaw.enums.AbstractShortEnumSet;
import de.jpaw.enums.EnumSetMarker;

public class TestEnumSetConversion {

    @Test
    public void testSetMap() throws Exception {
        StringBuilder sb = new StringBuilder(63);
        for (int i = 0; i < 10; ++i)
            sb.append((char)('0' + i));
        for (int i = 0; i < 26; ++i)
            sb.append((char)('A' + i));
        for (int i = 0; i < 26; ++i)
            sb.append((char)('a' + i));
        sb.append('_');

        assert(sb.toString().equals(EnumSetMarker.STANDARD_TOKENS));
    }

    private static enum ByteTestEnum {
        Z0, Z1, Z2, Z3, Z4, Z5, Z6 }
    private static enum ShortTestEnum {
        Z0, Z1, Z2, Z3, Z4, Z5, Z6, Z7, Z8, Z9, ZA, ZB, ZC, ZD, ZE }
    private static enum IntTestEnum {
        Z0, Z1, Z2, Z3, Z4, Z5, Z6, Z7, Z8, Z9, ZA, ZB, ZC, ZD, ZE, ZF,
        X0, X1, X2, X3, X4, X5, X6, X7, X8, X9, XA, XB, XC, XD, XE }
    private static enum LongTestEnum { Z0, Z1, Z2, Z3, Z4, Z5, Z6, Z7, Z8, Z9, ZA, ZB, ZC, ZD, ZE, ZF,
        T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, TA, TB, TC, TD, TE, TF,
        Y0, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8, Y9, YA, YB, YC, YD, YE, YF,
        X0, X1, X2, X3, X4, X5, X6, X7, X8, X9, XA, XB, XC, XD, XE }

    private static class ByteTestEnumset extends AbstractByteEnumSet<ByteTestEnum> {
        private static final long serialVersionUID = -3859434345666657711L;

        ByteTestEnumset(byte n) {
            super(n);
        }

        @Override
        protected int getMaxOrdinal() {
            return 7;
        }

        @Override
        public Iterator<ByteTestEnum> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EnumSetMarker ret$MutableClone(boolean deepCopy, boolean unfreezeCollections) {
            return null;
        }

        @Override
        public EnumSetMarker ret$FrozenClone() {
            return null;
        }
    }

    private static class ShortTestEnumset extends AbstractShortEnumSet<ShortTestEnum> {
        private static final long serialVersionUID = -3859434345666657712L;

        ShortTestEnumset(short n) {
            super(n);
        }

        @Override
        protected int getMaxOrdinal() {
            return 15;
        }

        @Override
        public Iterator<ShortTestEnum> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EnumSetMarker ret$MutableClone(boolean deepCopy, boolean unfreezeCollections) {
            return null;
        }

        @Override
        public EnumSetMarker ret$FrozenClone() {
            return null;
        }
    }

    private static class IntTestEnumset extends AbstractIntEnumSet<IntTestEnum> {
        private static final long serialVersionUID = -3859434345666657713L;

        IntTestEnumset(int n) {
            super(n);
        }

        @Override
        protected int getMaxOrdinal() {
            return 31;
        }

        @Override
        public Iterator<IntTestEnum> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EnumSetMarker ret$MutableClone(boolean deepCopy, boolean unfreezeCollections) {
            return null;
        }

        @Override
        public EnumSetMarker ret$FrozenClone() {
            return null;
        }
    }

    private static class LongTestEnumset extends AbstractLongEnumSet<LongTestEnum> {
        private static final long serialVersionUID = -3859434345666657714L;

        LongTestEnumset(long n) {
            super(n);
        }

        @Override
        protected int getMaxOrdinal() {
            return 63;
        }

        @Override
        public Iterator<LongTestEnum> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EnumSetMarker ret$MutableClone(boolean deepCopy, boolean unfreezeCollections) {
            return null;
        }

        @Override
        public EnumSetMarker ret$FrozenClone() {
            return null;
        }
    }

    @Test
    public void testSetConversionsByte() throws Exception {
        byte i = 0;
        while (i >= 0) {
            // create an enum for the bitmap i, convert it to string and back
            ByteTestEnumset set = new ByteTestEnumset(i);
            String chars = set.asStringMap();
            Assert.assertEquals(set.size(), chars.length());
            byte bitmap = ByteTestEnumset.fromStringMap(chars);
            Assert.assertEquals(i, bitmap);

            i = (byte) (i * 3 + 1);      // next value: 0, 1, 4, 13, 40, 121, 364, ...
        }
    }

    @Test
    public void testSetConversionsShort() throws Exception {
        short i = 0;
        while (i >= 0) {
            // create an enum for the bitmap i, convert it to string and back
            ShortTestEnumset set = new ShortTestEnumset(i);
            String chars = set.asStringMap();
            Assert.assertEquals(set.size(), chars.length());
            short bitmap = ShortTestEnumset.fromStringMap(chars);
            Assert.assertEquals(i, bitmap);

            i = (short) (i * 3 + 1);      // next value: 0, 1, 4, 13, 40, 121, 364, ...
        }
    }

    @Test
    public void testSetConversionsInt() throws Exception {
        int i = 0;
        while (i >= 0) {
            // create an enum for the bitmap i, convert it to string and back
            IntTestEnumset set = new IntTestEnumset(i);
            String chars = set.asStringMap();
            Assert.assertEquals(set.size(), chars.length());
            int bitmap = IntTestEnumset.fromStringMap(chars);
            Assert.assertEquals(i, bitmap);

            i = i * 3 + 1;      // next value: 0, 1, 4, 13, 40, 121, 364, ...
        }
    }

    @Test
    public void testSetConversionsLong() throws Exception {
        long i = 0;
        while (i >= 0) {
            // create an enum for the bitmap i, convert it to string and back
            LongTestEnumset set = new LongTestEnumset(i);
            String chars = set.asStringMap();
            Assert.assertEquals(set.size(), chars.length());
            long bitmap = LongTestEnumset.fromStringMap(chars);
            Assert.assertEquals(i, bitmap);

            i = i * 3 + 1;      // next value: 0, 1, 4, 13, 40, 121, 364, ...
        }
    }
}
