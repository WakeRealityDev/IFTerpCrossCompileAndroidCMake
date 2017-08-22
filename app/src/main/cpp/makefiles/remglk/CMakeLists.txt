#  reference NDK 15c sdk/cmake/3.6.4111459/bin/cmake
#  however, making minimum to 3.6.4 resulted in message "You are running 3.6.0-rc2"
cmake_minimum_required(VERSION 3.6.0)
project(RemGlk)

set(CMAKE_VERBOSE_MAKEFILE on)

# set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS}  -Wall -Werror")
# set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fPIE -pie")

# is this library bigger due to?
#   http://ptspts.blogspot.com/2013/12/how-to-make-smaller-c-and-c-binaries.html
#   "Don't use these flags: -pie, -fpie, -fPIE, -fpic, -fPIC. Some of these are useful in shared libraries, so enable them only when compiling shared libraries."
# Result: did not help, still much larger than Android.mk gcc compile
#

include_directories(${CMAKE_CURRENT_SOURCE_DIR})

add_library( # Specifies the name of the library.
             remglk

             # Sets the library type.
             #    SHARED works as long as libremglk.so is in export LD_LIBRARY_PATH=
             #        SHARED has advantage of revealing that RemGlk is the source of the size bloat over gcc and Android.mk build
             #    STATIC works
             STATIC

             # Source files.
             main.c
             rgevent.c rgfref.c
             rggestal.c
             rgdata.c
             rgmisc.c rgstream.c rgstyle.c
             rgwin_blank.c
             rgwin_buf.c
             rgwin_grid.c
             rgwin_graph.c
             rgwin_pair.c
             rgwindow.c
             rgschan.c rgblorb.c cgunicod.c cgdate.c gi_dispa.c gi_debug.c gi_blorb.c
             glkstart.c
      )

set_target_properties(remglk PROPERTIES OUTPUT_NAME "remglk")

target_include_directories(remglk PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
