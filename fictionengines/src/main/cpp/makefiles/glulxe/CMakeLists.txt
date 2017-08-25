#  reference NDK 15c sdk/cmake/3.6.4111459/bin/cmake
#  however, making minimum to 3.6.4 resulted in message "You are running 3.6.0-rc2"
cmake_minimum_required(VERSION 3.6.0)
project(glulxe)

# For operating system security reasons, PIE is required on Android 5.0 and newer, and is likely good practice on all systems.
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fPIC")
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -fPIE -pie")

# is this library bigger due to?
#   http://ptspts.blogspot.com/2013/12/how-to-make-smaller-c-and-c-binaries.html
#   "Don't use these flags: -pie, -fpie, -fPIE, -fpic, -fPIC. Some of these are useful in shared libraries, so enable them only when compiling shared libraries."
#


set(CMAKE_VERBOSE_MAKEFILE on)

# specific to Glulxe compile flags
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS}  -DOS_UNIX -DTOLERATE_SUPERGLUS_BUG")


include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../remglk)
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

add_executable( # Specifies the name of the executable.
             lib_app_glulxe.so

             # Source files.
             main.c files.c vm.c exec.c funcs.c operand.c string.c glkop.c
             heap.c serial.c search.c accel.c float.c gestalt.c osdepend.c profile.c
             unixstrt.c
             )

target_link_libraries(lib_app_glulxe.so remglk)

target_include_directories (lib_app_glulxe.so PUBLIC)
