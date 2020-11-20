package com.linksteady.common.util;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static OkHttpClient okHttpClient;

    static {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        okHttpClient = client;
    }

    public static OkHttpClient getOkHttpClient()
    {
        return okHttpClient;
    }

    /**
     * 调用一次get请求
     * @param url
     * @return
     */
    public static String getRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            return responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 调用一次post请求
     * @param url
     * @param value
     * @return
     */
    public static String postRequest(String url,String value) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .post(RequestBody.create(MediaType.parse("text/plain"), value.getBytes()))
                .build();

        try {
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            return responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 调用一次post请求
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static String postRequestByFormBody(String url, Map<String, String> param){
        FormBody.Builder builder = new FormBody.Builder();
        param.forEach(builder::add);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().post(requestBody).url(url)
                .post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            return responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 调用一次post请求
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static String postRequestByJson(String url, String param){
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param))
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            return responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 调用一次get请求，使用basic认证
     * @param url
     * @return
     */
    @SneakyThrows
    public static String callTextPlainWithBasicAuth(String url,String username,String password) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Authorization", Credentials.basic(username, password))
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    /**
     * 调用一次post请求，使用basic认证
     * @param url
     * @return
     */
    @SneakyThrows
    public static String callPostWithBasicAuth(String url,String username,String password,String value) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Authorization", Credentials.basic(username, password))
                .post(RequestBody.create(MediaType.parse("text/plain"), value.getBytes()))
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    /**
     * 提交数据和文件
     * @param url
     * @param map
     * @param file
     * @return
     * @throws Exception
     */
    public static String postFileAndData(String url, Map<String,String> map, File file){
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", file.getName(), body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry<String,String> entry : map.entrySet()) {
                requestBody.addFormDataPart(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .post(requestBody.build())
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            return responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 请求url 进行文件的下载
     */
    public static InputStream downLoadFile(String url)
    {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            return responseBody.byteStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 上传文件到企业微信
     * @param url
     * @param file
     * @return
     */
    public static String postUploadMedia(String url,File file){
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream; charset=utf-8"), file);

            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addPart(Headers.of("Content-Disposition","form-data;name=\"media\";filename=\""+file.getName()+"\";filelength="+file.length()),fileBody);

        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/octet-stream")
                .post(requestBody.build())
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            return responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 上传图片到企业微信
     * @param url
     * @param file
     * @return
     */
    public static String postUploadImg(String url,File file){
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);

            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addPart(Headers.of("Content-Disposition","form-data;name=\"fieldNameHere\";filename=\""+file.getName()+"\";filelength="+file.length()),fileBody);

        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "image/png")
                .post(requestBody.build())
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            return responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
