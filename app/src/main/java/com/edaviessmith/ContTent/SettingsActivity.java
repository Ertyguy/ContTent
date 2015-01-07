package com.edaviessmith.contTent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.edaviessmith.contTent.util.Var;


public class SettingsActivity extends ActionBarActivity implements View.OnClickListener {
    String TAG = "NotificationsActivity";

    Toolbar toolbar;


    View hiresWifi_v, hiresMobile_v, version_v, contact_v, rateApp_v, licenses_v;
    SwitchCompat hiresWifi_sw, hiresMobile_sw;
    TextView version_tv;
    boolean isHiresWifi, isHiresMobile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        isHiresWifi = Var.getBoolPreference(this, Var.PREF_HIRES_WIFI);
        isHiresMobile = Var.getBoolPreference(this, Var.PREF_HIRES_MOBILE);

        hiresWifi_v = findViewById(R.id.hires_wifi_v);
        hiresWifi_v.setOnClickListener(this);
        hiresWifi_sw = (SwitchCompat) findViewById(R.id.hires_wifi_sw);
        hiresWifi_sw.setChecked(isHiresWifi);

        hiresMobile_v = findViewById(R.id.hires_mobile_v);
        hiresMobile_v.setOnClickListener(this);
        hiresMobile_sw = (SwitchCompat) findViewById(R.id.hires_mobile_sw);
        hiresMobile_sw.setChecked(isHiresMobile);

        version_v = findViewById(R.id.version_v);
        version_v.setOnClickListener(this);

        contact_v = findViewById(R.id.contact_v);
        contact_v.setOnClickListener(this);

        rateApp_v = findViewById(R.id.rate_app_v);
        rateApp_v.setOnClickListener(this);

        licenses_v = findViewById(R.id.licenses_v);
        licenses_v.setOnClickListener(this);


        try {
            version_tv = (TextView) findViewById(R.id.version_tv);
            version_tv.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {

        if(contact_v == v) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "erty.guy@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        if(rateApp_v == v) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(i);
        }
        if(licenses_v == v) {
            LicensesDialogFragment.displayLicensesFragment(getSupportFragmentManager());
        }

        if(hiresWifi_v == v) {
            isHiresWifi = !isHiresWifi;
            Var.setBoolPreference(this, Var.PREF_HIRES_WIFI, isHiresWifi);
            hiresWifi_sw.setChecked(isHiresWifi);
        }

        if(hiresMobile_v == v) {
            isHiresMobile = !isHiresMobile;
            Var.setBoolPreference(this, Var.PREF_HIRES_MOBILE, isHiresMobile);
            hiresMobile_sw.setChecked(isHiresMobile);
        }
    }

}
