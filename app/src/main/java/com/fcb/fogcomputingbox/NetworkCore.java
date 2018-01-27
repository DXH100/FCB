package com.fcb.fogcomputingbox;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by wangtao on 2017/guide2/21.
 */

public class NetworkCore {

    private static OkHttpClient mOkHttpClient = null;

    static SSLContext sslContext;

    private Handler mHandler;

    private static NetworkCore mNetworkCore;

    private NetworkCore() {
        mHandler = new Handler(Looper.getMainLooper());
        overlockCard();
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory())
                .addInterceptor(getLogInterceptor(UIUtils.getContext()))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
    }

    public static NetworkCore getInstance() {
        if (mNetworkCore == null) {
            synchronized (NetworkCore.class) {
                if (mNetworkCore == null) {
                    mNetworkCore = new NetworkCore();
                }
            }
        }
        return mNetworkCore;
    }

    public void doPost(String tags,INetWorkResult mINetWorkResult, final int msgId, String urlName, final HashMap<String, Object> params, final Class t) {
        mNetworkCore.postData(tags,mINetWorkResult, msgId, urlName, params, t);
    }

    public void doAuthorizationPost(String tags,final INetWorkResult mINetWorkResult, final int msgId, final String urlName, final HashMap<String, Object> params, final Class t) {

        mNetworkCore.postAuthorizationData(tags,mINetWorkResult, msgId, urlName, params, t);
    }

    //取消请求
    public void cancel(String tags) {
        if(mOkHttpClient != null) {
            for(Call call : mOkHttpClient.dispatcher().queuedCalls()) {
                if(call.request().tag().equals(tags))
                    call.cancel();
            }
            for(Call call : mOkHttpClient.dispatcher().runningCalls()) {
                if(call.request().tag().equals(tags))
                    call.cancel();
            }
        }
    }



    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private void postData(String tags,INetWorkResult mINetWorkResult, int msgId, String urlName, HashMap<String, Object> params, Class t) {
        if (TextUtils.isEmpty(urlName)) {
            return;
        }
        //忽略所有证书
        overlockCard();
        String content = GsonUtil.GsonToString(params);
        RequestBody body = RequestBody.create(JSON_TYPE, content);
        final Request req = new Request.Builder().url(Constants.BASEURL + "/" + urlName).post(body).addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                .tag(tags)
                .build();

        dealHttps(req, mINetWorkResult, msgId, urlName, params, t);

    }

    private void postAuthorizationData(String tags,INetWorkResult mINetWorkResult, int msgId, String urlName, HashMap<String, Object> params, Class t) {

        if (TextUtils.isEmpty(urlName)) {
            return;
        }
        //忽略所有证书
//        overlockCard();
//        if (params == null) {
//            params = new1 HashMap<>();
//        }
//        String token = SPUtils.getInstance().getString("token");
//        if (TextUtils.isEmpty(token)) {
//            context.sendBroadcast(new1 Intent("finish"));
//            Intent intent = new1 Intent(context, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//            return;
//        }


        String content = GsonUtil.GsonToString(params);
        ;
        RequestBody body = RequestBody.create(JSON_TYPE, content);
        final Request req = new Request.Builder().url(Constants.BASEURL + "/" + urlName).post(body).
                addHeader("Content-Type", "application/json; charset=utf-8").addHeader("Authorization","").
                addHeader("AppVersion", AppUtils.getAppVersionCode() + "").
                addHeader("Accept-Language", Locale.getDefault().getLanguage())
                .tag(tags)
                .build();
        LogUtils.e("request body:" + content + ",token:" + "bearer ");

        dealHttps(req, mINetWorkResult, msgId, urlName, params, t);

    }

    private void dealHttps(Request req, final INetWorkResult mINetWorkResult, final int msgId, String urlName, HashMap<String, Object> params, final Class t) {
        mOkHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mINetWorkResult != null) {
                            mINetWorkResult.error(msgId, getNetErrorObj());
                        }
                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();

                try {
                    if (TextUtils.isEmpty(result)) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (mINetWorkResult != null) {

                                    mINetWorkResult.error(msgId, getNetErrorObj());

                                }
                            }
                        });
                        return;
                    }

                    final BaseServerObj serverObj = GsonUtil.getObj(result);

                    if (serverObj == null) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (mINetWorkResult != null) {

                                    mINetWorkResult.error(msgId, getNetErrorObj());

                                }
                            }
                        });

                        return;
                    }

                    JsonParser parser = new JsonParser();

                    JsonElement el = parser.parse(serverObj.Data);

                    if (el.isJsonNull()) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (mINetWorkResult != null) {

                                    mINetWorkResult.error(msgId, serverObj);

                                }
                            }
                        });

                        return;
                    }

                    if (el.isJsonObject()) {

                        serverObj.contentObj = GsonUtil.GsonToBean(serverObj.Data, t);

                    } else if (el.isJsonArray()) {

                        serverObj.contentObj = GsonUtil.jsonToList(serverObj.Data, t);

                    } else if(el.isJsonPrimitive()){

                        if(t.getSimpleName().equals("Boolean")){

                            boolean info = Boolean.parseBoolean(serverObj.Data);

                            serverObj.contentObj = info;

                        } else if(t.getSimpleName().equals("Integer")){

                            int info = Integer.parseInt(serverObj.Data);

                            serverObj.contentObj = info;

                        } else if(t.getSimpleName().equals("String")){

                            serverObj.contentObj = serverObj.Data;

                        }

                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mINetWorkResult != null) {

                                mINetWorkResult.success(msgId, serverObj);

                            }
                        }
                    });


                } catch (Exception e) {
                    //解析出错

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mINetWorkResult != null) {

                                mINetWorkResult.success(msgId, getParseErrorObj());

                            }
                        }
                    });

                }

            }
        });
    }


    private static BaseServerObj getNoDataObj() {
        BaseServerObj serverObj = new BaseServerObj();
        serverObj.IsSuccess = false;
        serverObj.Message = "没有数据";
        return serverObj;
    }

    private static BaseServerObj getParseErrorObj() {
        BaseServerObj serverObj = new BaseServerObj();
        serverObj.IsSuccess = false;
        serverObj.Message = "解析出错";
        return serverObj;
    }

    private static BaseServerObj getNetErrorObj() {
        BaseServerObj serverObj = new BaseServerObj();
        serverObj.IsSuccess = false;
        serverObj.Message = "网络出错";
        return serverObj;
    }


    private static LogInterceptor mInterceptor;

    public static class LogInterceptor implements Interceptor {

        private final Context context;
        private long mLast;

        public LogInterceptor(Context context) {
            this.context = context;
        }


        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String method = request.method();

            Response response = null;
            MediaType mediaType = null;
            String content = null;
            LogUtils.e(request.url());
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                LogUtils.e("| RequestParams:{" + sb.toString() + "}");
            }
            Headers headers = request.headers();
            try {
                long t1 = System.nanoTime();
                response = chain.proceed(chain.request());
                Response copy = response.newBuilder().build();
                long t2 = System.nanoTime();
                LogUtils.e("responseCode = " + copy.code() + " " + String.format(Locale.getDefault(), "Received response for %s in %.1fms",
                        copy.request().url(), (t2 - t1) / 1e6d));
                Headers headers1 = copy.headers();
                mediaType = copy.body().contentType();
                content = copy.body().string();
                if (copy.code() != 404) {
                    BaseServerObj obj = null;
                    obj = GsonUtil.getObj(content);
                    if (obj != null) {

                    }
                }
                LogUtils.e("response body:" + content);
            } catch (IOException e) {

                LogUtils.e(e);
                e.printStackTrace();
                throw e;
            }
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }

    }

    public static LogInterceptor getLogInterceptor(Context context) {
        if (mInterceptor == null) {
            mInterceptor = new LogInterceptor(context);
        }
        return mInterceptor;
    }


    private static void overlockCard() {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };


        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }


}
