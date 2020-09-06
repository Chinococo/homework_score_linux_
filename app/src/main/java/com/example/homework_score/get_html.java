package com.example.homework_score;


import android.util.Log;

import org.apache.http.NameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class get_html extends Thread {
    HashMap<java.lang.String, List<Cookie>> cookies = new HashMap<>();
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    OkHttpClient client = new OkHttpClient.Builder().build();
    java.lang.String url;
    ArrayList<NameValuePair> headers = new ArrayList<>();
    java.lang.String metholn;
    FormBody.Builder post_data = new FormBody.Builder();
    ArrayList<NameValuePair> upload_parmter;
    HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<okhttp3.Cookie>>();
    String result;

    public get_html(String url, String metholn) {
        this.metholn = metholn;
        this.url = url;
    }

    public get_html(java.lang.String url, String metholn, ArrayList<NameValuePair> headers, ArrayList<NameValuePair> upload_parmter) {
        this.url = url;
        this.metholn = metholn;
        this.headers = headers;
        this.upload_parmter = upload_parmter;
    }

    public get_html(java.lang.String url, java.lang.String metholn, ArrayList<NameValuePair> upload_parmter) {
        this.url = url;
        this.metholn = metholn;
        this.upload_parmter = upload_parmter;
    }

    String returndata() {
        return result;
    }

    @Override
    public void run() {
        if (metholn.equals("GET")) {
            builder.cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(final HttpUrl url, List<okhttp3.Cookie> cookies) {
                    String cookie = cookies.toString();
                    Log.e("url1", url.toString());
                    Log.e("cookie", cookies.toString());
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<okhttp3.Cookie> loadForRequest(HttpUrl url) {

                    Log.e("url2", url.host());
                    List<okhttp3.Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            });
            client = builder.build();
            Request.Builder request = new Request.Builder();
            for (int i = 0; i < headers.size(); i++)
                request.addHeader(headers.get(i).getName(), headers.get(i).getValue());
            for (int i = 0; i < upload_parmter.size(); i++)
                if (i == 0)
                    url += "?" + upload_parmter.get(i).getName() + "=" + upload_parmter.get(i).getValue();
                else
                    url += ("&" + upload_parmter.get(i).getName() + "=" + upload_parmter.get(i).getValue());

            Log.e("url", url);
            request
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0")
                    .addHeader("Accept", "*/*")
                    .addHeader("Accept-Encoding", "gzip");
            try {
                Response response = client.newCall(request.build()).execute();
                java.lang.String html = new java.lang.String(response.body().bytes(), "UTF-8");
                //System.out.println(html);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (metholn.equals("POST")) {
            //FormBody.Builder form = new FormBody.Builder();
            for (int i = 0; i < upload_parmter.size(); i++)
                post_data.add(upload_parmter.get(i).getName(), upload_parmter.get(i).getValue());
            builder.cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(final HttpUrl url, List<okhttp3.Cookie> cookies) {
                    String cookie = cookies.toString();
                    Log.e("url1", url.toString());
                    Log.e("cookie", cookies.toString());
                    cookieStore.put(url.host(), cookies);

                }

                @Override
                public List<okhttp3.Cookie> loadForRequest(HttpUrl url) {
                    Log.e("url2", url.host());
                    List<okhttp3.Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            });
            client = builder.build();
            Request.Builder request = new Request.Builder();
            request.url(url);
            request.post(post_data.build());
            for (int i = 0; i < headers.size(); i++)
                request.addHeader(headers.get(i).getName(), headers.get(i).getValue());
            try {
                Response response = client.newCall(request.build()).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (metholn.equals("just_getdata")) {
            Log.e("hello", "Log.e(\"hello\",\"bug\");");
            client = builder.build();
            try {
                Request.Builder request = new Request.Builder();
                Response response = client.newCall(new Request.Builder().url(url).build()).execute();
                result = response.body().string().toString();
                //System.out.println(result);
                System.out.println("fin");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.e("hello", "bug");
        }
    }
    /*
    CookieJar cahace = new CookieJar() {
        @Override
        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            cookies.put(httpUrl.host(), list);
            System.out.println(httpUrl.host());
            System.out.println(list.toString());
        }

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
            List<okhttp3.Cookie> cookie = cookies.get(httpUrl.host());
            return cookies != null ? cookie : new ArrayList<Cookie>();
        }
    };

     */
}
