package com.medeveloper.ayaz.boltconnect;

import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLogTags;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BoltRegistration extends AppCompatActivity {

    private EditText boltIdEditText;
    private EditText boltAPIEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolt_registration);
        initView();
    }

    private void initView() {
        boltIdEditText = findViewById(R.id.bolt_id_edit_text);
        boltAPIEditText = findViewById(R.id.bolt_api_edit_text);
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields())
                    startBoltInterface();
            }
        });
    }

    private void startBoltInterface() {
        if(isOnline()) {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.setBoltAPIKey(boltAPIEditText.getText().toString());
            sessionManager.setBoltID(boltIdEditText.getText().toString());
            sessionManager.setDeviceSetupCompleted(true);
            startActivity(new Intent(this, BoltConnectionInterface.class));
            finish();
        }
    }

    private boolean checkFields() {
        ((TextInputLayout) findViewById(R.id.id_layout)).setError(null);
        ((TextInputLayout) findViewById(R.id.api_layout)).setError(null);
        if(boltIdEditText.getText().toString().equals(""))
        {
            ((TextInputLayout) findViewById(R.id.id_layout)).setError("Bolt ID Required");
            return false;
        }
        else if(boltAPIEditText.getText().toString().equals(""))
        {
            ((TextInputLayout) findViewById(R.id.api_layout)).setError("Bolt API Required");
            return false;
        }

        return true;
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
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(
                            getResources().getString(
                                    R.string.internet_error))
                    .setPositiveButton("OK", null).show();
        }
        return false;
    }
}
