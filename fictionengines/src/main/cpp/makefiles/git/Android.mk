# Git interactive fiction interpreter Makefile for Android

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := git
LOCAL_SRC_FILES := compiler.c gestalt.c git.c git_mac.c git_unix.c \
                     git_windows.c glkop.c heap.c memory.c opcodes.c \
                     operands.c peephole.c savefile.c saveundo.c \
                     search.c terp.c accel.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../remglk
# -g flag will generate debug symbols
LOCAL_CFLAGS    := -DUSE_INLINE \
                   -g

LOCAL_STATIC_LIBRARIES := remglk
LOCAL_LDLIBS    := -llog

# For operating system security reasons, PIE is required on Android 5.0 and newer, and is likely good practice on all systems.
# Enable PIE manually. Will get reset on $(CLEAR_VARS) - here. NOTE: PIC and PIE, not just PIE
LOCAL_CFLAGS += -fPIC
LOCAL_LDFLAGS += -fPIE -pie

# include $(BUILD_SHARED_LIBRARY)
include $(BUILD_EXECUTABLE)
