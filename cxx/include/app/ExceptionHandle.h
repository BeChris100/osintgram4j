#ifndef OSINTGRAM4J_NATIVES_EXCEPTIONHANDLE_H
#define OSINTGRAM4J_NATIVES_EXCEPTIONHANDLE_H

#include "jni.h"

void throwJavaAppException(JNIEnv* env, const char* msg);

void throwJavaAppIOException(JNIEnv* env, const char* msg);

void throwJavaAppRuntimeException(JNIEnv* env, const char* msg);

void throwJavaException(JNIEnv* env, const char* _cls, const char* msg);

#endif //OSINTGRAM4J_NATIVES_EXCEPTIONHANDLE_H
