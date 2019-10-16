package com.kunfei.bookshelf.model.impl;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.Map;

/**
 * Created by GKF on 2018/1/29.
 * post
 */

public interface IHttpPostApi {

    @FormUrlEncoded
    @POST
    Observable<Response<String>> postMap(@Url String url,
                                         @FieldMap(encoded = true) Map<String, String> fieldMap,
                                         @HeaderMap Map<String, String> headers);

    @POST
    Observable<Response<String>> postJson(@Url String url,
                                          @Body RequestBody body,
                                          @HeaderMap Map<String, String> headers);
}
