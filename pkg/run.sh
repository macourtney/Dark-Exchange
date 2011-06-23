#!/bin/sh

echo path is $PATH

####
# provide your own values for mainjar, mainclass, and jars.
####

# Class file to run
mainclass="darkexchange.main"

# jar directory containing dependent jars
jars="lib"

####
# don't change the stuff below here.
####

# build the classpath
# initially contain the main app and the properties file directory
# cp=$jars

# Add all of the jar files in the dependent jars directory (using Java 6 wildcard)
cp="${jars}/*"

cp="${cp}:resources"

if [ "$OS" = "Windows_NT" ]; then
    cp=`cygpath --path --windows $cp`
fi

# run the application
java -cp "$cp" $mainclass ${@+"$@"}
