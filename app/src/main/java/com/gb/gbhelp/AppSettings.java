package com.gb.gbhelp;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


public class AppSettings extends BaseSettings implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = AppSettings.class.getName();

    ListPreference language, appTheme;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.app_settings_layout);
        addPreferencesFromResource(R.xml.app_settings);
        sharedPreferences = getPreferenceManager().getSharedPreferences();
        language = (ListPreference) findPreference("language_key");
        language.setOnPreferenceChangeListener(this);
        findPreference("help_key").setOnPreferenceClickListener(this);
        findPreference("logout_key").setOnPreferenceClickListener(this);
        /*
        if (appTheme != null) {
            appTheme = (ListPreference) findPreference("app_theme_key");
            appTheme.setOnPreferenceChangeListener(this);
            language.setOnPreferenceChangeListener(this);
        }*/
        updateSummary();
    }

    private void updateSummary() {
        String summary;
        switch (language.getValue()) {
            case "0": {
                summary = getResources().getString(R.string.default_language_summary);
                break;
            }
            case "1": {
                summary = getResources().getString(R.string.arabic);
                break;
            }
            case "2": {
                summary = getResources().getString(R.string.english);
                break;
            }
            default:
                summary = getResources().getString(R.string.default_language_summary);
        }
        language.setSummary(summary);
        /*
        if (appTheme != null) {
            switch (sharedPreferences.getString("app_theme_key", "0")) {
                case "0": {
                    summary = getResources().getString(R.string.light);
                    break;
                }
                case "1": {
                    summary = getResources().getString(R.string.dark);
                    break;
                }
                default:
                    summary = getResources().getString(R.string.light);
            }
            appTheme.setSummary(summary);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (key != null) {
            switch (preference.getKey()) {
                case "language_key": {
                    GB.saveSharedBool(this, GB.SHOULD_RECREATE, true);
                    recreate();
                    return true;
                }
                case "app_theme_key": {
                    if (newValue.toString().equals("0"))
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    GB.saveSharedBool(this, GB.SHOULD_RECREATE, true);
                    recreate();
                    return true;
                }
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("help_key")) {
            startActivity(new Intent(this,AboutActivity.class));
            return true;
        }
        if (preference.getKey().equals("logout_key")) {
            FirebaseAuth.getInstance().signOut();
            GB.saveSharedBool(this,GB.IS_LOGIN,false);
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finishAffinity();
            return true;
        }

        return false;
    }

}
