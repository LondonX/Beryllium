package com.londonx.be.tv.web

import android.content.Context
import com.yanzhenjie.andserver.annotation.Config
import com.yanzhenjie.andserver.framework.config.WebConfig
import com.yanzhenjie.andserver.framework.website.AssetsWebsite

@Config
class AppWebConfig : WebConfig {
    override fun onConfig(context: Context?, delegate: WebConfig.Delegate?) {
        context ?: return
        delegate ?: return
        delegate.addWebsite(AssetsWebsite(context, "/build"))
    }
}