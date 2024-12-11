package com.gb.gbhelp;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class MediaManager extends Thread{
    private String fileName;
    Context context;
    private String mediaUrl;

    public MediaManager(Context context,String mediaUrl,String fileName){
        this.fileName = fileName;
        this.context = context;
        this.mediaUrl = mediaUrl;
    }
    @Override
    public void run() {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(mediaUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                GB.printLog("MediaManager/Server returned HTTP");
                return;
            }
            File file = getFile(context);
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
            GB.printLog("MediaManager/Exception/"+e.getMessage());
            return;
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
        GB.printLog("MediaManager/download media completed");
        onDownloadComplete();
        //    GB.Unzip2(context.getFilesDir() + "/stickers/"+"stickers.zip",context.getFilesDir().getAbsolutePath());

    }
    public File getFile(Context context) {
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
    public abstract void onDownloadComplete();
}