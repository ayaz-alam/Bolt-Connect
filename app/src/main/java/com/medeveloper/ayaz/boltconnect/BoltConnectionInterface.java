package com.medeveloper.ayaz.boltconnect;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.IDN;
import java.util.ArrayList;

import static com.medeveloper.ayaz.boltconnect.ConnectionManger.CONNECTION_TIMEOUT;
import static com.medeveloper.ayaz.boltconnect.ConnectionManger.DEVICE_NOT_FOUND;
import static com.medeveloper.ayaz.boltconnect.ConnectionManger.FAILED_TO_WRITE;
import static com.medeveloper.ayaz.boltconnect.ConnectionManger.INVALID_API_KEY;
import static com.medeveloper.ayaz.boltconnect.ConnectionManger.OFFLINE;
import static com.medeveloper.ayaz.boltconnect.ConnectionManger.ONLINE;

public class BoltConnectionInterface extends AppCompatActivity implements View.OnClickListener {

    private static final int TIMEOUT = 15;//Wait for 30s

    private String APIKey;
    private String BoltID;
    private SessionManager sessionManager;
    private ConnectionManger connectionManger;
    private ArrayList<View> pinList;
    LinearLayout PinLayoutContainer;
    View analogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_interface);
        sessionManager = new SessionManager(this);
        APIKey = sessionManager.getBoltAPIKey();
        BoltID = sessionManager.getBoltID();
        connectionManger =new ConnectionManger(this,BoltID,APIKey);
        initViews();

        if(isOnline())
        {
            isBoltOnline();
            showNoConnectionView(false, "null");
            showProgressBar(true);
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        else
        {
            showProgressBar(false);
            showNoConnectionView(true,"No internet connection\n Please check your internet connection");
        }
        return false;
    }

    private void showProgressBar(boolean show) {
        int state;
        if(show)
            state = View.VISIBLE;
        else state = View.GONE;
        ((findViewById(R.id.progress_bar))).setVisibility(state);
        ((ProgressBar)(findViewById(R.id.progress_bar))).getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void initViews() {
        pinList = new ArrayList<>();
        PinLayoutContainer = findViewById(R.id.pin_layout_container);
        ((findViewById(R.id.retry_button))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline())isBoltOnline();
            }
        });
    }

    int view_populated =-1;
    private void isBoltOnline()
    {
        showNoConnectionView(false,"");
        showProgressBar(true);
        connectionManger.setBoltStatusListener(new ConnectionManger.BoltStatus() {
            @Override
            public void getBoltStatus(int status) {
                if(status==ONLINE&&view_populated==-1)
                {
                    proceed();
                    view_populated=0;
                    showProgressBar(false);
                }
                else if(status==OFFLINE)
                {
                    dontProceed();
                    showNoConnectionView(true,"Bolt is offline");
                    showProgressBar(false);
                }
                else if(status==CONNECTION_TIMEOUT) {
                    connectionTimeOut();
                    showProgressBar(false);
                    showNoConnectionView(true,"Network error");
                }
                else if(status==INVALID_API_KEY) {
                    connectionTimeOut();
                    showProgressBar(false);
                    showNoConnectionView(true,"Invalid API Key");
                }
                else if(status==DEVICE_NOT_FOUND) {
                    connectionTimeOut();
                    showProgressBar(false);
                    showNoConnectionView(true,"Device does not exists");
                }
            }
        });
        connectionManger.isBoltOnline();
    }

    private void connectionTimeOut() {
        Log.d("BoltStatus","TimeOut");
        Toast.makeText(this,"Timeout",Toast.LENGTH_SHORT).show();
        PinLayoutContainer.setVisibility(View.GONE);
    }

    private void dontProceed() {
        Log.d("BoltStatus","Offline");
        Toast.makeText(this,"Offline",Toast.LENGTH_SHORT).show();
        PinLayoutContainer.setVisibility(View.GONE);
    }

    private void proceed() {
        showNoConnectionView(false, "");
        Toast.makeText(this,"Online :)",Toast.LENGTH_SHORT).show();
        PinLayoutContainer.setVisibility(View.VISIBLE);
        for(int i= 0;i<5;i++)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.analog_pin_list,null);
            pinList.add(view);
            PinLayoutContainer.addView(view);
            ((TextView)view.findViewById(R.id.pin_0)).setText("GPIO Pin "+i);

        }
        preparePinListeners();
        prepareAnalogPin();
    }

    private void preparePinListeners() {
        for(int i=0;i<pinList.size();i++)
        {
            final int pos = i;
            (pinList.get(i).findViewById(R.id.read_pin)).setOnClickListener(this);
            (pinList.get(i).findViewById(R.id.toggle_button)).setOnClickListener(this);
            (pinList.get(i).findViewById(R.id.write)).setOnClickListener(this);
            ((SeekBar)pinList.get(i).findViewById(R.id.analog_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    ((TextView)pinList.get(pos).findViewById(R.id.current_value)).setText(progress+"");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }


    }

    @Override
    public void onClick(final View v) {
        int readPos=-1;
        int writePos= -1;
        int writePwm=-1;
        for(int i=0;i<pinList.size();i++)
        {
            if(pinList.get(i).findViewById(R.id.read_pin)==v)
                readPos = i;
            else if(pinList.get(i).findViewById(R.id.toggle_button)==v)
                writePos = i;
            else if(pinList.get(i).findViewById(R.id.write)==v)
                writePwm = i;


        }
        if(readPos!=-1) {
            connectionManger.setPinStatusListener(new ConnectionManger.ReadDigitalPin() {
                @Override
                public void getPinStatus(int status,int pin) {

                        Toast.makeText(BoltConnectionInterface.this,"Successfully written",Toast.LENGTH_SHORT).show();
                        String Status = "Low";
                        if (status == 1)
                            Status = "High";
                        ((TextView) pinList.get(pin).findViewById(R.id.status)).setText("Status:[ " + Status + " ]");
                        ((ToggleButton) pinList.get(pin).findViewById(R.id.toggle_button)).setChecked(!Status.equals("High"));
                        ((ToggleButton) pinList.get(pin).findViewById(R.id.toggle_button)).setVisibility(View.VISIBLE);
                        (pinList.get(pin).findViewById(R.id.seek_bar_layout)).setVisibility(View.VISIBLE);
                }
            });
            connectionManger.readDigitalPin(readPos);
        }
        if(writePos!=-1)
        {
            connectionManger.setWriteToPinListener(new ConnectionManger.WriteDigitalPin() {
                @Override
                public void getPinStatus(int status, int pin,boolean isPWM) {
                    if(status!=FAILED_TO_WRITE) {
                        Toast.makeText(BoltConnectionInterface.this,"Successfully written",Toast.LENGTH_SHORT).show();
                        if(!isPWM) {
                            String Status = "High";
                            if (((ToggleButton) v).isChecked())
                                Status = "Low";
                            ((TextView) pinList.get(pin).findViewById(R.id.status)).setText("Status:[ " + Status + " ]");
                        }
                    }
                    else Toast.makeText(BoltConnectionInterface.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            });

            if(((ToggleButton)v).isChecked())
                connectionManger.writeDigitalPin(writePos,0,false);
            else connectionManger.writeDigitalPin(writePos,1,false);
        }

        if(writePwm!=-1) {
            int value = ((SeekBar)pinList.get(writePwm).findViewById(R.id.analog_seekbar)).getProgress();
            connectionManger.setWriteToPinListener(new ConnectionManger.WriteDigitalPin() {
                @Override
                public void getPinStatus(int status, int pin,boolean isPWM) {
                    if(status!=FAILED_TO_WRITE) {
                        Toast.makeText(BoltConnectionInterface.this,"Successfully written",Toast.LENGTH_SHORT).show();
                        if(!isPWM) {
                            String Status = "High";
                            if (((ToggleButton) v).isChecked())
                                Status = "Low";
                            ((TextView) pinList.get(pin).findViewById(R.id.status)).setText("Status:[ " + Status + " ]");
                        }
                    }
                    else Toast.makeText(BoltConnectionInterface.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            });

            connectionManger.writeDigitalPin(writePwm,value,true);
        }

    }

    private void showNoConnectionView(boolean show, String msg) {
        int state;
        if(show)
            state = View.VISIBLE;
        else state = View.GONE;
        ((findViewById(R.id.not_con))).setVisibility(state);
        ((findViewById(R.id.retry_button))).setVisibility(state);
        ((findViewById(R.id.error_text))).setVisibility(state);
        if (show)
            ((TextView)(findViewById(R.id.error_text))).setText(msg);

    }
        void prepareAnalogPin()
    {

        analogView= LayoutInflater.from(this).inflate(R.layout.pin_list_item,null);
        PinLayoutContainer.addView(analogView);
        (analogView.findViewById(R.id.read_pin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionManger.readAnalogPin();

            }
        });
        connectionManger.setAnalogValueReadListener(new ConnectionManger.AnalogRead() {
            @Override
            public void analogPinValue(int value) {
                ((TextView)analogView.findViewById(R.id.status)).setText("Value:["+value+"]");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout: logout();break;
            case R.id.restart: restart();break;
            case R.id.about: Toast.makeText(this,"Not implemented yet",Toast.LENGTH_SHORT).show();break;
        }
        return true;
    }

    private void restart() {
        connectionManger.setRestartListener(new ConnectionManger.RestartBolt() {
            @Override
            public void restarted() {
                Toast.makeText(BoltConnectionInterface.this,"Restarted",Toast.LENGTH_SHORT).show();
                connectionManger.setRestartListener(null);
            }

            @Override
            public void cancelled() {
                Toast.makeText(BoltConnectionInterface.this,"Can't Restart, some error occured",Toast.LENGTH_SHORT).show();
                connectionManger.setRestartListener(null);
            }
        });
        if(isOnline())
            connectionManger.restart();

    }

    private void logout() {
        sessionManager.setDeviceSetupCompleted(false);
        sessionManager.setBoltID(null);
        sessionManager.setBoltAPIKey(null);
        startActivity(new Intent(this,BoltRegistration.class));
        finish();
    }


}
