package tech.sud.mgp.common.http.interceptor

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import tech.sud.mgp.common.http.param.IBaseUrl

/**
 * 请求BaseUrl拦截器
 * 可在此处实现BaseUrl修改逻辑
 */
class RequestBaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        //获取request
        val request = chain.request()
        //获取request的创建者builder
        val builder = request.newBuilder()
        //优先处理完整的URL传递过来
        val fullUrl = request.header(IBaseUrl.KEY_FULL_URL)
        if (!fullUrl.isNullOrEmpty()) {
            builder.removeHeader(IBaseUrl.KEY_FULL_URL)
            //从request中获取原有的HttpUrl实例oldHttpUrl
            val oldHttpUrl = request.url
            //如果定义的map中存在这个url，就构建新的url，否则直接进行请求
            val urlBuilder = oldHttpUrl.newBuilder()//使用旧的url构建一个新的Builder

            val newFullUrl = fullUrl.toHttpUrl() //这个是包含路径的
            val pathSegments = newFullUrl.pathSegments
            val newUrlBuilder = urlBuilder
                .scheme(newFullUrl.scheme)
                .host(newFullUrl.host)
                .port(newFullUrl.port)
            pathSegments.forEach {
                newUrlBuilder.addPathSegment(it)
            }
            return chain.proceed(builder.url(newUrlBuilder.build()).build())
        }

        //从request中获取headers，通过给定的键url_name
        val headUrl = request.header(IBaseUrl.KEY_BASE_URL)

        if (headUrl.isNullOrEmpty()) {
            return chain.proceed(request)
        }

        //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
        builder.removeHeader(IBaseUrl.KEY_BASE_URL)

        //匹配获得新的BaseUrl
        val newBaseUrl = headUrl.toHttpUrl()
        //从request中获取原有的HttpUrl实例oldHttpUrl
        val oldHttpUrl = request.url
        //如果定义的map中存在这个url，就构建新的url，否则直接进行请求
        val urlBuilder = oldHttpUrl.newBuilder()//使用旧的url构建一个新的Builder
        val newFullUrl = urlBuilder
            .scheme(newBaseUrl.scheme)
            .host(newBaseUrl.host)
            .port(newBaseUrl.port)
            .build()//修改url的协议，域名，端口为新的url，可以根据自己的需求修改其他部分比如path，params
        return chain.proceed(builder.url(newFullUrl).build())
    }

}