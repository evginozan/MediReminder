package com.evginozan.medireminder

import android.app.Application
import com.evginozan.medireminder.di.appModules
import com.evginozan.medireminder.service.MedicineTrackerWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MedicineApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MedicineApp)
            modules(appModules)
        }

        MedicineTrackerWorker.scheduleWork(this)
    }
}