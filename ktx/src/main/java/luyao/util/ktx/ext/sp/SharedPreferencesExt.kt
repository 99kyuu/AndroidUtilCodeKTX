package luyao.util.ktx.ext.sp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Created by luyao
 * on 2019/7/8 14:56
 */
inline fun SharedPreferences.edit(commit: Boolean = false, action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    action(editor)
    if (commit)
        editor.commit()
    else
        editor.apply()
}

fun Context.sp(name: String = packageName, mode: Int = Context.MODE_PRIVATE) =
    getSharedPreferences(name, mode)

fun Activity.sp(name: String = packageName, mode: Int = Context.MODE_PRIVATE) =
    getSharedPreferences(name, mode)


fun <T> Context.putSpValue(name: String, value: T) = sp().edit {
    when (value) {
        is Long -> putLong(name, value)
        is String -> putString(name, value)
        is Int -> putInt(name, value)
        is Boolean -> putBoolean(name, value)
        is Float -> putFloat(name, value)
        else -> putString(name, serialize(value))
    }
}

fun <T> Activity.putSpValue(name: String, value: T) = sp().edit {
    when (value) {
        is Long -> putLong(name, value)
        is String -> putString(name, value)
        is Int -> putInt(name, value)
        is Boolean -> putBoolean(name, value)
        is Float -> putFloat(name, value)
        else -> putString(name, serialize(value))
    }
}

fun <T> Context.getSpValue(key: String, default: T) :T{
    sp().run {
        val result = when (default) {
            is Long -> getLong(key, default)
            is String -> getString(key, default)
            is Int -> getInt(key, default)
            is Boolean -> getBoolean(key, default)
            is Float -> getFloat(key, default)
            else -> deSerialization(getString(key, serialize(default)))
        }
        return result as T
    }
}

private fun <T> serialize(obj: T): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(
        byteArrayOutputStream
    )
    objectOutputStream.writeObject(obj)
    var serStr = byteArrayOutputStream.toString("ISO-8859-1")
    serStr = URLEncoder.encode(serStr, "UTF-8")
    objectOutputStream.close()
    byteArrayOutputStream.close()
    return serStr
}

private fun <T> deSerialization(str: String?): T {
    val redStr = URLDecoder.decode(str, "UTF-8")
    val byteArrayInputStream = ByteArrayInputStream(
        redStr.toByteArray(charset("ISO-8859-1"))
    )
    val objectInputStream = ObjectInputStream(
        byteArrayInputStream
    )
    val obj = objectInputStream.readObject() as T
    objectInputStream.close()
    byteArrayInputStream.close()
    return obj
}