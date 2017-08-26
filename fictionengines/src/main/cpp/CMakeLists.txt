#
# portions (c) Copyright 2015 Google, Inc with Apache 2.0 license for the original "Hello JNI" NDK example app code
# portions (c) Copyright 2017 Stephen A. Gutknecht. All Rights Reserved.
# License granted: Apache 2.0 license to maintain compatibility with existing "Hello JNI" NDK example app code
#

#  reference NDK 15c sdk/cmake/3.6.4111459/bin/cmake
#  however, making minimum to 3.6.4 resulted in message "You are running 3.6.0-rc2"
cmake_minimum_required(VERSION 3.6.0)

set(CMAKE_VERBOSE_MAKEFILE on)



#
# https://cmake.org/Wiki/CMake_Useful_Variables/Logging_Useful_Variables
#

MESSAGE ( STATUS "CMAKE -------------=============--------- INFO section begin" )

# if you are building in-source, this is the same as CMAKE_SOURCE_DIR, otherwise
# this is the top level directory of your build tree
MESSAGE( STATUS "CMAKE_BINARY_DIR:         " ${CMAKE_BINARY_DIR} )

# if you are building in-source, this is the same as CMAKE_CURRENT_SOURCE_DIR, otherwise this
# is the directory where the compiled or generated files from the current CMakeLists.txt will go to
MESSAGE( STATUS "CMAKE_CURRENT_BINARY_DIR: " ${CMAKE_CURRENT_BINARY_DIR} )

# this is the directory, from which cmake was started, i.e. the top level source directory
MESSAGE( STATUS "CMAKE_SOURCE_DIR:         " ${CMAKE_SOURCE_DIR} )

# this is the directory where the currently processed CMakeLists.txt is located in
MESSAGE( STATUS "CMAKE_CURRENT_SOURCE_DIR: " ${CMAKE_CURRENT_SOURCE_DIR} )

# contains the full path to the top level directory of your build tree
MESSAGE( STATUS "PROJECT_BINARY_DIR: " ${PROJECT_BINARY_DIR} )

# contains the full path to the root of your project source directory,
# i.e. to the nearest directory where CMakeLists.txt contains the PROJECT() command
MESSAGE( STATUS "PROJECT_SOURCE_DIR: " ${PROJECT_SOURCE_DIR} )

# set this variable to specify a common place where CMake should put all executable files
# (instead of CMAKE_CURRENT_BINARY_DIR)
MESSAGE( STATUS "EXECUTABLE_OUTPUT_PATH: " ${EXECUTABLE_OUTPUT_PATH} )

# set this variable to specify a common place where CMake should put all libraries
# (instead of CMAKE_CURRENT_BINARY_DIR)
MESSAGE( STATUS "LIBRARY_OUTPUT_PATH:     " ${LIBRARY_OUTPUT_PATH} )

# tell CMake to search first in directories listed in CMAKE_MODULE_PATH
# when you use FIND_PACKAGE() or INCLUDE()
MESSAGE( STATUS "CMAKE_MODULE_PATH: " ${CMAKE_MODULE_PATH} )

# this is the complete path of the cmake which runs currently (e.g. /usr/local/bin/cmake)
MESSAGE( STATUS "CMAKE_COMMAND: " ${CMAKE_COMMAND} )

# this is the CMake installation directory
MESSAGE( STATUS "CMAKE_ROOT: " ${CMAKE_ROOT} )

# this is the filename including the complete path of the file where this variable is used.
MESSAGE( STATUS "CMAKE_CURRENT_LIST_FILE: " ${CMAKE_CURRENT_LIST_FILE} )

# this is linenumber where the variable is used
MESSAGE( STATUS "CMAKE_CURRENT_LIST_LINE: " ${CMAKE_CURRENT_LIST_LINE} )

# this is used when searching for include files e.g. using the FIND_PATH() command.
MESSAGE( STATUS "CMAKE_INCLUDE_PATH: " ${CMAKE_INCLUDE_PATH} )

# this is used when searching for libraries e.g. using the FIND_LIBRARY() command.
MESSAGE( STATUS "CMAKE_LIBRARY_PATH: " ${CMAKE_LIBRARY_PATH} )

# the complete system name, e.g. "Linux-2.4.22", "FreeBSD-5.4-RELEASE" or "Windows 5.1"
MESSAGE( STATUS "CMAKE_SYSTEM: " ${CMAKE_SYSTEM} )

# the short system name, e.g. "Linux", "FreeBSD" or "Windows"
MESSAGE( STATUS "CMAKE_SYSTEM_NAME: " ${CMAKE_SYSTEM_NAME} )

# only the version part of CMAKE_SYSTEM
MESSAGE( STATUS "CMAKE_SYSTEM_VERSION: " ${CMAKE_SYSTEM_VERSION} )

# the processor name (e.g. "Intel(R) Pentium(R) M processor 2.00GHz")
MESSAGE( STATUS "CMAKE_SYSTEM_PROCESSOR: " ${CMAKE_SYSTEM_PROCESSOR} )

# is TRUE on all UNIX-like OS's, including Apple OS X and CygWin
MESSAGE( STATUS "UNIX: " ${UNIX} )

# is TRUE on Windows, including CygWin
MESSAGE( STATUS "WIN32: " ${WIN32} )

# is TRUE on Apple OS X
MESSAGE( STATUS "APPLE: " ${APPLE} )

# is TRUE when using the MinGW compiler in Windows
MESSAGE( STATUS "MINGW: " ${MINGW} )

# is TRUE on Windows when using the CygWin version of cmake
MESSAGE( STATUS "CYGWIN: " ${CYGWIN} )

# is TRUE on Windows when using a Borland compiler
MESSAGE( STATUS "BORLAND: " ${BORLAND} )

# is TRUE on Windows when using a Borland compiler
MESSAGE( STATUS "ANDROID_ABI: " ${ANDROID_ABI} )

MESSAGE ( STATUS "CMAKE -------------=============--------- INFO section end" )



# set binary output folder to jniLibs folder, Android gradle will package into apk
set(EXECUTABLE_OUTPUT_PATH      "${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")

#:::
#::: Interactive Fiction engines / interpreter apps to link with RemGlk
#:::
#::: RemGLk should be first, as it is a Glk library that will be linked by all the others.
add_subdirectory (${CMAKE_SOURCE_DIR}/loadable_engines/remglk)
add_subdirectory (${CMAKE_SOURCE_DIR}/loadable_engines/glulxe)
add_subdirectory (${CMAKE_SOURCE_DIR}/loadable_engines/git)
add_subdirectory (${CMAKE_SOURCE_DIR}/loadable_engines/magnetic)
add_subdirectory (${CMAKE_SOURCE_DIR}/loadable_engines/level9)


#:::
#::: The original hello-uni NDK example app legacy. This can be removed.
#:::
add_library(hello-jni SHARED
            hello-jni.c)

# Include libraries needed for hello-jni lib
target_link_libraries(hello-jni
                      android
                      log)
