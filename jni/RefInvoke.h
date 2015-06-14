/*
 * RefInvoke.h
 *
 *  Created on: 2015年6月14日
 *      Author: pegasus
 */
#include <jni.h>

#ifndef REFINVOKE_H_
#define REFINVOKE_H_

jobject invokeMethod(JNIEnv *env, jstring class_name, jstring method_name, jclass[] pareTyple, jobject[] pareValues);

jobject invokeStaticMethod(JNIEnv *env, jstring class_name, jstring method_name, jobject[] pareTyple, jclass[] pareValues);

jobject getFieldObject(JNIEnv *env, jstring class_name, jobject obj, jstring fieldName);

jobject getStaticFieldObject(JNIEnv *env, jstring class_name, jstring filedName);

void setFieldObject(JNIEnv *env, jstring class_name, jstring fieldName, jobject obj, jobject fieldValue);

void setStaticFieldObject(JNIEnv *env, jstring class_name, jstring fieldName, jobject fieldValue);

#endif /* REFINVOKE_H_ */
