package com.londonx.kutil

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.Serializable
import java.security.MessageDigest


fun log(string: String) {
    Log.i("KUtil", string)
}

inline fun <reified T> Context.startActivity(vararg params: Pair<String, *>) {
    val intent = assembleIntent(*params)
    intent.setClass(this, T::class.java)
    startActivity(intent)
}

inline fun <reified T> Activity.startActivityForResult(
    requestCode: Int,
    vararg params: Pair<String, *>
) {
    val intent = assembleIntent(*params)
    intent.setClass(this, T::class.java)
    startActivityForResult(intent, requestCode)
}

fun assembleIntent(vararg params: Pair<String, *>): Intent {
    val intent = Intent()
    params.forEach {
        when (it.second) {
            is Bundle -> intent.putExtra(it.first, it.second as Bundle)
            is Parcelable -> intent.putExtra(it.first, it.second as Parcelable)
            is Serializable -> intent.putExtra(it.first, it.second as Serializable)
            is Array<*> -> intent.putExtra(it.first, it.second as Array<*>)
            is Boolean -> intent.putExtra(it.first, it.second as Boolean)
            is BooleanArray -> intent.putExtra(it.first, it.second as BooleanArray)
            is Byte -> intent.putExtra(it.first, it.second as Byte)
            is ByteArray -> intent.putExtra(it.first, it.second as ByteArray)
            is Char -> intent.putExtra(it.first, it.second as Char)
            is CharArray -> intent.putExtra(it.first, it.second as CharArray)
            is CharSequence -> intent.putExtra(it.first, it.second as CharSequence)
            is Double -> intent.putExtra(it.first, it.second as Double)
            is DoubleArray -> intent.putExtra(it.first, it.second as DoubleArray)
            is Float -> intent.putExtra(it.first, it.second as Float)
            is FloatArray -> intent.putExtra(it.first, it.second as FloatArray)
            is Int -> intent.putExtra(it.first, it.second as Int)
            is IntArray -> intent.putExtra(it.first, it.second as IntArray)
            is Long -> intent.putExtra(it.first, it.second as Long)
            is LongArray -> intent.putExtra(it.first, it.second as LongArray)
            is Short -> intent.putExtra(it.first, it.second as Short)
            is ShortArray -> intent.putExtra(it.first, it.second as ShortArray)
            is String -> intent.putExtra(it.first, it.second as String)
            else -> throw IllegalArgumentException("Context#startActivity with illegal param ${it.first}")
        }
    }
    return intent
}

fun Activity.immersion(statusBar: Boolean = true, navigationBar: Boolean = true) {
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    if (statusBar) {
        window.statusBarColor = Color.TRANSPARENT
    }
    if (navigationBar) {
        window.navigationBarColor = Color.TRANSPARENT
    }
}

val Activity.contentView: ViewGroup? get() = findViewById(android.R.id.content)

fun ViewGroup.forEach(f: (childView: View) -> Unit) {
    (0 until childCount).forEach { index -> f.invoke(this.getChildAt(index)) }
}

fun ViewGroup.forEachIndex(f: (index: Int, childView: View) -> Unit) {
    (0 until childCount).forEach { index -> f.invoke(index, this.getChildAt(index)) }
}

fun ImageView.setImageMipmap(mipmapName: String) {
    val resID = resources.getIdentifier(mipmapName, "mipmap", context.packageName)
    setImageResource(resID)
}

fun View.onLayout(f: (View) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener {
        f.invoke(this)
    }
}

fun EditText.setPasswordVisible(visible: Boolean) {
    val currentSelection = selectionStart
    this.transformationMethod = if (visible) {
        HideReturnsTransformationMethod.getInstance()
    } else {
        PasswordTransformationMethod.getInstance()
    }
    this.setSelection(currentSelection)
}

