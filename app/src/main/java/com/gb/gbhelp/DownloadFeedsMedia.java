/*package com.gb.gbhelp;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFeedsMedia extends AsyncTask<String ,Integer , String> {
    private final WeakReference<FeedsExpand> weakReference;
    private final String[] fileName;

    public DownloadFeedsMedia(FeedsExpand feedsExpand, String[] fileName) {
        this.weakReference = new WeakReference(feedsExpand);
        this.fileName = fileName;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (strings == null)
            return "null";
        for (int i = 0; i < strings.length; i++) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(strings[i]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    GB.printLog("MediaManager/Server returned HTTP");
                    return "MediaManager/Server returned HTTP";
                }
                File file = getFile(weakReference.get(),fileName[i]);
                GB.printLog("MediaManager/file path=" + file);
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {

                    total += count;

                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                GB.printLog("MediaManager/Exception/" + e.getMessage());
                return e.getMessage();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        weakReference.get().setAdapter();
    }

    public File getFile(Context context,String fileName) {
        File file;
        File dir = new File(context.getFilesDir() + "/Media/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        do {
            file = new File(dir, fileName);
        } while (file.exists());
        return file;
    }
}*/
