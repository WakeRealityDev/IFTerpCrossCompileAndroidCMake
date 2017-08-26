# magnetic

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := magnetic
LOCAL_SRC_FILES := Generic/emu.c Glk/glk.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../remglk $(LOCAL_PATH)/Generic/

# -g generates debug symbols
# -include "limits.h" fixes SIZE_MAX references
# GarGlk linux may use -std=c99
LOCAL_CFLAGS    := -std=gnu11  \
                      \
                     -g

LOCAL_STATIC_LIBRARIES := remglk
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
