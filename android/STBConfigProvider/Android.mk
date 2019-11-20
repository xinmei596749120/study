LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := STBConfigProvider

LOCAL_CERTIFICATE := platform

LOCAL_MODULE_TAGS := optional

LOCAL_JAVA_LIBRARIES := 

LOCAL_SRC_FILES := $(call all-java-files-under, src)

include $(BUILD_PACKAGE)
