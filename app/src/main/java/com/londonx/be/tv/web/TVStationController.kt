package com.londonx.be.tv.web

import com.londonx.be.tv.entity.TVStation
import com.londonx.be.tv.util.db
import com.londonx.kutil.gson
import com.yanzhenjie.andserver.annotation.*

@RestController
class TVStationController {

    @PostMapping("/tvStation/insert")
    fun insertSync(
        @RequestParam("title") title: String,
        @RequestParam("m3u8") m3u8: String
    ) {
        val maxSort = db.tvStationDao().getAllSync().lastOrNull()?.sort ?: -1
        db.tvStationDao().insertSync(TVStation(title, m3u8, maxSort + 1))
    }

    @DeleteMapping("/tvStation/delete")
    fun deleteSync(@RequestParam("tvStationId") tvStationId: Long) {
        val tvStation = db.tvStationDao().getAllSync().find { it.id == tvStationId } ?: return
        db.tvStationDao().deleteSync(tvStation)
    }

    @PutMapping("/tvStation/update")
    fun updateSync(
        @RequestParam("tvStationId") tvStationId: Long,
        @RequestParam("title") title: String,
        @RequestParam("m3u8") m3u8: String,
        @RequestParam("sort") sort: Int
    ) {
        db.tvStationDao().updateSync(TVStation(title, m3u8, sort, tvStationId))
    }

    @GetMapping("/tvStation/getAll")
    fun getAll(): String {
        return gson.toJson(db.tvStationDao().getAllSync())
    }
}