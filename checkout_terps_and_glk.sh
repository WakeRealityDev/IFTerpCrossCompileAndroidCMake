#!/bin/bash

#
# (c) Copyright 2017 Stephen A. Gutknecht. All Rights Reserved.
# License granted: Public domain, world wide.
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

# function for git source control checkout
# arguments: 1: network path to download from 2: destination directory 3: git commit reference
gitsourcecheckout () {
   echo "git source checkout, Parameter #1 is $1 Parameter #2 is $2"
   if [ -d "$2" ]; then
      echo "WARNING: Destination folder already exists before git source checkout"
   fi

   git clone $1 $2

   # check error returned from git
   if [ $? -gt 0 ]; then
      echo "!!!! ERROR_CODE:B00 git source checkout ERROR, aborting."
      exit 2
   fi
}


gitsourcecheckout https://github.com/erkyrath/remglk remglk
cp ../makefiles/remglk/Android.mk     remglk/
cp ../makefiles/remglk/CMakeLists.txt remglk/

gitsourcecheckout https://github.com/erkyrath/glulxe glulxe
cp ../makefiles/glulxe/Android.mk     glulxe/
cp ../makefiles/glulxe/CMakeLists.txt glulxe/

gitsourcecheckout https://github.com/DavidKinder/Git.git git
cp ../makefiles/git/Android.mk        git/
cp ../makefiles/git/CMakeLists.txt    git/
