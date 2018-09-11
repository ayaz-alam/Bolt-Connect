package com.medeveloper.ayaz.boltconnect;

import android.content.Context;
import android.preference.PreferenceManager;

class SessionManager {
    private static final String BOLT_ID = "BOLT_ID";
    private static final String BOLT_API_KEY = "BOLT_API";
    private static final String DEVICE_CONFIGURED = "CONFIGURED";
    private Context context;
    public SessionManager(Context context) {
        this.context = context;
    }


    private void savePrefs(String Key,String Value)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Key,Value).apply();
    }
    private String getPrefs(String Key,String defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Key, defaultValue);
    }

    public void setBoltID(String ID)
    {
        savePrefs(BOLT_ID,ID);
    }
    public String getBoltID()
    {
        return getPrefs(BOLT_ID,null);
    }
    public void setBoltAPIKey(String APIKey)
    {
        savePrefs(BOLT_API_KEY,APIKey);
    }
    public String getBoltAPIKey()
    {
        return getPrefs(BOLT_API_KEY,null);
    }

    public void setDeviceSetupCompleted(boolean completed)
    {
        if(completed)
            savePrefs(DEVICE_CONFIGURED,"true");
        else savePrefs(DEVICE_CONFIGURED,"false");
    }
    public boolean isDeviceSetUpCompleted()
    {
        return getPrefs(DEVICE_CONFIGURED,"null").equals("true");
    }


}
