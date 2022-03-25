package tech.sud.mgp.hello.common.http.retrofit;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.common.http.interceptor.RequestBaseUrlInterceptor;
import tech.sud.mgp.hello.common.http.interceptor.RequestHeaderInterceptor;
import tech.sud.mgp.hello.common.http.param.BaseUrlManager;

public class RetrofitManager {

    private static RetrofitManager INSTANCE;
    private final Retrofit mRetrofit;
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private RetrofitManager() {
        OkHttpClient okHttpClient = createOkHttpClient();
        mRetrofit = new Retrofit.Builder().baseUrl(BaseUrlManager.getBaseUrl())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(okHttpClient)
                .build();
    }

    public static RetrofitManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RetrofitManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitManager();
                }
            }
        }
        return INSTANCE;
    }

    private OkHttpClient createOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE); // debug模式才打印
        Builder builder = new Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new RequestHeaderInterceptor())
                .addInterceptor(new RequestBaseUrlInterceptor())
                .addInterceptor(loggingInterceptor); // 添加控制台网络请求消息拦截器,直接在控制台输出
        return builder.build();
    }

    /**
     * 创建网络请求方法
     *
     * @param service 方法类
     */
    public static <T> T createMethod(final Class<T> service) {
        return getInstance().mRetrofit.create(service);
    }

}