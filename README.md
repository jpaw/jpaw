##Utility classes used for several jpaw projects

The classes defined within this repository have been part of the bonaparte java implementation,
but in fact do not relate to the DSL and/or serialization / deserialization functionality
and therefore have been separated into this repository for better modularity.

Classes which might be useful in other projects are
  * ByteBuilder, a corresponding class to StringBuilder, but for byte arrays
  * ByteArray, a corresponding class to java's String class as implemented in Java 5
  * xenums, enum based classes which allow some kind of inheritance
  * enum sets, with access to the bitmap representation
  * enum sets which are treated as character arrays / strings
  * ApplicationException, some kind of Exception which supports an error number, which can be used to index the messages and categorize the exceptions by core reason.

In addition, this repository serves as a parent pom / bom, defining some revisions of libraries used in other projects.

###Building

This project uses maven3 as a build tool.

