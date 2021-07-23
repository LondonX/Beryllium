# TV Station Management
* http://[yourAndroidTVIp]:8899/

# TV Station Management API
* http://`your Android TV IP`:8899/tvStation
* Supported methods GET POST PUT DELETE
* All params in **QUERY PARAM**, not **~~Form PARAM~~**

*EX: http://192.168.5.24:8899/tvStation?tvStationId=7&title=CCTV-4K&m3u8=http://39.134.176.148/PLTV/88888888/224/3221226758/index.m3u8&sort=3*

| METHOD | Desc | Params |
|:-------|:-----|:---|
| GET | get all saved TV stations |   |
| POST | add TV stations | title: String <br> m3u8: String |
| PUT | modify TV stations | tvStationId: Int <br> title: String <br> m3u8: String <br> sort: Int |
| DELETE | delete TV stations | tvStationId: Int |

# m3u8 repositories
* [https://github.com/iptv-org/iptv](https://github.com/iptv-org/iptv)
* [https://www.haah.net/archives/5209.html](辉哥博客)