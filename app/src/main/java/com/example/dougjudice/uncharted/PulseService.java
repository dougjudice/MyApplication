package com.example.dougjudice.uncharted;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dougjudice on 4/9/17.
 */

public class PulseService extends Service {

    // Pulse time
    public static final long NOTIFY_INTERVAL = 5 * 1000; // 5 seconds, increase later TODO
    public static final String BROADCAST_ACTION = "com.example.dougjudice.uncharted.PulseService";
    Intent broadcastIntent;

    public IBinder mBinder = new LocalBinder();
    private ServiceCallback serviceCallbacks;
    private Handler mHandler = new Handler();
    private static Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent){
        System.out.println("Restarting Timer...");

        if(mTimer != null){
            mTimer.cancel();
        } else {
            System.out.println("Making new timer");
            mTimer = new Timer();
        }
        //broadcastIntent = new Intent(BROADCAST_ACTION);
        mTimer.scheduleAtFixedRate(new timedTask(), 0, NOTIFY_INTERVAL);

        return mBinder;
    }

    public void onRebind(Intent intent){
        System.out.println("[ REBIND ]Restarting Timer...");

        if(mTimer != null){
            mTimer.cancel();
        } else {
            System.out.println("Making new timer");
            mTimer = new Timer();
        }
        //broadcastIntent = new Intent(BROADCAST_ACTION);
        mTimer.scheduleAtFixedRate(new timedTask(), 0, NOTIFY_INTERVAL);
    }

    @Override
    public boolean onUnbind(Intent intent){
        return true;
    }

    public class LocalBinder extends Binder {
        PulseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PulseService.this;
        }
    }

    @Override
    public void onCreate(){

    }

    class timedTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post((new Runnable() {

                @Override
                public void run() {
                    // display toast
                    //Toast.makeText(getApplicationContext(), getDateTime(),
                     //       Toast.LENGTH_SHORT).show();

                    // This is what calls forceLocationUpdates on a timer
                    if(serviceCallbacks != null){
                        serviceCallbacks.forceLocationUpdate();
                    }
                    //sendBroadcast(broadcastIntent);
                }
            }));
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

    }
    public static void cancelTimer(){
        //mTimer.purge();
        mTimer.cancel();
        mTimer = null;
    }

    // When a user logs out, for some reason, this guy has to be called in onServiceConnected within MapsActivity to make sure that the timer starts again properly
    public void forceStartTimer(){
        if(mTimer != null){
            return; // Timer may have already been started, would cause exception if you didn't return
        } else {
            System.out.println("Making new timer");
            mTimer = new Timer();
        }
        //broadcastIntent = new Intent(BROADCAST_ACTION);
        mTimer.scheduleAtFixedRate(new timedTask(), 0, NOTIFY_INTERVAL);
    }

    public void setCallback(ServiceCallback callback){
        serviceCallbacks = callback;
    }
}
