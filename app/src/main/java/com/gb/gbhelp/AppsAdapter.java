package com.gb.gbhelp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AppsAdapter extends ArrayAdapter {
    private final Activity activity;
    private final String[] appsList = {"com.gbwhatsapp",
            "com.whatsapp",
            "com.gbwhatsapp3",
            "com.yowhatsapp",
            "com.fmwhatsapp",
            "com.nowhatsapp",
            "com.nowhatsapp2",
            "com.nowhatsapp3"};

    public AppsAdapter(Activity activity) {
        super(activity, R.layout.apps_row);
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return appsList.length;
    }

    public View getView(int position, View view, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view1 = this.activity.getLayoutInflater().inflate(R.layout.apps_row, null);

        TextView app_name = view1.findViewById(R.id.app_name);
        TextView list_text_message = view1.findViewById(R.id.package_name);
        TextView list_frequency_type = view1.findViewById(R.id.list_frequency_type);
        TextView ver = view1.findViewById(R.id.version);
        ImageView wa_icon = view1.findViewById(R.id.app_icon);
        ver.setText(CheckUpdate.getWaver(activity));
        list_text_message.setText(appsList[position]);

        int waIcon;
        String title,downloadLink;
        switch (appsList[position]) {
            case "com.gbwhatsapp3":
                waIcon = R.drawable.gb_icon_13;
                title = "GBWhatsApp3";
                downloadLink = GB.getSharedString(activity,"json_gbwa3_check_key");
                break;
            case "com.fmwhatsapp":
                waIcon = R.drawable.gb_icon_12;
                title = "FMWhatsApp";
                downloadLink = GB.getSharedString(activity,"json_fmwa_check_key");
                break;
            case "com.whatsapp":
                waIcon = R.drawable.gb_icon_3;
                title = "WhatsApp";
                downloadLink = GB.getSharedString(activity,"json_wa_check_key");
                break;
            case "com.yowhatsapp":
                waIcon = R.drawable.gb_icon_7;
                title = "YOWhatsApp";
                downloadLink = GB.getSharedString(activity,"json_yowa_check_key");
                break;
            case "com.nowhatsapp":
                waIcon = R.drawable.gb_icon_1;
                title = "NOWhatsApp";
                downloadLink = GB.getSharedString(activity,"json_nowa_check_key");
                break;
            case "com.nowhatsapp2":
                waIcon = R.drawable.gb_icon_2;
                title = "NOWhatsApp2";
                downloadLink = GB.getSharedString(activity,"json_nowa2_check_key");
                break;
            case "com.nowhatsapp3":
                waIcon = R.drawable.gb_icon_5;
                title = "NOWhatsApp3";
                downloadLink = GB.getSharedString(activity,"json_nowa3_check_key");
                break;
            default:
                waIcon = R.drawable.icon;
                title = "GBWhatsApp";
                downloadLink = GB.getSharedString(activity,"json_gbwa_check_key");
        }
        view1.findViewById(R.id.appsView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(downloadLink)));
            }
        });
        wa_icon.setImageResource(waIcon);
        app_name.setText(title);
        return view1;
    }
}