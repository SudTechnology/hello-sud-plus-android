package tech.sud.mgp.hello.common.http.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;

public class RequestBaseUrlInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // 获取request
        Request request = chain.request();
        // 获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        // 优先处理完整的URL传递过来
        String fullUrl = request.header(IBaseUrl.KEY_FULL_URL);
        if (fullUrl != null && fullUrl.length() > 0) {
            builder.removeHeader(IBaseUrl.KEY_FULL_URL);
            // 从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldHttpUrl = request.url();
            // 如果定义的map中存在这个url，就构建新的url，否则直接进行请求
            HttpUrl.Builder urlBuilder = oldHttpUrl.newBuilder(); // 使用旧的url构建一个新的Builder

            HttpUrl newFullUrl = HttpUrl.parse(fullUrl); // 这个是包含路径的
            if (newFullUrl != null) {
                HttpUrl.Builder newUrlBuilder = urlBuilder
                        .scheme(newFullUrl.scheme())
                        .host(newFullUrl.host())
                        .port(newFullUrl.port());
                List<String> pathSegments = newFullUrl.pathSegments();
                for (String pathSegment : pathSegments) {
                    newUrlBuilder.addPathSegment(pathSegment);
                }
                return chain.proceed(builder.url(newUrlBuilder.build()).build());
            }
        }

        // 从request中获取headers，通过给定的键url_name
        String headUrl = request.header(IBaseUrl.KEY_BASE_URL);
        if (headUrl == null || headUrl.length() == 0) {
            return chain.proceed(request);
        }

        // 如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
        builder.removeHeader(IBaseUrl.KEY_BASE_URL);
        // 匹配获得新的BaseUrl
        HttpUrl newBaseUrl = HttpUrl.parse(headUrl);
        if (newBaseUrl != null) {
            // 从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldHttpUrl = request.url();
            // 如果定义的map中存在这个url，就构建新的url，否则直接进行请求
            HttpUrl.Builder urlBuilder = oldHttpUrl.newBuilder();// 使用旧的url构建一个新的Builder
            HttpUrl newFullUrl = urlBuilder
                    .scheme(newBaseUrl.scheme())
                    .host(newBaseUrl.host())
                    .port(newBaseUrl.port())
                    .build();//修改url的协议，域名，端口为新的url，可以根据自己的需求修改其他部分比如path，params
            return chain.proceed(builder.url(newFullUrl).build());
        }
        return chain.proceed(request);
    }

}
