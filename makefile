# ##############################################################################
## Makefile for lmock project.
## The input directories are src (for the sources) and tests.
## The outputs are:
##  - build/src and build/tests: the class files generated when compiling
##    sources
##  - lib: the generated jar files: lmock.jar and lmockTests.jar
##  - findbugs.xml: findbugs report
##  - lmock-javadoc: documentation of the lmock API as javadoc
##  - A jar package including the sources
##  - doxydoc: full documentation of the project 
# ##############################################################################

## Version tag, please keep in sync with the version
VERSION=1.0.0-alpha1

# ------------------------------------------------------------------------------
# List of tools used to build the application.
# You need to have the following binaries in your path:
#  - java tools: javac, jar, javadoc
#  - doxygen 
#  - findbugs 
# ------------------------------------------------------------------------------
JAVAC=javac -encoding utf8 -target 1.6
JAR=jar cf
JAREXEC=jar cfm
JAVADOC=javadoc -public -sourcepath src -d lmock-javadoc -windowtitle "lmock" -doctitle lmock
DOXYGEN=doxygen tools/Doxyfile
FINDBUGS=findbugs -textui -conserveSpace -xml -outputFile findbugs.xml

# ------------------------------------------------------------------------------
# INPUT AND RESULTING STRUCTURE
# ------------------------------------------------------------------------------

## Where we write the produced libraries...
LIB_OUTPUT_DIR=lib
lmockJar=$(LIB_OUTPUT_DIR)/lmock-$(VERSION).jar
lmockJavadocJar=$(LIB_OUTPUT_DIR)/lmock-javadoc-$(VERSION).jar
lmockTestsJar=$(LIB_OUTPUT_DIR)/lmockTests-$(VERSION).jar
lmockSrcJar=../$(LIB_OUTPUT_DIR)/lmock-src-$(VERSION).jar

## Where we put the built classes.
CLASS_OUTPUT_DIR=build
lmockClazz=$(CLASS_OUTPUT_DIR)/src
lmockTestsClazz=$(CLASS_OUTPUT_DIR)/tests
lmockTestManifest=$(CLASS_OUTPUT_DIR)/Manifest.txt

## The JUnit library
jUnit=tools/junit-4.9b2.jar

# ------------------------------------------------------------------------------
# SOURCES TO PROCEED
# ------------------------------------------------------------------------------
SOURCES=$(shell find src -name "*.java")
TEST_SOURCES=$(shell find tests -name "*.java")

# ------------------------------------------------------------------------------
# BUILD
# ------------------------------------------------------------------------------

all: $(lmockJar) $(lmockTestsJar) _junit _findbugs _javadoc _src_package
clean:
	-rm -rf lmock-javadoc
	-rm -rf doxydoc
	-rm -rf bin
	-rm -rf coverage
	-rm -rf $(CLASS_OUTPUT_DIR)
	-rm -rf $(LIB_OUTPUT_DIR)
	-rm -f findbugs.xml
	-rm -f lmock.fbp

$(lmockJar): $(SOURCES)
	mkdir -p $(lmockClazz)
	mkdir -p $(LIB_OUTPUT_DIR)
	$(JAVAC) -d $(lmockClazz) $(SOURCES)
	$(JAR) $(lmockJar) -C $(lmockClazz) .

$(lmockTestsJar): $(TEST_SOURCES) $(lmockJar)
	mkdir -p $(lmockTestsClazz)
	mkdir -p $(LIB_OUTPUT_DIR)
	$(JAVAC) -classpath $(lmockJar):$(jUnit) -d $(lmockTestsClazz) $(TEST_SOURCES)
	@echo "Main-Class: com.vmware.lmock.test.TestRunner" > $(lmockTestManifest)
	$(JAREXEC) $(lmockTestsJar) $(lmockTestManifest) -C $(lmockTestsClazz) .

_junit: $(lmockJar) $(lmockTestsJar)
# Run once with no trace to quickly validate
	java -classpath $(lmockJar):$(jUnit):$(lmockTestsJar) com.vmware.lmock.test.TestRunner
# Now do the same with traces and do the test coverage
	java -cp tools/emma.jar emmarun -cp tools/junit-4.9b2.jar:$(lmockJar):$(lmockTestsJar) -sp src -r html com.vmware.lmock.test.TestRunner -t

_findbugs: $(lmockJar)
# Create a configuration file aligned with the current version
	@cat tools/lmock.fbp | sed s="@LMOCKJAR@"=$(lmockJar)=g > lmock.fbp
	$(FINDBUGS) -project lmock.fbp

_javadoc: $(lmockJar)
	$(JAVADOC) $(SOURCES)
	$(JAR) $(lmockJavadocJar) lmock-javadoc 

_src_package:
	cd src && $(JAR) $(lmockSrcJar) com

_doxydoc: $(lmockJar)
	$(DOXYGEN)

