package com.gb.gbhelp.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class GBListPreference extends ListPreference {
    public GBListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GBListPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        GBPreference.setTitleColor(parent);
        return super.onCreateView(parent);
    }
}
