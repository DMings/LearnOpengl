//
// Created by Administrator on 2019/9/30.
//
#include <jni.h>
#include "GifPlayer.h"

JNIEXPORT jlong JNICALL
create_jni(JNIEnv *env, jobject instance) {
    GifPlayer *gifPlayer = new GifPlayer();
    jlong gifPtr = (jlong) gifPlayer;
    return gifPtr;
}

JNIEXPORT jboolean JNICALL
load_jni(JNIEnv *env, jobject instance, jlong gifPlayerFormJava,
         jobject assetManager, jstring gifPath_) {
    const char *gifPath = env->GetStringUTFChars(gifPath_, 0);
    GifPlayer *gifPlayer = (GifPlayer *) gifPlayerFormJava;
    jboolean ret;
    if (gifPlayer) {
        ret = gifPlayer->load_gif(env, assetManager, gifPath);
    } else {
        ret = JNI_FALSE;
    }
    env->ReleaseStringUTFChars(gifPath_, gifPath);
    return ret;
}

JNIEXPORT void JNICALL
start_jni(JNIEnv *env, jobject instance, jlong gifPlayerFormJava, jboolean once, jint texture,
          jobject runnable) {
    GifPlayer *gifPlayer = (GifPlayer *) gifPlayerFormJava;
    if (gifPlayer) {
        gifPlayer->start(env, once, texture, runnable);
    }
}

JNIEXPORT void JNICALL pause_jni(JNIEnv *env, jobject instance, jlong gifPlayerFormJava) {
    GifPlayer *gifPlayer = (GifPlayer *) gifPlayerFormJava;
    if (gifPlayer) {
        gifPlayer->pause();
    }
}

JNIEXPORT void JNICALL resume_jni(JNIEnv *env, jobject instance, jlong gifPlayerFormJava) {
    GifPlayer *gifPlayer = (GifPlayer *) gifPlayerFormJava;
    if (gifPlayer) {
        gifPlayer->resume();
    }
}

JNIEXPORT jint JNICALL get_width_jni(JNIEnv *env, jobject instance, jlong gifPlayerFormJava) {
    GifPlayer *gifPlayer = (GifPlayer *) gifPlayerFormJava;
    if (gifPlayer) {
        return gifPlayer->get_width();
    } else {
        return 0;
    }
}

JNIEXPORT jint JNICALL get_height_jni(JNIEnv *env, jobject instance, jlong gifPlayerFormJava) {
    GifPlayer *gifPlayer = (GifPlayer *) gifPlayerFormJava;
    if (gifPlayer) {
        return gifPlayer->get_height();
    } else {
        return 0;
    }
}

JNIEXPORT void JNICALL stop_jni(JNIEnv *env, jobject instance, jlong gifPlayerFormJava) {
    GifPlayer *gifPlayer = (GifPlayer *) gifPlayerFormJava;
    if (gifPlayer) {
        gifPlayer->release();
    }
}

JNIEXPORT void JNICALL release_jni(JNIEnv *env, jobject instance, jlong gifPlayerFormJava) {
    GifPlayer *gifPlayer = (GifPlayer *) gifPlayerFormJava;
    if (gifPlayer) {
        delete (gifPlayer);
    }
}

JNINativeMethod method[] = {
        {"native_create",     "()J",                                                      (void *) create_jni},
        {"native_load",       "(JLandroid/content/res/AssetManager;Ljava/lang/String;)Z", (void *) load_jni},
        {"native_start",      "(JZILjava/lang/Runnable;)V",                               (void *) start_jni},
        {"native_pause",      "(J)V",                                                     (void *) pause_jni},
        {"native_resume",     "(J)V",                                                     (void *) resume_jni},
        {"native_get_width",  "(J)I",                                                     (void *) get_width_jni},
        {"native_get_height", "(J)I",                                                     (void *) get_height_jni},
        {"native_stop",       "(J)V",                                                     (void *) stop_jni},
        {"native_release",    "(J)V",                                                     (void *) release_jni},
};

jint registerNativeMethod(JNIEnv *env) {
    jclass cl = env->FindClass("com/dming/testgif/GifPlayer");
    if ((env->RegisterNatives(cl, method, sizeof(method) / sizeof(method[0]))) < 0) {
        return -1;
    }
    return 0;
}

jint unRegisterNativeMethod(JNIEnv *env) {
    jclass cl = env->FindClass("com/dming/testgif/GifPlayer");
    env->UnregisterNatives(cl);
    return 0;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) == JNI_OK) {
        registerNativeMethod(env);
        return JNI_VERSION_1_6;
    } else if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) == JNI_OK) {
        registerNativeMethod(env);
        return JNI_VERSION_1_4;
    }
    return JNI_ERR;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) == JNI_OK) {
        unRegisterNativeMethod(env);
    } else if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) == JNI_OK) {
        unRegisterNativeMethod(env);
    }
}
