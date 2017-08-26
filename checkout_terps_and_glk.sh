#!/bin/bash

#
# (c) Copyright 2017 Stephen A. Gutknecht. All Rights Reserved.
# License granted: Public Domain, world wide.
#

#
# Makes assumption that this is virgin run, no previous checkout files.
#

# starting from current path, root of this project.
pwd
if [ ! -d "fictionengines/src/main/cpp/makefiles" ]; then
  echo "!!!! ERROR_CODE:A00 expected folder does not exist, aborting"
  exit 1
fi

mkdir -p fictionengines/src/main/cpp/loadable_engines
cd fictionengines/src/main/cpp/loadable_engines

pwd


actionwhat=$1
onstep=unknown
# default to all actions if no arguments given
if [[ $# -eq 0 ]] ; then
    actionwhat=all
fi

echo "actionwhat: $actionwhat"

# function for git source control checkout
# arguments: 1: network path to download from 2: destination directory 3: git commit reference
gitsourcecheckout () {
   echo "git source checkout, Parameter #1 is $1 Parameter #2 is $2"
   if [ -d "$2" ]; then
      echo "WARNING: Destination folder already exists before git source checkout. onstep: $onstep"
   fi

   git clone $1 $2

   # check error returned from git
   if [ $? -gt 0 ]; then
      echo "!!!! ERROR_CODE:B00 git source checkout ERROR, aborting. onstep: $onstep"
      exit 2
   fi
}

onstep=remglk
if [[ "$actionwhat" == "all" || "$actionwhat" == "$onstep" ]]; then
gitsourcecheckout https://github.com/erkyrath/remglk remglk
cp ../makefiles/remglk/Android.mk     remglk/
cp ../makefiles/remglk/CMakeLists.txt remglk/
fi

onstep=glulxe
if [[ "$actionwhat" == "all" || "$actionwhat" == "$onstep" ]]; then
gitsourcecheckout https://github.com/erkyrath/glulxe glulxe
cp ../makefiles/glulxe/Android.mk     glulxe/
cp ../makefiles/glulxe/CMakeLists.txt glulxe/
fi

onstep=git
if [[ "$actionwhat" == "all" || "$actionwhat" == "$onstep" ]]; then
gitsourcecheckout https://github.com/DavidKinder/Git.git git
cp ../makefiles/git/Android.mk        git/
cp ../makefiles/git/CMakeLists.txt    git/
fi

onstep=magnetic
if [[ "$actionwhat" == "all" || "$actionwhat" == "$onstep" ]]; then
gitsourcecheckout https://github.com/DavidKinder/Magnetic magnetic
cp ../makefiles/magnetic/Android.mk        magnetic/
cp ../makefiles/magnetic/CMakeLists.txt    magnetic/
fi

onstep=level9
if [[ "$actionwhat" == "all" || "$actionwhat" == "$onstep" ]]; then
gitsourcecheckout https://github.com/DavidKinder/Level9 level9
cp ../makefiles/level9/Android.mk        level9/
cp ../makefiles/level9/CMakeLists.txt    level9/
fi

