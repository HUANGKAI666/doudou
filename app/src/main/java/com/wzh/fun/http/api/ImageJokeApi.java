package com.wzh.fun.http.api;

import com.wzh.fun.entity.ImageJokeEntity;
import com.wzh.fun.http.ApiResponseWraperNoData;
import com.wzh.fun.http.RequestParam;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;


public interface ImageJokeApi {
    @GET("randJoke.php")
    Observable<ApiResponseWraperNoData<ImageJokeEntity>> getImageJoke(@QueryMap RequestParam param);
}
