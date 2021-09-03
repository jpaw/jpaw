TARGET=libjpawFP128.so
CC=gcc
DEBUG_OR_OPT=-O3 -m64 -std=c99 -Wall -Wshadow -Wpointer-arith -Wcast-qual -Wstrict-prototypes -Wmissing-prototypes
CFLAGS=-c -Wall -fPIC -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux $(DEBUG_OR_OPT)
LDFLAGS=-fPIC -shared $(DEBUG_OR_OPT) -L$(HOME)/lib

SRCDIR=src/main/c
OBJDIR=target/o
TARGETDIR=target/lib
TARGETLIB=$(TARGETDIR)/$(TARGET)
OBJECTS=$(OBJDIR)/mult128.o
INCLUDES=$(SRCDIR)/jpawFP128.h

all: $(TARGETLIB)

$(TARGETLIB): $(OBJECTS)
	mkdir -p $(TARGETDIR)
	$(CC) $(LDFLAGS) $(OBJECTS) -o $(TARGETLIB)
	cp $(TARGETLIB) $(HOME)/lib

$(OBJDIR)/mult128.o: $(SRCDIR)/mult128.c $(INCLUDES)
	mkdir -p $(OBJDIR)
	$(CC) $(CFLAGS) $< -o $@

clean:
	rm -rf $(OBJDIR) $(TARGETDIR)
