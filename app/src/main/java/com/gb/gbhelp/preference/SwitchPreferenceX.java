package com.gb.gbhelp.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

public class SwitchPreferenceX extends SwitchPreference {
    public SwitchPreferenceX(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchPreferenceX(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        GBPreference.setTitleColor(holder);
        super.onBindViewHolder(holder);
    }
}
