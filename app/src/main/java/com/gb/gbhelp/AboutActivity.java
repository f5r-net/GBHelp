package com.gb.gbhelp;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_app_bar);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.about_title));
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
           // actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(0x106000d, null)));
        }
      //  ((TextView)findViewById(R.id.tv_version_info2)).setText(GB.getNeedToUpdate(this) ? R.string.upgrade_found_title : R.string.settings_latest_version_installed);
        ((AppBarLayout) findViewById(R.id.app_bar)).setExpanded(false);
        initVerticalSpace();
        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(CheckUpdate.getUpdateLink(AboutActivity.this))));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void initVerticalSpace() {
        Space space = findViewById(R.id.space_top);
        Space space2 = findViewById(R.id.space_bottom);
        int isLandscapeMode;
        double d;
        double d2 = 0.07d;
        d = 0.05d;
        Resources resources = getResources();

        double d3 = resources.getDisplayMetrics().heightPixels;
        isLandscapeMode = (int) (d2 * d3);
        int i = (int) (d3 * d);

        space.getLayoutParams().height = isLandscapeMode;

        space2.getLayoutParams().height = i;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
