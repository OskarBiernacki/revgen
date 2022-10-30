#!/bin/bash
if [ -f "revgen.class" ]; then
    java revgen $@
else
    javac revgen.java
    java revgen $@
fi