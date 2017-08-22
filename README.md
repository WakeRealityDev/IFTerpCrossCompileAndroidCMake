IFTerpCrossCompileAndroidCMake
===============================
This is a newer alternate to the https://github.com/WakeRealityDev/IFTerpCrossCompileAndroid project that uses gcc and Android.mk.

This project uses clang and CMake system with integrated Android Studio 2.3.3 compilation of the NDK code.

Based on Hello JNI
======================
Hello JNI is an Android sample that uses JNI to call C code from a Android Java Activity.

This sample uses the new [Android Studio CMake plugin](http://tools.android.com/tech-docs/external-c-builds) with C++ support.

Pre-requisites
--------------
- Android Studio 2.3.3+ with [NDK](https://developer.android.com/ndk/) bundle.

Getting Started
---------------
Tested on Ubuntu 17.04 x86 system, latest Google Android NDK from Android Studio as of 2017-08-07. Bash is not required, you could manually run the commands inside of the checkout_terps_and_glk.sh script. These basic steps should work on Linux, OS X, Windows, etc - as long as you have the Android NDK properly installed and some way to download the IF interpreter and RemGlk.

1. Checkout this project from GitHub.com
1. $ chmod +x checkout_terps_and_glk.sh
1. $ ./checkout_terps_and_glk.sh
1. Launch Android Studio and open the project.

RemGlk + Glulxe source code are not in this project and the bash script will download them intot he proper folder.

You will not need to run ndk-build, the C code build should be automatically performed by the gradle system of Android Studio.

Resulting APK
===============
The apk should have two binary files:

lib_app_glulxe.so 450kB  
libhello-jni.so  6kB

The libhello-jni.so is part of the Google Sample and isn't relevant to the Interactive Fiction code, but it was left in to keep things simple.

The important thing to understand is that lib_app_glulxe.so is actualy not a dynamic library but instead an entire executable binary for the android device.  It is only named ".so" to be package dinto the APK - it is not the proper extension for the file given it's stand-alone executable binary format.

If you unzip the APK and extract the lib_app_glulxe.so, you should be able to test it directly as an Android exectuable app:

```bash
$ adb push lib_app_glulxe.so /data/local/tmp
$ adb shell
  Now on Android shell:
  $ cd /data/local/tmp
  $ chmod +x lib_app_glulxe.so
  $ ./lib_app_glulxe.so
```
At this point you should be able to send JSON to stdin to interface with the RemGlk that is on top of Glulxe interpreter.


License for the original Hello JNI
------------------------------------
Copyright 2015 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
