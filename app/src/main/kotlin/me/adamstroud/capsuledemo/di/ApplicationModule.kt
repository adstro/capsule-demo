package me.adamstroud.capsuledemo.di

import android.net.Uri
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import me.adamstroud.capsuledemo.BuildConfig
import me.adamstroud.capsuledemo.api.WebService
import me.adamstroud.capsuledemo.model.Multimedia
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import org.threeten.bp.format.DateTimeFormatterBuilder
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber

@Module
class ApplicationModule {
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(object {
            @ToJson
            fun toJson(uri: Uri): String = uri.toString()

            @FromJson
            fun fromJson(json: String): Uri? = Uri.parse(json)
        })

        .add(object {
            private val offsetDateTimeFormatter = DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(ISO_LOCAL_DATE_TIME)
                .appendOffset("+HHMM", "Z")
                .toFormatter().withChronology(IsoChronology.INSTANCE)

            @ToJson
            fun toJson(offsetDateTime: OffsetDateTime): String = offsetDateTime.format(offsetDateTimeFormatter)

            @FromJson
            fun fromJson(json: String): OffsetDateTime = OffsetDateTime.parse(json, offsetDateTimeFormatter)
        })
        .add(Multimedia.Type::class.java, EnumJsonAdapter.create(Multimedia.Type::class.java).withUnknownFallback(Multimedia.Type.UNSUPPORTED))
        .add(Multimedia.CropName::class.java, EnumJsonAdapter.create(Multimedia.CropName::class.java).withUnknownFallback(Multimedia.CropName.UNSUPPORTED))
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor { message ->
                Timber.tag("OkHttp").d(message)
            }.also { interceptor ->
                interceptor.level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }.build()

    @Provides
    fun provideWebService(okHttpClient: OkHttpClient,
                          moshi: Moshi): WebService = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create()
}