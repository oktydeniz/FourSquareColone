package com.oktydeniz.k_foursquare.parse

import android.app.Application
import com.parse.Parse

class ParseSettings : Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG)
        Parse.initialize(
            //Put Your Parse Server info
            Parse.Configuration.Builder(this)
                .applicationId("T9......")
                .clientKey("rN6.....i7548...54")
                .server("https://...../")
                .build()
        )
    }
}