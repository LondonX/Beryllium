package com.londonx.kutil

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Created by London on 2017/7/25.
 * update on 2020-02-06
 * networker
 */
private val client: OkHttpClient by lazy {
    OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build()
}
private var globalResponder: (statusCode: Int, body: String?) -> Boolean = { _, _ -> false }
private val mainScope = MainScope()

fun networkerGlobalResponder(responder: (statusCode: Int, body: String?) -> Boolean) {
    globalResponder = responder
}

private fun doRequestAsync(
    call: Call,
    f: (statusCode: Int, body: String?) -> Unit,
    charset: Charset
) {
    mainScope.launch {
        val resp = withContext(Dispatchers.IO) { doRequest(call, charset).also { call.cancel() } }
        if (globalResponder.invoke(resp?.statusCode ?: 0, resp?.body)) {
            return@launch
        }
        f.invoke(resp?.statusCode ?: 0, resp?.body)
    }
    return
}

private fun doRequest(call: Call, charset: Charset): MyResponse? {
    try {
        if (call.isCanceled()) {
            return null
        }
        val response = call.execute()
        val status = response.code
        val bs = response.body?.bytes() ?: return null
        val body = String(bs, charset)
        response.close()
        call.cancel()
        if (status !in (200..300)) {
            Log.e("Networker", call.request().url.toString() + " statusCode:" + status)
        }
        return MyResponse(status, body)
    } catch (ex: IOException) {
        ex.printStackTrace()
    } finally {
        call.cancel()
    }
    return null
}

fun String.urlParam(param: Param? = null): String {
    var temp = this
    param?.map?.forEach { it ->
        temp = temp.replace("{${it.key}}", it.value.toString())
    }
    return temp
}

fun String.httpGet(
    param: Param? = null,
    charset: Charset = Charset.defaultCharset(),
    f: (statusCode: Int, body: String?) -> Unit
): Call {
    val call = client.newCall(
        Request.Builder()
            .url(this + (param?.toGetParam() ?: ""))
            .build()
    )
    doRequestAsync(call, f, charset)
    return call
}

suspend fun String.httpGetSync(
    param: Param? = null,
    charset: Charset = Charset.defaultCharset()
): MyResponse? {
    val call = client.newCall(
        Request.Builder()
            .url(this + (param?.toGetParam() ?: ""))
            .build()
    )
    return withContext(Dispatchers.IO) {
        doRequest(call, charset)
    }
}

fun String.httpPost(
    param: Param? = null,
    json: Boolean = false,
    charset: Charset = Charset.defaultCharset(),
    f: (statusCode: Int, body: String?) -> Unit
): Call {
    val body = if (json) param?.toJSONBody() else param?.toFormBody()
    val builder = Request.Builder()
    if (body != null) {
        builder.post(body)
    }
    val call = client.newCall(
        builder
            .url(this)
            .build()
    )
    doRequestAsync(call, f, charset)
    return call
}

fun String.httpPut(
    param: Param? = null,
    json: Boolean = false,
    charset: Charset = Charset.defaultCharset(),
    f: (statusCode: Int, body: String?) -> Unit
): Call {
    val body = if (json) param?.toJSONBody() else param?.toFormBody()
    val builder = Request.Builder()
    if (body != null) {
        builder.put(body)
    }
    val call = client.newCall(
        builder
            .url(this)
            .build()
    )
    doRequestAsync(call, f, charset)
    return call
}

fun String.httpDelete(
    param: Param? = null,
    json: Boolean = false,
    charset: Charset = Charset.defaultCharset(),
    f: (statusCode: Int, body: String?) -> Unit
): Call {
    val body = if (json) param?.toJSONBody() else param?.toFormBody()
    val builder = Request.Builder()
    if (body != null) {
        builder.delete(body)
    }
    val call = client.newCall(
        builder
            .url(this)
            .build()
    )
    doRequestAsync(call, f, charset)
    return call
}

fun String.httpRawPut(
    body: RequestBody?,
    charset: Charset = Charset.defaultCharset()
): MyResponse? {
    val builder = Request.Builder()
    if (body != null) {
        builder.put(body)
    }
    val call = client.newCall(
        builder
            .url(this)
            .build()
    )
    return doRequest(call, charset)
}

open class Param(k: String? = null, v: Any? = null) {
    val map = LinkedHashMap<String, Any?>()

    init {
        if (!k.isNullOrBlank()) {
            map[k] = v
        }
    }

    fun put(k: String, v: Any?): Param {
        map[k] = v
        return this
    }

    fun remove(k: String): Param {
        map.remove(k)
        return this
    }

    fun toGetParam(): String {
        val sb = StringBuilder()
        map.forEach {
            if (sb.isEmpty()) {
                sb.append("?")
            } else {
                sb.append("&")
            }
            sb.append("${it.key}=${URLEncoder.encode(it.value.toString(), "utf-8")}")
        }
        return sb.toString()
    }

    fun toFormBody(): RequestBody {
        val builder = FormBody.Builder()
        map.forEach {
            builder.add(it.key, it.value.toString())
        }
        return builder.build()
    }

    fun toJSONBody(): RequestBody =
        Gson().toJson(map).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

data class MyResponse(val statusCode: Int, val body: String?)
