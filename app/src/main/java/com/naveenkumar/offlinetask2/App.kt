package com.naveenkumar.offlinetask2

import android.app.Application
import com.devs.acr.AutoErrorReporter

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        AutoErrorReporter.get(this)
            .setEmailAddresses("naveenkumar70271@gmail.com")
            .setEmailSubject("Offline Task 2 Auto Crash Report")
            .start()
    }
}