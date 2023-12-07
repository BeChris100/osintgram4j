#include "ExceptionHandle.h"

void throwJavaAppException(JNIEnv* env, const char* msg) {
    jclass cls = env->FindClass("net/bc100dev/commons/ApplicationException");
    env->ThrowNew(cls, msg);
}

void throwJavaAppIOException(JNIEnv* env, const char* msg) {
    jclass cls = env->FindClass("net/bc100dev/commons/ApplicationIOException");
    env->ThrowNew(cls, msg);
}

void throwJavaAppRuntimeException(JNIEnv* env, const char* msg) {
    jclass cls = env->FindClass("net/bc100dev/commons/ApplicationRuntimeException");
    env->ThrowNew(cls, msg);
}