fun EditText.onEditableChange(afterChange: (text: Editable) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterChange.invoke(s ?: return)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun EditText.onTextChange(afterChange: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterChange.invoke(s?.toString() ?: return)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun Context.removePref(key: String) {
    val editor = getSharedPreferences(packageName + "_pref.xml", Context.MODE_PRIVATE).edit()
    editor.remove(key)
    editor.apply()
}

fun Context.savePref(key: String, value: Any) {
    val editor = getSharedPreferences(packageName + "_pref.xml", Context.MODE_PRIVATE).edit()
    //B F I L S
    when (value) {
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        is String -> editor.putString(key, value)
        else -> editor.putString(key, gson.toJson(value))
    }
    editor.apply()
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Context.loadPref(key: String, defaultValue: T? = null): T {
    val sp = getSharedPreferences(packageName + "_pref.xml", Context.MODE_PRIVATE)
    return when (defaultValue) {
        is Boolean -> sp.getBoolean(key, defaultValue) as T
        is Float -> sp.getFloat(key, defaultValue) as T
        is Int -> sp.getInt(key, defaultValue) as T
        is Long -> sp.getLong(key, defaultValue) as T
        is String -> sp.getString(key, defaultValue) as T
        is Collection<*> -> gson.fromJson(sp.getString(key, "[]"), object : TypeToken<T>() {}.type)
        else -> gson.fromJson(sp.getString(key, "{}"), object : TypeToken<T>() {}.type)
    }
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

fun Context.registerReceiver(
    vararg actions: String,
    onReceive: (intent: Intent?) -> Unit
): BroadcastReceiver {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (actions.contains(intent?.action)) {
                onReceive.invoke(intent)
            }
        }
    }
    val filter = IntentFilter()
    actions.forEach { filter.addAction(it) }
    registerReceiver(receiver, filter)
    return receiver
}

fun Context.safeUnregisterReceiver(receiver: BroadcastReceiver?) {
    try {
        unregisterReceiver(receiver)
    } catch (ignore: Exception) {
    }
}

fun Bitmap.compressToFile(
    context: Context,
    fmt: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    file: File? = null
): File {
    val extension = when (fmt) {
        Bitmap.CompressFormat.JPEG -> "jpg"
        Bitmap.CompressFormat.PNG -> "png"
        else -> if (Build.VERSION.SDK_INT > 30) {
            if (fmt == Bitmap.CompressFormat.WEBP_LOSSLESS || fmt == Bitmap.CompressFormat.WEBP_LOSSY) {
                "webp"
            } else {
                fmt.toString()
            }
        } else {
            if (fmt == Bitmap.CompressFormat.WEBP) {
                "webp"
            } else {
                fmt.toString()
            }
        }
    }
    val bos = ByteArrayOutputStream()
    this.compress(fmt, 100, bos)
    val bytes = bos.toByteArray()

    val actualFile = if (file == null) {
        val md5 = bytes.md5
        var f = File(context.filesDir, "$md5.$extension")
        var retry = 1
        while (f.exists()) {
            f = File(context.filesDir, "${md5}_$retry.$extension")
            retry++
        }
        f
    } else {
        file
    }
    actualFile.writeBytes(bytes)
    return actualFile
}

fun String?.isJSON(): Boolean {
    if (this == null) {
        return false
    }
    return try {
        JSONObject(this)
        true
    } catch (ignore: JSONException) {
        try {
            JSONArray(this)
            true
        } catch (ignore: JSONException) {
            false
        }
    }
}

fun String?.isIp4(): Boolean {
    if (this == null) {
        return false
    }
    return Patterns.IP_ADDRESS.matcher(this).matches()
}

fun String?.isURL(): Boolean {
    if (this == null) {
        return false
    }
    if (!this.startsWith("http") && !this.startsWith("HTTP")) {
        return false
    }
    return Patterns.WEB_URL.matcher(this).matches()
}

fun String?.isPhoneNumber(): Boolean {
    if (this == null) {
        return false
    }
    return Patterns.PHONE.matcher(this).matches()
}

val Int.dp: Float
    get() {
        return this.toFloat().dp
    }

val Float.dp: Float
    get() {
        return this * Resources.getSystem().displayMetrics.density
    }

fun Byte.toUInt(): Int = this.toInt() and 0xFF

fun Short.toUInt(): Int = this.toInt() and 0xFFFF

fun String.extractInt(): Int? {
    if (this.isEmpty()) {
        return null
    }
    var str2 = ""
    this.forEach { c ->
        if (c >= 48.toChar() && c <= 57.toChar()) {
            str2 += c
        }
    }
    if (str2.isEmpty()) {
        return null
    }
    return str2.toIntOrNull()
}

val ByteArray.md5: String
    get() {
        return MessageDigest
            .getInstance("MD5")
            .digest(this)
            .joinToString("") { "%02x".format(it) }
    }

inline fun <reified T> JSONObject.getOrNull(k: String): T? {
    if (!this.has(k)) {
        return null
    }
    return try {
        when (T::class) {
            String::class -> this.getString(k) as? T
            Int::class -> this.getInt(k) as? T
            JSONObject::class -> this.getJSONObject(k) as? T
            Boolean::class -> this.getBoolean(k) as? T
            Double::class -> this.getDouble(k) as? T
            Float::class -> this.getDouble(k).toFloat() as? T
            Long::class -> this.getLong(k) as? T
            JSONArray::class -> this.getJSONArray(k) as? T
            else -> null
        }
    } catch (ex: JSONException) {
        ex.printStackTrace()
        null
    }
}

/**
 * Created by London on 2017/5/26.
 * 拓展类
 */
val gson: Gson by lazy { Gson() }
