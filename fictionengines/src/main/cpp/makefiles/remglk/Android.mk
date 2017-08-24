#   Copyright © 2016-2017 Stephen A. Gutknecht
#   This Android.mk file is licensed MIT License in terms with existing RemGlk code
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := remglk

# -g generates debug symbols
LOCAL_CFLAGS    :=  -g

LOCAL_SRC_FILES := \
                 main.c \
                 rgevent.c rgfref.c \
                 rggestal.c \
                 rgdata.c \
                 rgmisc.c rgstream.c rgstyle.c \
                 rgwin_blank.c \
                 rgwin_buf.c \
                 rgwin_grid.c \
                 rgwin_graph.c \
                 rgwin_pair.c \
                 rgwindow.c \
                 rgschan.c rgblorb.c cgunicod.c cgdate.c gi_dispa.c gi_debug.c gi_blorb.c \
                 glkstart.c

LOCAL_CFLAGS += -fPIE
LOCAL_LDFLAGS += -fPIE -pie

include $(BUILD_STATIC_LIBRARY)

