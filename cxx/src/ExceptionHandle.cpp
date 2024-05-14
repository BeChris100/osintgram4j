#include <app/ExceptionHandle.h>

void throwJavaException(JNIEnv* env, const char* _cls, const char* msg) {
    jclass cls = env->FindClass(_cls);
    if (cls == nullptr)
        return;

    env->ThrowNew(cls, msg);
}

void throwJavaAppException(JNIEnv* env, const char* msg) {
    throwJavaException(env, "net/bc100dev/commons/ApplicationException", msg);
}

void throwJavaAppIOException(JNIEnv* env, const char* msg) {
    throwJavaException(env, "net/bc100dev/commons/ApplicationIOException", msg);
}

void throwJavaAppRuntimeException(JNIEnv* env, const char* msg) {
    throwJavaException(env, "net/bc100dev/commons/ApplicationRuntimeException", msg);
}