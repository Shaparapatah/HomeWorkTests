package com.geekbrains.tests.application

import android.app.Application
import com.geekbrains.tests.application.di.application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin



class GitHubApp: Application() {
    companion object {
        lateinit var instance: GitHubApp
    }


    override fun onCreate() {
        super.onCreate()
        instance = this


        startKoin {
            androidContext(applicationContext)
            modules(listOf(application))
        }
    }
}