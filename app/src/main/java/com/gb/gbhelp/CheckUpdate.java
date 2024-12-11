package com.gb.gbhelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

public class CheckUpdate extends AsyncTask<String, String, String> {
    WeakReference<Activity> activity;
    AlertDialog.Builder builder;
    private String msg = "";
    private final CustomTagHandler customTagHandler;

    public CheckUpdate(Activity activity) {
        GB.printLog("checkUpdate/doInBackground/cons");
        this.activity = new WeakReference<>(activity);
        this.customTagHandler = new CustomTagHandler();
    }

    @Override
    protected String doInBackground(String... paramVarArgs) {
        GB.printLog("checkUpdate/doInBackground/start");
        try {
            String readLine = GB.readToString("https://www.gbgroupapps.com/GB/GBHelpUpdate.txt");
            JSONObject localJSONObject = new JSONObject(readLine);
            JSONObject jsonObject =  localJSONObject.getJSONObject("GB");
            GB.printLog("checkUpdate/doInBackground/jsonObject" + jsonObject);

            String updateMsg = jsonObject.getString("update_message");
            String updateLink = jsonObject.getString("update_link");
            int updateCode = Integer.parseInt(jsonObject.getString("update_code"));
            String reminderTime = jsonObject.getString("reminder_time");
            String wa_version = jsonObject.getString("wa_version");

            String gbwa = jsonObject.getString("gbwa");
            String gbwa3 = jsonObject.getString("gbwa3");
            String wa = jsonObject.getString("wa");
            String fmwa = jsonObject.getString("fmwa");
            String yowa = jsonObject.getString("yowa");
            String nowa = jsonObject.getString("nowa");
            String nowa2 = jsonObject.getString("nowa2");
            String nowa3 = jsonObject.getString("nowa3");

            GB.printLog("checkUpdate/doInBackground/update_code=" + updateCode);
            if (updateCode > GB.update_code) {
                GB.printLog("checkUpdate/doInBackground/update found");
                saveReminderTime(activity.get(),reminderTime);
                saveUpdateLink(activity.get(),updateLink);
                saveWAver(activity.get(),wa_version);
                GB.saveSharedString(activity.get(), "json_gbwa_check_key", gbwa);
                GB.saveSharedString(activity.get(), "json_gbwa3_check_key", gbwa3);
                GB.saveSharedString(activity.get(), "json_wa_check_key", wa);
                GB.saveSharedString(activity.get(), "json_fmwa_check_key", fmwa);
                GB.saveSharedString(activity.get(), "json_yowa_check_key", yowa);
                GB.saveSharedString(activity.get(), "json_nowa_check_key", nowa);
                GB.saveSharedString(activity.get(), "json_nowa2_check_key", nowa2);
                GB.saveSharedString(activity.get(), "json_nowa3_check_key", nowa3);
                msg = updateMsg;
                return "update";
            }else if (updateCode == GB.update_code) {
                GB.printLog("checkUpdate/doInBackground/same version update time only");
                saveReminderTime(activity.get(),reminderTime);
                saveUpdateLink(activity.get(),updateLink);
                saveWAver(activity.get(),wa_version);
                GB.saveSharedString(activity.get(), "json_gbwa_check_key", gbwa);
                GB.saveSharedString(activity.get(), "json_gbwa3_check_key", gbwa3);
                GB.saveSharedString(activity.get(), "json_wa_check_key", wa);
                GB.saveSharedString(activity.get(), "json_fmwa_check_key", fmwa);
                GB.saveSharedString(activity.get(), "json_yowa_check_key", yowa);
                GB.saveSharedString(activity.get(), "json_nowa_check_key", nowa);
                GB.saveSharedString(activity.get(), "json_nowa2_check_key", nowa2);
                GB.saveSharedString(activity.get(), "json_nowa3_check_key", nowa3);
                msg = updateMsg;
                return "time";
            }
            GB.printLog("checkUpdate/doInBackground/update not found");
            return "null";
        } catch (JSONException | IOException exception) {
            exception.printStackTrace();
            GB.printLog("checkUpdate/doInBackground/exception/" + exception.getMessage());
        }
        GB.printLog("checkUpdate/doInBackground/update not found2");
        return "null";
    }
    @Override
    protected void onPostExecute(String result) {
        if (result.equals("update")) {
            GB.printLog("checkUpdate/onPostExecute/show update dialog");
            showDialog(msg);
            saveJsonTime(activity.get());
        } else if (result.equals("time")) {
            GB.printLog("checkUpdate/onPostExecute/checking time updated");
            saveJsonTime(activity.get());
        }

    }

    private void showDialog(String msg) {
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());
        builder.setTitle(GB.getString("upgrade_found_title",activity.get()));
        builder.setMessage(Html.fromHtml(msg, null, customTagHandler));
        builder.setCancelable(false).setPositiveButton((GB.getstr("upgrade")), (dialog, which) -> GB.ActionView(GB.getUpdateLink(activity.get()),activity.get()));
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {

        });
        AlertDialog dialog = builder.create();
        if (activity != null && !activity.get().isFinishing()) {
            dialog.show();
        }

         */
    }

    public static void saveJsonTime(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, getJsonReminderTime(context));
        long plusDate = calendar.getTime().getTime();
        GB.saveSharedLong(context, "json_time_check_key", plusDate);
    }

    public static long getJsonCheckTime(Context context) {
        return GB.getSharedLong(context, "json_time_check_key", 8);
    }

    public static void saveReminderTime(Context context, String reminder) {
        GB.saveSharedInt(context, "json_reminder_time_check_key", Integer.parseInt(reminder));
    }

    public static int getJsonReminderTime(Context context) {
        return GB.getSharedInt(context, "json_reminder_time_check_key", 0);
    }

    public static void saveUpdateLink(Context context, String reminder) {
        GB.saveSharedString(context, "json_update_link_check_key", reminder);
    }

    public static String getUpdateLink(Context context) {
        return GB.getSharedString(context, "json_update_link_check_key", "https://gbgroupapps.com/?p=508");
    }

    public static void saveWAver(Context context, String reminder) {
        GB.saveSharedString(context, "json_wa_version_key", reminder);
    }

    public static String getWaver(Context context) {
        return GB.getSharedString(context, "json_wa_version_key", "");
    }

    public static boolean checkJsonTime(Context context) {
        long prefJsonTime = getJsonCheckTime(context);
        if (prefJsonTime == 0) {
            GB.printLog("checkJsonTime/prefJsonTime/0");
            return true;
        }
        long plusDate = getJsonCheckTime(context);
        long nowDate = new Date().getTime();
        return plusDate >= nowDate;
    }

    public static void checkUpdate(final Activity activity) {

        if (!checkJsonTime(activity)) {
            GB.printLog("checkUpdate/check updated json");
            new CheckUpdate(activity).execute();
        } else {
            GB.printLog("checkUpdate/no Need to check update this time");
        }
    }

}

