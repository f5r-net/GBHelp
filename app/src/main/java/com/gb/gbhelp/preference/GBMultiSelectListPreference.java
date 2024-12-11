package com.gb.gbhelp.preference;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.gb.gbhelp.GB;


public class GBMultiSelectListPreference extends MultiSelectListPreference {

    public GBMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GBMultiSelectListPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        GB.printLog("sss/" + getKey());
        GBPreference.setTitleColor2(parent);
        return super.onCreateView(parent);
    }
}
