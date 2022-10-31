#!/bin/bash
#Install
echo "Installincg...";
chmod 777 revgen.sh ;
bob="ERROR";
( (link ./revgen.sh /usr/bin/revgen 2> /dev/null  && echo "/usr/bin/" > path.txt) || 
(link ./revgen.sh /opt/homebrew/bin/revgen 2> /dev/null && echo "/opt/homebrew/bin/" > path.txt) ||
(link ./revgen.sh /bin/revgen 2> /dev/null && echo "/bin/" > path.txt)) &&
echo "[+]Install path found"|| ( echo "Error, installing not complete" ; exit );
bob=$( cat path.txt ) || exit;
rm path.txt;
rm $bob"revgen";
mkdir $bob"revgen_INIT";
cp revgen.* $bob"revgen_INIT/";
link $bob"revgen_INIT/revgen.sh" $bob"/revgen";

tmp_file=$( echo "revgen" | base64 );
echo $bob > /tmp/$tmp_file ;


echo "Installing Complete!";