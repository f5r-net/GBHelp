package com.gb.gbhelp.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.PreferenceViewHolder;

import com.gb.gbhelp.GB;
import com.gb.gbhelp.R;

public class GBPreference extends Preference {


    public GBPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GBPreference(Context context) {
        super(context);
    }

    static void setTitleColor(View view) {
        TextView title = view.findViewById(android.R.id.title);
        TextView summary = view.findViewById(android.R.id.summary);
        if (title != null)
            title.setTextColor(view.getContext().getResources().getColorStateList(R.color.title_primary_color_selector));


        if (summary != null)
            summary.setTextColor(summary.getResources().getColor(R.color.summary));
    }

    static void setTitleColor2(View view) {
        TextView title = view.findViewById(android.R.id.title);
        TextView summary = view.findViewById(android.R.id.summary);
        if (title != null)
            title.setTextColor(view.getContext().getResources().getColorStateList(R.color.title_primary_color_selector));


        if (summary != null) {
            GB.printLog("test/");
            summary.setTextColor(summary.getResources().getColor(R.color.summary));
        }
    }

    static void setTitleColor(PreferenceViewHolder view) {
        TextView title = (TextView) view.findViewById(android.R.id.title);
        TextView summary = (TextView) view.findViewById(android.R.id.summary);
        if (title != null)
            title.setTextColor(title.getContext().getResources().getColorStateList(R.color.title_primary_color_selector));

        if (summary != null)
            summary.setTextColor(summary.getResources().getColor(R.color.summary));
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        setTitleColor(view);
        return view;
    }
}
