package com.medeveloper.ayaz.boltconnect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.medeveloper.ayaz.boltconnect.ConnectionManger.DEVICE_NOT_FOUND;
import static com.medeveloper.ayaz.boltconnect.ConnectionManger.INVALID_API_KEY;
import static com.medeveloper.ayaz.boltconnect.ConnectionManger.OFFLINE;
import static com.medeveloper.ayaz.boltconnect.ConnectionManger.ONLINE;

public class CheckInternetService extends Service {
    public CheckInternetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BOLTServiceStarted","TRUE");
        new checkForStatus(this,intent.getStringExtra("API"),intent.getStringExtra("ID")).execute();
        return START_STICKY;
    }
}

class checkForStatus extends AsyncTask
{
    private String APIKey,ID;
    private Context context;
    public checkForStatus(Context context,String APIKey,String ID) {
        this.APIKey = APIKey;
        this.ID = ID;
        this.context =context;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    int serviceCalls = 0;
    private RequestQueue queue;
    private StringRequest stringRequest;
    @Override
    protected Object doInBackground(Object[] objects) {

        Timer timer  = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


        String url = "https://cloud.boltiot.com/remote/"+APIKey+"/isOnline?&deviceName="+ID;
        queue = Volley.newRequestQueue(context);
        stringRequest  = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("BOLTServiceStarted","RESPONSE : "+serviceCalls+++" "+response);
                        try {
                            JSONObject jsonObject =new JSONObject(response);
                            if(jsonObject.get("success").equals(1)) {
                                String status = jsonObject.get("value").toString();
                                if (status.equals("online"))
                                Log.d("BOLTService","Device Status Online" )  ;// queue.add(stringRequest);//Recreate the process if online
                                else if (status.equals("offline")) {
                                    Log.d("BOLTService", "Device Status Online");
                                }
                            }
                            queue.stop();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                queue.stop();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
            }
        },1,3000);
        return null;
    }
}

