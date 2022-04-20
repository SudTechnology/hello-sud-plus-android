package tech.sud.mgp.hello.common.http.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.model.SudMetaModel;

public class RequestHeaderInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String token = HSUserInfo.token;
        if (token != null) {
            builder.addHeader("Authorization", token);
        }
        builder.addHeader("Sud-Meta", SudMetaModel.buildString());
        return chain.proceed(builder.build());
    }
}
