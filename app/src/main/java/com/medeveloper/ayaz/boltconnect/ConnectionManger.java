package com.medeveloper.ayaz.boltconnect;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectionManger {


    private static final int ON = 1;
    private static final int OFF = 0;
    public static final int FAILED_TO_WRITE = -1;
    private Context context;
    private String ID,APIKey;
    public static final int ONLINE = 121;
    public static final int OFFLINE = 212;
    public static final int DEVICE_NOT_FOUND = 232;
    public static final int INVALID_API_KEY  =123;
    public static final int CONNECTION_TIMEOUT = 231;
    private RestartBolt boltRestart;

    public ConnectionManger(Context context,String BoltID,String APIKey) {
        this.context = context;
        this.ID = BoltID;
        this.APIKey = APIKey;
    }

    private String status="Unknown";
    public String isBoltOnline() {
        String url = "https://cloud.boltiot.com/remote/"+APIKey+"/isOnline?&deviceName="+ID;
        final RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response",response.toString());
                        try {
                            JSONObject jsonObject =new JSONObject(response);
                            if(jsonObject.get("success").equals(1)) {
                                status = jsonObject.get("value").toString();
                                if (status.equals("online"))
                                    boltStatus.getBoltStatus(ONLINE);
                                else if (status.equals("offline"))
                                    boltStatus.getBoltStatus(OFFLINE);
                            }
                            else
                            {
                                if(jsonObject.get("value").equals("Invalid API key"))
                                    boltStatus.getBoltStatus(INVALID_API_KEY);
                                if ((jsonObject.get("value").equals("Device does not exist")))
                                    boltStatus.getBoltStatus(DEVICE_NOT_FOUND);
                            }
                                queue.stop();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyResponse","That didn't work!");
                boltStatus.getBoltStatus(CONNECTION_TIMEOUT);
                queue.stop();
              //  boltStatus.getBoltStatus(CONNECTION_TIMEOUT);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return status;
    }

    public BoltStatus boltStatus;

    public void restart() {
        String Url  = "https://cloud.boltiot.com/remote/"+APIKey+"/restart?&deviceName="+ID;
        Log.d("My URL",Url);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VolleyResponse","Response is: "+ response);
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            if(responseJson.get("success").equals("1")&&responseJson.get("value").equals("Restarted")) {
                                if (boltRestart != null)
                                    boltRestart.restarted();
                            }
                            else
                            {
                                if (boltRestart != null)
                                    boltRestart.cancelled();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            //Parsing Error
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Error: "+error,Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void setRestartListener(RestartBolt boltRestart)
    {
        this.boltRestart = boltRestart;

    }

    public interface RestartBolt{
        void restarted();
        void cancelled();
    }

    public interface BoltStatus {
        void getBoltStatus(int status);
    }

    public ReadDigitalPin digitalPin;

    public interface ReadDigitalPin
    {
        void getPinStatus(int status,int pin);
    }

    public void readDigitalPin(final int pin)
    {
        String Url = "https://cloud.boltiot.com/remote/"+APIKey+"/digitalRead?pin="+pin+"&deviceName="+ID;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(digitalPin!=null) {
                            Log.d("VolleyResponse", "Response is: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("VolleyJson", "" + jsonObject.get("value"));
                                status = jsonObject.get("value").toString();
                                if (status.equals("1"))
                                    digitalPin.getPinStatus(ON,pin);
                                else if (status.equals("0"))
                                    digitalPin.getPinStatus(OFF,pin);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyResponse","That didn't work!");
                //  boltStatus.getBoltStatus(CONNECTION_TIMEOUT);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void setPinStatusListener(ReadDigitalPin pinStatus)
    {
        this.digitalPin = pinStatus;
    }

    public void setBoltStatusListener(BoltStatus boltStatus)
    {
        this.boltStatus = boltStatus;
    }

    public WriteDigitalPin digitalWrite;

    public interface WriteDigitalPin
    {
        void getPinStatus(int status,int pin,boolean isPWM);
    }

    public void writeDigitalPin(final int pin, int state, final boolean isPWM)
    {
        String Url =null;
        if(!isPWM) {
            String Value = "LOW";
            if (state == 1)
                Value = "HIGH";
            Url = "https://cloud.boltiot.com/remote/"+APIKey+"/digitalWrite?pin="+pin+"&state="+Value+"&deviceName="+ID;
        }
        else
        {

            Url = "https://cloud.boltiot.com/remote/"+APIKey+"/analogWrite?pin="+pin+"&value="+state+"&deviceName="+ID;
        }
        Log.d("WriteCalled",""+Url);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(digitalPin!=null) {
                            Log.d("VolleyResponse", "Response is: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("VolleyJson", "" + jsonObject.get("value"));
                                status = jsonObject.get("value").toString();
                                if(jsonObject.get("success").equals("1")) {
                                    if (status.equals("1")) {
                                        if (digitalWrite != null)
                                            digitalWrite.getPinStatus(ON, pin,isPWM);
                                    } else if (status.equals("0"))
                                        if (digitalWrite != null)
                                            digitalWrite.getPinStatus(OFF, pin,isPWM);
                                }
                                else digitalWrite.getPinStatus(FAILED_TO_WRITE,pin,isPWM);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyResponse","That didn't work!");
                if(digitalWrite!=null)
                digitalWrite.getPinStatus(FAILED_TO_WRITE,pin,isPWM);
                //  boltStatus.getBoltStatus(CONNECTION_TIMEOUT);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void setWriteToPinListener(WriteDigitalPin pinStatus)
    {
        this.digitalWrite = pinStatus;
    }

    public void readAnalogPin()

    {  String url = "https://cloud.boltiot.com/remote/"+APIKey+"/analogRead?pin=A0&deviceName="+ID;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VolleyResponse","Response is: "+ response);
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            if(responseJson.get("success").equals("1")&&analogReadListener!=null)
                                analogReadListener.analogPinValue(Integer.parseInt(responseJson.get("value").toString()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            //Parsing Error
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Error: "+error,Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private  AnalogRead analogReadListener;
    public interface AnalogRead
    {
        void analogPinValue(int value);
    }

    public void setAnalogValueReadListener(AnalogRead analogReadListener)
    {
        this.analogReadListener = analogReadListener;
    }
}
