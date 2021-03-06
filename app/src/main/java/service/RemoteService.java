package service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

import com.fcb.fogcomputingbox.LogUtils;
import com.hejunlin.tvsample.service.IMyAidlInterface;

public class RemoteService extends Service {

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), conn, Context.BIND_IMPORTANT);
        
        PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder
        .setContentIntent(contentIntent)
        .setAutoCancel(true)
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
           LogUtils.e("建立连接成功！");
            
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
           LogUtils.e("LocalService服务被干掉了~~~~断开连接！");
            //启动被干掉的
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), conn, Context.BIND_IMPORTANT);
        }
        
    }
    
    
}
