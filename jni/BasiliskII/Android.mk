LOCAL_PATH := $(call my-dir)

# remove -ffunction-sections and -fomit-frame-pointer from the default compile flags
#TARGET_thumb_release_CFLAGS := $(filter-out -ffunction-sections,$(TARGET_thumb_release_CFLAGS))
#TARGET_thumb_release_CFLAGS := $(filter-out -fomit-frame-pointer,$(TARGET_thumb_release_CFLAGS))
#TARGET_CFLAGS := $(filter-out -ffunction-sections,$(TARGET_CFLAGS))

# include libandprof.a in the build
#include $(CLEAR_VARS)
#LOCAL_MODULE := andprof
#LOCAL_SRC_FILES := andprof/$(TARGET_ARCH_ABI)/libandprof.a
#include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := basilisk

CG_SUBDIRS := \
src \
src/SDL \
src/slirp \
src/uae_cpu \
src/uae_cpu/fpu \
src/Unix \
src/Unix/Linux


# Add more subdirs here, like src/subdir1 src/subdir2

CG_SRCDIR=LOCAL_PATH
LOCAL_CFLAGS := $(foreach D, $(CG_SUBDIRS), -I$(CG_SRCDIR)/$(D))
#				-I$(LOCAL_PATH)/../sdl/include \
#				-I$(LOCAL_PATH)/../sdl_mixer \
#				-I$(LOCAL_PATH)/../sdl/src/video/android \
#				-I$(LOCAL_PATH)/../sdl/src/events \
#				-I$(LOCAL_PATH)/../stlport/stlport \
#				-I$(LOCAL_PATH)/include \
#				-I$(LOCAL_PATH) \
#				-I$(SYSROOT)/usr/include

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../sdl/include \
                    $(LOCAL_PATH)/../sdl_mixer \
                    $(LOCAL_PATH)/../sdl/src/video/android \
                    $(LOCAL_PATH)/../sdl/src/events \
                    $(LOCAL_PATH)/src/include \
                    $(LOCAL_PATH)/src/Unix \
                    $(LOCAL_PATH)/src/uae_cpu \
                    $(LOCAL_PATH)/src/uae_cpu/fpu \
                    $(LOCAL_PATH)/src/slirp \
                    $(LOCAL_PATH) \
                    $(SYSROOT)/usr/include

LOCAL_CPPFLAGS := $(LOCAL_CFLAGS)
LOCAL_CXXFLAGS := $(LOCAL_CFLAGS)

#Change C++ file extension as appropriate
LOCAL_CPP_EXTENSION := .cpp

LOCAL_SRC_FILES := $(foreach F, $(CG_SUBDIRS), $(addprefix $(F)/,$(notdir $(wildcard $(LOCAL_PATH)/$(F)/*.cpp))))
# Uncomment to also add C sources
LOCAL_SRC_FILES += $(foreach F, $(CG_SUBDIRS), $(addprefix $(F)/,$(notdir $(wildcard $(LOCAL_PATH)/$(F)/*.c))))

# LOCAL_STATIC_LIBRARIES := sdl_mixer sdl tremor stlport
LOCAL_STATIC_LIBRARIES := sdl_mixer sdl stlport andprof

LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog

include $(BUILD_SHARED_LIBRARY)
