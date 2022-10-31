#!/bin/bash

tmp_file=$( echo revgen | base64 ) ;
runme=$( cat /tmp/$tmp_file ) ;
runme=$runme"revgen_INIT/" ;
cd $runme ;

if [ -f "revgen.class" ]; then
    java "revgen" $@
else
    javac "revgen.java"
    java "revgen" $@
fi