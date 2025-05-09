package com.evginozan.medireminder.di

import androidx.work.WorkManager
import com.evginozan.medireminder.core.notification.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val workerModule = module {
    single { WorkManager.getInstance(androidContext()) }
    single { NotificationHelper(androidContext()) }
}