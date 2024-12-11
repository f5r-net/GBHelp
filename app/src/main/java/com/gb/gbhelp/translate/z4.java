package com.gb.gbhelp.translate;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.gb.gbhelp.GB;

public class z4 {

    String yandex_lang;
    String finalText;
    Context context;
    public z4(String yandex_lang, Context context, TextView title, TextView summery) {
        this.yandex_lang = yandex_lang;
        this.context = context;
        TextView textView;
        for (int i = 0; i < 2; i++) {
            if (i == 0)
                textView = title;
            else
                textView = summery;
            try {
                TranslateAPI translateAPI = new TranslateAPI(
                        "auto",
                        yandex_lang,
                        textView.getText().toString());

                TextView finalTextView = textView;
                translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                    @Override
                    public void onSuccess(String translatedText) {
                        GB.printLog("z4/onSuccess: " + translatedText);
                        finalText = translatedText;
                        finalTextView.setText(finalText);
                    }

                    @Override
                    public void onFailure(String ErrorText) {
                        GB.printLog("z4/onFailure: " + ErrorText);
                        finalText = ErrorText;
                        Toast.makeText(context, "An error occurred!,please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}