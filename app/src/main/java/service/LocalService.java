package service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.fcb.fogcomputingbox.Constants;
import com.fcb.fogcomputingbox.LogUtils;
import com.fcb.fogcomputingbox.MacUtils;
import com.hejunlin.tvsample.service.IMyAidlInterface;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocalService extends Service {

    private MyBinder binder;
    private MyServiceConnection conn;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return binder;
    }
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        if(binder ==null){
            binder = new MyBinder();
        }
        conn = new MyServiceConnection();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                LogUtils.e(11111);
//            }
//        },500);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    SystemClock.sleep(1000*60*60*10);
                        requestAward();
                }

            }
        }).start();
    }
    private void requestAward() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .build();

        FormBody.Builder builder = new FormBody.Builder();
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String deviceId = tm.getDeviceId();
//        LogUtils.e(deviceId);
        LogUtils.e(MacUtils.getAdresseMAC(this));
        builder.add("address", SPUtils.getInstance().getString("address"));
//        builder.add("deviceId",deviceId);
        builder.add("macAddress", MacUtils.getAdresseMAC(this));
//        builder.add("startDate",DateHelper.getYYMMdd(time));
//        builder.add("endDate",DateHelper.getYYMMdd(time1));

        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url("http://39.107.96.160:8080/api/saveMining")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        
        PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("XXX")
        .setContentIntent(contentIntent)
        .setContentTitle("我是XXX，我怕谁!")
        .setAutoCancel(true)
        .setContentText("哈哈")
        .setWhen( System.currentTimeMillis());
        
        //把service设置为前台运行，避免手机系统自动杀掉改服务。
        startForeground(startId, builder.build());
        return START_STICKY;
    }
    

    class MyBinder extends IMyAidlInterface.Stub{

        @Override
        public String getProcessName() throws RemoteException {
            // TODO Auto-generated method stub
            return "LocalService";
        }
        
    }
    
    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
         LogUtils.e( "建立连接成功！");
            
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
           LogUtils.e( "RemoteService服务被干掉了~~~~断开连接！");
            Toast.makeText(LocalService.this, "断开连接", Toast.LENGTH_SHORT).show();

            //启动被干掉的
            LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        }
        
    }
    

}