package com.ubtechinc.nets.http;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @desc : restful http接口定义
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/5/15
 * @modifier:
 * @modify_time:
 */

public interface RestApi {

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> doPostWithForm(@Url String url, @FieldMap Map<String, String> maps);

    @POST()
    Observable<ResponseBody> doPostWithJson(@Url String url, @Body RequestBody body);

    @PUT()
    @FormUrlEncoded
    Observable<ResponseBody> doPutWithForm(@Url String url, @FieldMap Map<String, String> maps);

    @PUT()
    Observable<ResponseBody> doPutWithJson(@Url String url, @Body RequestBody body);

    @GET()
    Observable<ResponseBody> doGet(@Url String url, @QueryMap Map<String, String> maps);

    @DELETE()
    Observable<ResponseBody> doDelete(@Url String url, @QueryMap Map<String, String> maps);

    @DELETE()
    Observable<ResponseBody> doDelete(@Url String url);


    @POST()
    Observable<ResponseBody> doPostWithJsonAndHeader(@Url String url, @Body RequestBody body, @HeaderMap Map<String, String> headers);

    @PUT()
    Observable<ResponseBody> doPutWithJsonAndHeader(@Url String url, @Body RequestBody body, @HeaderMap Map<String, String> headers);

    @GET()
    Observable<ResponseBody> doGetWithHeader(@Url String url, @QueryMap Map<String, String> maps, @HeaderMap Map<String, String> headers);

    @PATCH
    Observable<ResponseBody> doPatchWithJsonAndHeaders(@Url String url, @Body RequestBody body,  @HeaderMap Map<String, String> headers);

    @PATCH
    Observable<ResponseBody> doPatch(@Url String url, @Body RequestBody body);

}
