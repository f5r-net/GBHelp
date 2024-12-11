package com.gb.gbhelp.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;


public class GBPreferenceX extends Preference {


    public GBPreferenceX(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GBPreferenceX(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {

        GBPreference.setTitleColor(holder);
        super.onBindViewHolder(holder);
    }
}
