package com.londonx.be.tv

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.github.salomonbrys.kotson.fromJson
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.londonx.be.tv.entity.TVStation
import com.londonx.be.tv.util.db
import com.londonx.kutil.gson
import com.yanzhenjie.andserver.AndServer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import splitties.systemservices.wifiManager
import splitties.toast.longToast
import splitties.toast.toast
import splitties.views.textResource
import java.util.concurrent.TimeUnit


private const val SERVER_PORT = 8899

class MainActivity : AppCompatActivity() {
    private val andServer by lazy {
        AndServer.webServer(this)
            .port(SERVER_PORT)
            .timeout(10, TimeUnit.SECONDS)
            .build()
    }
    private val player by lazy { SimpleExoPlayer.Builder(this).build() }
    private val dataSourceFactory by lazy {
        DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "Mozilla/4.0 (compatible; MSIE 6.0; ztebw V1.0)")
        )
    }
    private var currentStation: TVStation? = null
        set(value) {
            if (field?.id == value?.id) {
                return
            }
            field = value
            notifyTvStationChange()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView.player = player

        andServer.startup()
        MainScope().launch {
            while (!isDestroyed) {
                if (!andServer.isRunning) {
                    tvInfo.textResource = R.string.starting_config_server
                    delay(1000)
                }
                val ip = wifiManager?.connectionInfo?.ipAddress
                if (ip == null) {
                    delay(1000)
                    continue
                }
                val ip4String = intToIP(ip)
                val webPage = "http://$ip4String:$SERVER_PORT/index.html"
                //QR code
                tvInfo.text = getString(R.string.fmt_cfg_page_, webPage)
                delay(5000)
            }
        }
        MainScope().launch {
            val savedStations = db.tvStationDao().getAll()
            if (savedStations.isEmpty()) {
                val defaultStations = withContext(Dispatchers.IO) {
                    val defaultStationsJson = String(
                        assets.open("default_tv_stations.json").readBytes()
                    )
                    gson.fromJson<Array<TVStation>>(defaultStationsJson)
                }
                db.tvStationDao().insert(*defaultStations)
            }
            changeStation(true)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (BuildConfig.DEBUG){
            toast("keyCode: $keyCode")
        }
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> changeStation(false)
            KeyEvent.KEYCODE_DPAD_DOWN -> changeStation(true)
            KeyEvent.KEYCODE_DPAD_LEFT -> {
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
            }
            KeyEvent.KEYCODE_DPAD_CENTER -> {
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        andServer.shutdown()
        player.release()
    }

    private fun intToIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }

    private fun changeStation(nextOrPrevious: Boolean) {
        MainScope().launch {
            val savedStations = db.tvStationDao().getAll()
            val index = savedStations.indexOfFirst {
                it.id == currentStation?.id
            } + if (nextOrPrevious) 1 else -1
            val targetIndex = when {
                index > savedStations.lastIndex -> 0
                index < 0 -> savedStations.lastIndex
                else -> index
            }
            currentStation = savedStations.getOrNull(targetIndex)
        }
    }

    private fun notifyTvStationChange() {
        val currentStation = this.currentStation
        if (currentStation == null) {
            longToast(R.string.no_saved_stations)
            return
        }

        val ms = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(currentStation.m3u8))
        player.prepare(ms, true, true)
        player.playWhenReady = true
    }
}
