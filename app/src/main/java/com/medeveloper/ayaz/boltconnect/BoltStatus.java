package com.medeveloper.ayaz.boltconnect;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Timer;
import java.util.TimerTask;

import static com.medeveloper.ayaz.boltconnect.ConnectionManger.CONNECTION_TIMEOUT;

public class BoltStatus extends AsyncTask {
    Context mContext;
    String ID,APIKey;
    private ConnectionManger connectionManger;
    private int TimeOut = 10;
    private int timeCounter = 0;

    public BoltStatus(Context mContext, String ID, String APIKey, ConnectionManger connectionManger) {
        this.mContext = mContext;
        this.ID = ID;
        this.APIKey = APIKey;
        this.connectionManger = connectionManger;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        final Timer timer =new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeCounter++;
                String status = connectionManger.isBoltOnline();
                if(status.equals("online"))
                    timer.cancel();
                else if(status.equals("offline"))
                    timer.cancel();
                else if(timeCounter==TimeOut) {
                    timer.cancel();

                }
            }
        },1000,10);

        return null;
    }
}