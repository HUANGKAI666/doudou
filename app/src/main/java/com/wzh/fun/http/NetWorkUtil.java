package com.wzh.fun.http;


import com.wzh.fun.http.api.ImageJokeApi;
import com.wzh.fun.http.api.TextJokeApi;
import com.wzh.fun.http.api.WeatherApi;
import com.wzh.fun.utils.Constant;

//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetWorkUtil {
    private static OkHttpClient okHttpClient;
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
    private static TextJokeApi textJokeApi;
    private static ImageJokeApi imageJokeApi;
    private static WeatherApi weatherApi;

    /**
     * 初始化okhttp
     */
    public static void initOkhttp() {
        if (okHttpClient == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
            okHttpClient = new OkHttpClient.Builder().build();
        }

    }

    /**
     * 获取最新文本笑话
     *
     * @return
     */
    public static TextJokeApi getTextJokeApi() {
        initOkhttp();
        if (textJokeApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.RANDOM_BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            textJokeApi = retrofit.create(TextJokeApi.class);
        }
        return textJokeApi;
    }



    /**
     * 获取最新图片笑话
     *
     * @return
     */
    public static ImageJokeApi getImageJokeApi() {
        initOkhttp();
        if (imageJokeApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.RANDOM_BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            imageJokeApi = retrofit.create(ImageJokeApi.class);
        }
        return imageJokeApi;
    }


//    public static void getJsoupImageJokeApi(Subscriber sb) {
//        Observable.create(new Observable.OnSubscribe<Document>() {
//            @Override
//            public void call(Subscriber<? super Document> subscriber) {
//                Document doc = null;
//                try {
//                    doc = Jsoup.connect("http://www.qiushibaike.com/")
//                            .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101" +
//                                    " Firefox/32.0").get();
//                    subscriber.onNext(doc);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(sb);
//
//    }

    /**
     * 获取天气数据
     *
     * @return
     */
    public static WeatherApi getWeatherApi() {
        initOkhttp();
        if (weatherApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_WHEATHER)
                    .client(okHttpClient)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            weatherApi = retrofit.create(WeatherApi.class);
        }
        return weatherApi;
    }

}
