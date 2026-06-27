package com.therealdeal.kotlift

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.therealdeal.kotlift.koin.appModule
import com.therealdeal.kotlift.koin.dataModule
import com.therealdeal.kotlift.koin.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.osmdroid.config.Configuration

class MainApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().apply {
            load(this@MainApplication, getSharedPreferences("osmdroid", MODE_PRIVATE))
            userAgentValue = packageName
        }


        startKoin {
            androidContext(this@MainApplication)
            modules(appModule, dataModule, networkModule)
        }
    }

    // only for coil
    @SuppressLint("ObsoleteSdkInt")
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
}