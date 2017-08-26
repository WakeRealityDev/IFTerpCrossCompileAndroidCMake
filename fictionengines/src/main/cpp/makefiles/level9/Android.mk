# Level9

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := level9
LOCAL_SRC_FILES := bitmap.c level9.c Glk/glk.c


LOCAL_C_INCLUDES := $(LOCAL_PATH)/../remglk

# -g generates debug symbols
LOCAL_CFLAGS    := -std=gnu11 \
                     -DBITMAP_DECODER -DNEED_STRICMP_PROTOTYPE -Dstricmp=gln_strcasecmp -Dstrnicmp=gln_strncasecmp \
                     -g

LOCAL_STATIC_LIBRARIES := remglk
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
