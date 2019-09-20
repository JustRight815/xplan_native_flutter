package com.module.common.utils

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import java.util.*

/**
 * 存储封装
 */
object MMKVUtil {
    var mmkv: MMKV =  MMKV.defaultMMKV()

    fun save(key: String, value: Any?) {
        when (value) {
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            is Nothing -> return
        }
    }

    fun <T : Parcelable> save(key: String, t: T?) {
        if(t ==null){
            return
        }
        mmkv.encode(key, t)
    }

    fun save(key: String, sets: Set<String>?) {
        if(sets ==null){
            return
        }
        mmkv.encode(key, sets)
    }

    fun getString(key: String): String {
        return mmkv.decodeString(key)
    }
    fun getString(key: String,defaultValue:String): String {
        return mmkv.decodeString(key, defaultValue)
    }

    fun getInt(key: String): Int {
        return mmkv.decodeInt(key)
    }

    fun getInt(key: String,defaultValue:Int): Int {
        return mmkv.decodeInt(key,defaultValue)
    }

    fun getDouble(key: String): Double {
        return mmkv.decodeDouble(key)
    }

    fun getDouble(key: String,defaultValue:Double): Double {
        return mmkv.decodeDouble(key,defaultValue)
    }

    fun getLong(key: String): Long {
        return mmkv.decodeLong(key)
    }

    fun getLong(key: String,defaultValue:Long): Long {
        return mmkv.decodeLong(key,defaultValue)
    }

    fun getBoolean(key: String): Boolean {
        return mmkv.decodeBool(key)
    }

    fun getBoolean(key: String,defaultValue: Boolean): Boolean {
        return mmkv.decodeBool(key, defaultValue)
    }

    fun getFloat(key: String): Float {
        return mmkv.decodeFloat(key, 0F)
    }

    fun getFloat(key: String,defaultValue:Float): Float {
        return mmkv.decodeFloat(key, defaultValue)
    }

    fun getByteArray(key: String): ByteArray {
        return mmkv.decodeBytes(key)
    }

    fun <T : Parcelable> getParcelable(key: String, tClass: Class<T>): T? {
        return mmkv.decodeParcelable(key, tClass)
    }

    fun getStringSet(key: String): Set<String>{
        return mmkv.decodeStringSet(key, Collections.emptySet())
    }

    fun removeKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    fun clearAll() {
        mmkv.clearAll()
    }
}
