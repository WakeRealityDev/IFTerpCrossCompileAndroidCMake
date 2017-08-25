#  reference NDK 15c sdk/cmake/3.6.4111459/bin/cmake
#  however, making minimum to 3.6.4 resulted in message "You are running 3.6.0-rc2"
cmake_minimum_required(VERSION 3.6.0)
project(git)

# For operating system security reasons, PIE is required on Android 5.0 and newer, and is likely good practice on all systems.
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fPIC")
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -fPIE -pie")


set(CMAKE_VERBOSE_MAKEFILE on)

# specific to Git compile flags
# -g flag will generate debug symbols (is this true for CMake?)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DUSE_INLINE -g")

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../remglk)
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

add_executable( # Specifies the name of the executable.
             lib_app_git.so

             # Source files.
             # excluded: git_mac.c git_windows.c
             compiler.c gestalt.c git.c git_unix.c
             glkop.c heap.c memory.c opcodes.c
             operands.c peephole.c savefile.c saveundo.c
             search.c terp.c accel.c
             )

target_link_libraries(lib_app_git.so remglk)

target_include_directories (lib_app_git.so PUBLIC)
