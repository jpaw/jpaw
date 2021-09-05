#!/bin/bash
# create subclasses for Milli, Micro, Nano, Pico from the Femto subclass
# The Units subclass should be done manually, because it has some minor optimizations

# replacement method. It expects 3 parameters: The class prefix, the number of decimals, and the unit
createFromFemtos() {
    folder=src/main/java/de/jpaw/fixedpoint/types
    cat $folder/FemtoUnits.java | sed -e s/Femto/$1/g | sed -e s/15/$2/g | sed -e s/1000000000000000L/$3/g > $folder/$1Units.java
}

createFromFemtos Milli 3 1000L
createFromFemtos Micro 6 1000000L
createFromFemtos Nano  9 1000000000L
createFromFemtos Pico 12 1000000000000L
