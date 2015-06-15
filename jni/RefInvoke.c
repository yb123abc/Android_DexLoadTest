/*
 * RefInvoke.c
 *
 *  Created on: 2015年6月14日
 *      Author: pegasus
 */
#include "RefInvoke.h"
#include <jni.h>
#include <string.h>

jobject invokeMethod(JNIEnv *env, jstring class_name,
		jstring method_name, jclass pareTyple[], jobject pareValues[])
{

}

jobject invokeStaticMethod(JNIEnv* env, const char* class_name,
		const char* method_name, const char* sig, jclass pareValues[])
{

	jclass obj_class = FindClass(env, class_name);
	jmethodID methodId = GetMethodID(obj_class, method_name, sig);


}

jobject getFieldObject(JNIEnv *env, jstring class_name, jobject obj, jstring fieldName)
{

}

jobject getStaticFieldObject(JNIEnv *env, jstring class_name, jstring filedName)
{

}

void setFieldObject(JNIEnv *env, jstring class_name, jstring fieldName, jobject obj, jobject fieldValue)
{

}

void setStaticFieldObject(JNIEnv *env, jstring class_name, jstring fieldName, jobject fieldValue)
{

}


