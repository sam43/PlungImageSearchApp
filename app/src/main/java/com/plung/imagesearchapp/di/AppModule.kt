package com.plung.imagesearchapp.di

import com.plung.imagesearchapp.BuildConfig.BASE_URL
import com.plung.imagesearchapp.BuildConfig.DEBUG
import com.plung.imagesearchapp.api.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("HeaderInterceptor") headerInterceptor: Interceptor,
        @Named("CurlInterceptor") curlInterceptor: Interceptor,
        loggerInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(Constants.CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(Constants.READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(Constants.WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.addInterceptor(headerInterceptor)
        if (DEBUG) {
            okHttpClientBuilder.addInterceptor(loggerInterceptor)
            okHttpClientBuilder.addInterceptor(curlInterceptor)
        }
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashApi =
        retrofit.create(UnsplashApi::class.java)

    @Provides
    @Singleton
    @Named("HeaderInterceptor")
    fun provideHeaderInterceptor(): Interceptor = Interceptor {
        val requestBuilder = it.request().newBuilder()
        //hear you can add all headers you want by calling 'requestBuilder.addHeader(name ,  value)'
        it.proceed(requestBuilder
            .build())
    }

    @Provides
    @Singleton
    fun provideLoggerInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    @Named("CurlInterceptor")
    fun provideCurlInterceptor(logger: HttpLoggingInterceptor.Logger): Interceptor = Interceptor { chain ->
        val uft8 = Charset.forName("UTF-8")
        val request: Request = chain.request()
        var compressed = false
        val curlCmdBuilder = StringBuilder()
        curlCmdBuilder.append("curl ")
        curlCmdBuilder.append("-X ")
        curlCmdBuilder.append(request.method)
        val headers = request.headers
        var i = 0
        val count = headers.size
        while (i < count) {
            val name = headers.name(i)
            val value = headers.value(i)
            if ("Accept-Encoding".equals(name, ignoreCase = true) && "gzip".equals(
                    value,
                    ignoreCase = true
                )
            ) {
                compressed = true
            }
            curlCmdBuilder.append(" -H ")
            curlCmdBuilder.append("\"")
            curlCmdBuilder.append(name)
            curlCmdBuilder.append(": ")
            curlCmdBuilder.append(value)
            curlCmdBuilder.append("\"")
            i++
        }
        val requestBody = request.body
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            var charset = uft8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(uft8)
            }
            // try to keep to a single line and use a subshell to preserve any line breaks
            curlCmdBuilder.append(" --data $'")
            curlCmdBuilder.append(buffer.readString(charset!!).replace("\n", "\\n"))
            curlCmdBuilder.append("'")
        }
        curlCmdBuilder.append(if (compressed) " --compressed " else " ")
        curlCmdBuilder.append("\"")
        curlCmdBuilder.append(request.url)
        curlCmdBuilder.append("\"")
        logger.log("╭--- cURL (" + request.url + ")")
        logger.log(curlCmdBuilder.toString())
        logger.log("╰--- (copy and paste the above line to a terminal)")
        chain.proceed(request)
    }


    @Provides
    @Singleton
    fun provideLogger() = HttpLoggingInterceptor.Logger.DEFAULT

}