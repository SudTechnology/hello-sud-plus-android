package tech.sud.mgp.common.http.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import tech.sud.mgp.common.model.HSUserInfo;

public class RequestHeaderInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String token = HSUserInfo.token;
        if (token != null) {
            builder.addHeader("Authorization", token);
        }
        return chain.proceed(builder.build());
    }
}