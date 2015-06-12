LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE	:= DexLoadJni
LOCAL_SRC_FILES := DexLoadJni.h

include $(BUILD_SHARED_LIBRARY)