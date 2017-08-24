# Glulxe interactive fiction interpreter Makefile for Android
# Makefile Author: Stephen A. Gutknecht
# (C) Copyright 2017 Stephen A. Gutknecht
# License: This Android.mk file is Public Domain. Compatible with any license.

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := glulxe
LOCAL_SRC_FILES := main.c files.c vm.c exec.c funcs.c operand.c string.c glkop.c \
                     heap.c serial.c search.c accel.c float.c gestalt.c osdepend.c profile.c \
                     unixstrt.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../remglk
# -g generates debug symbols
# TOLERATE_SUPERGLUS_BUG allows compatibility with Superglús story data files
LOCAL_CFLAGS    := -g -DOS_UNIX -DTOLERATE_SUPERGLUS_BUG
LOCAL_STATIC_LIBRARIES := remglk
LOCAL_LDLIBS    := -llog

LOCAL_CFLAGS += -fPIE
LOCAL_LDFLAGS += -fPIE -pie

include $(BUILD_EXECUTABLE)

