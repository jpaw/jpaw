#!/bin/bash

folder=src/main/java/de/jpaw/enums

# create Byte, Integer and Long classes from short
sed -e 's/ 15/ 31/' < $folder/AbstractShortEnumSet.java | sed -e 's/short/int/g' | sed -e 's/AbstractShort/AbstractInt/' > $folder/AbstractIntEnumSet.java
sed -e 's/ 15/ 7/' < $folder/AbstractShortEnumSet.java | sed -e 's/short/byte/g' | sed -e 's/AbstractShort/AbstractByte/' > $folder/AbstractByteEnumSet.java
sed -e 's/ 15/ 63/' < $folder/AbstractShortEnumSet.java | sed -e 's/short/long/g' | sed -e 's/AbstractShort/AbstractLong/' > $folder/AbstractLongEnumSet.java
