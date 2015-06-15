LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE	:= DexLoadJni
LOCAL_SRC_FILES := DexLoadJni.cpp
LOCAL_LDLIBS    := -lm -llog 

include $(BUILD_SHARED_LIBRARY)