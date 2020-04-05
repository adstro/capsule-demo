package me.adamstroud.capsuledemo

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import me.adamstroud.capsuledemo.di.ApplicationComponent
import me.adamstroud.capsuledemo.di.DaggerApplicationComponent
import timber.log.Timber

class CapsuleDemoApplication : Application() {
    val applicationComponent: ApplicationComponent = DaggerApplicationComponent.create()

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, tag, "${Thread.currentThread().name}: $message", t)
                }
            })
        }
    }
}