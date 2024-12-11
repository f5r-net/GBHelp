package com.gb.gbhelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gb.gbhelp.translate.TranslateItems;
import com.gb.gbhelp.translate.z4;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GB {
    protected final static int update_code = 4;

    public static final int FEED_LANG = 0;
    public static final int FEED_LANG_AR = 1;

    public static final int SELECT_FEED_MEDIA = 100;
    public static final int SELECT_FEED_MEDIA_AR = 101;
    final static String SHOULD_RECREATE = "should_recreate";
    public final static String STATUS_WAITING = "waiting";
    public final static String STATUS_RECEIVED = "received";
    public final static String STATUS_FIXED = "fixed";
    public final static String DATABASE_COMMUNITY = "community";
    public final static String DATABASE_APPS = "apps";
    public final static String DATABASE_FEEDS = "feeds";
    public final static String NICKNAME = "nickname";
    public static final String ADMIN_ID = "sxXwxKeElhR5Gz9La5EcvlVm0O63";
    public static final int MEDIA_UPLOADED = 7;
    public static final String FEEDS_TAG_IMPORTANT = "important";
    public static final String FEEDS_SUPPORT_LANG = "en";
    public static final String FEEDS_SUPPORT_LANG_AR = "ar";
    public static final String CHAT_TAG_SUGGESTION = "suggestion";
    public static final String CHAT_TAG_HELP = "help";
    public static final String IS_LOGIN = "is_login";
    public static final String USER_ID = "user_id";

    public static void startFeedsActivity(Activity activity,String id){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
    public static void startChatActivity(Context activity,String id,String nickname,String tag){
        Intent intent = new Intent(activity,ChatActivity.class);
        if (id != null)
        intent.putExtra("userId",id);
        if (nickname != null)
        intent.putExtra("nickname",nickname);
        if (tag != null)
        intent.putExtra("tag",tag);
        activity.startActivity(intent);
    }

    public static String getSharedString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static String getSharedString(Context context, String key,String def) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, def);
    }
    public static void saveSharedString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }
    public static boolean getSharedBool(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public static void saveSharedBool(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static void printLog(String msg){
        Log.e("GBHelp",msg);
    }
    public static void printLog(Activity activity,String msg){
        Log.e("GBHelp",activity.getLocalClassName()+"/"+msg);
    }
    public static boolean isAdmin(){
        return FirebaseAuth.getInstance().getUid().equals(ADMIN_ID);
    }

    public static void translate(Context context, TextView title,TextView summery, String lang) {
        new z4(lang, context, title,summery);
    }
    private static void TranslateToMenuEntry(FeedsExpand feedsExpand, TextView title, TextView summery) {
        final String[] items = {
                feedsExpand.getResources().getString( R.string.translate_to_ar),
                feedsExpand.getResources().getString( R.string. translate_to_en),
                feedsExpand.getResources().getString( R.string.translate_to_es),
                feedsExpand.getResources().getString( R.string. translate_to_pt_rbr),
                feedsExpand.getResources().getString( R.string. translate_to_ge),
                feedsExpand.getResources().getString( R.string. translate_to_fr),
                feedsExpand.getResources().getString( R.string. translate_to_in),
                feedsExpand.getResources().getString( R.string. translate_to_it),
                feedsExpand.getResources().getString( R.string. translate_to_tu),
                feedsExpand.getResources().getString( R.string. translate_to_fa)
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(feedsExpand);
        builder.setItems(items, new TranslateItems(feedsExpand, title,summery));
        builder.create();
        builder.show();
    }

    public static void startTranslation(FeedsExpand feedsExpand,TextView title,TextView summery) {
                if (title == null || title.getText().toString().isEmpty())
                    return;
                    GB.printLog("startTranslation/TranslateToMenuEntry");
                    TranslateToMenuEntry(feedsExpand,title,summery);
        /*
        feedsExpand.GBTranslate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String title = feedsExpand.A37.getText().toString();
                if (title == null || title.isEmpty())
                    return false;
                transText2 = title;
                TranslateToMenuEntry(feedsExpand.A37);
                return false;
            }
        });*/
    }

    public static void setLanguage(Context context) {
        Locale locale = null;
        int i = Integer.parseInt(getSharedString(context, "language_key", "0"));
        if (i == 0) {
            locale = Locale.getDefault();
        } else if (i == 1) {
            locale = new Locale("ar");
        } else if (i == 2) {
            locale = new Locale("en");
        }/* else if (i == 3) {
            locale = new Locale("es");
        } else if (i == 4) {
            locale = new Locale("pt", "BR");
        } else if (i == 5) {
            locale = new Locale("it");
        } else if (i == 6) {
            locale = new Locale("hi");
        }*/
        if (locale != null) {
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    public static boolean getLanguage(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString("language_key","0");
        if (value.equals("0"))
        return Locale.getDefault().getLanguage().contains("ar");
        else if (value.equals("1"))
            return true;
        else
            return false;
    }

    public static String getUploadFileName() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        return formatter.format(now);
    }

    public static String getText(Context context,FeedsModel feedsModel,boolean isTitle){
        if (isTitle)
        return GB.getLanguage(context) ? feedsModel.getTitleAR() : feedsModel.getTitle();
        else
        return GB.getLanguage(context) ? feedsModel.getSummeryAR() : feedsModel.getSummery();
    }

    public static String readToString(String targetURL) throws IOException {
        java.net.URL url = new URL(targetURL);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuilder stringBuilder = new StringBuilder();

        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(inputLine);
            stringBuilder.append(System.lineSeparator());
        }

        bufferedReader.close();
        return stringBuilder.toString().trim();
    }

    public static void saveSharedLong(Context context, String key, long l) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, l).apply();
    }

    public static long getSharedLong(Context context, String key, int i) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, i);
    }

    public static void saveSharedInt(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    public static int getSharedInt(Context context, String key, int i) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, i);
    }
    public static void copyMessage(Context context,String string) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("textlabel", string);
        clipboard.setPrimaryClip(clip);
    }
    public static void passwordAlignment(Context context,EditText editText, Editable editable){
        if (editable != null && !editable.toString().isEmpty()){
            editText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }else {
            if (GB.getLanguage(context))
                editText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }
    }
}
