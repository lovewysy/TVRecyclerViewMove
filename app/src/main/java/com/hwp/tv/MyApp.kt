package com.hwp.tv

import android.app.Application
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig

/**
 * @author hongweiping
 * @date 2023/3/23
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AutoSizeConfig.getInstance().designWidthInDp = 960
        AutoSizeConfig.getInstance().designHeightInDp = 540
        AutoSize.checkAndInit(this)
    }
}