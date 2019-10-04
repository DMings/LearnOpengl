//
// Created by Administrator on 11/17/2016.
//

#ifndef FACERECOGNITION_LOG_H
#define FACERECOGNITION_LOG_H

#define LOG_DEBUG true

#define TAG "DMUI"

#include <android/log.h>

#ifdef LOG_DEBUG
#define LOGI(...) \
        __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)

#define LOGD(...) \
        __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)

#define LOGW(...) \
        __android_log_print(ANDROID_LOG_WARN,TAG,__VA_ARGS__)

#define LOGE(...) \
        __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#else
#define LOGI(...)
#define LOGD(...)
#define LOGW(...)
#define LOGE(...)
#endif


#endif //FACERECOGNITION_LOG_H
