package tech.sud.mgp.common.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import tech.sud.mgp.common.model.HSUserInfo

/**
 * 请求头拦截器
 */
class RequestHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("token", HSUserInfo.token)
            .build()
        return chain.proceed(request)
    }

}