package com.bhlwan.covid_19;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Log;



import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationService extends JobService {

    CountryModel countryModel;
    public static String TODAY_CASES = "TODAY_CASES";
    public static String TODAY_DEATHS = "TODAY_DEATHS";
    public static String PREFERNCES_TAG = "PREF_TAG";

    private void createNotification(CountryModel country, String message) {

        Intent intent ;
        URL url = null;

        try {
            url = new URL(country.getFlag());
        } catch (MalformedURLException ex) {

        }


        Bitmap img = null;
        try {
            img = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException ex) {
        }

        intent = new Intent(this, DetailActivity.class);
        intent.putExtra("position",country);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.cast_abc_scrubber_primary_mtrl_alpha)
                .setLargeIcon(img)
                .setContentIntent(pendingIntent)
                .setContentTitle(country.getCountry())
                .setContentText("Tap to to see more details...")
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat.from(this.getApplicationContext()).notify(1, builder.build());

    }

    private void doBackgroundWork(final JobParameters parms) {
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        final String current_country_iso2 = tm.getNetworkCountryIso();
        getCurrentCountryInfo(current_country_iso2);
        (new Thread(new Runnable() {
            public void run() {
                try {

                    Thread.sleep(10000L);
                } catch (InterruptedException ex) {

                }
                String today_cases = getFromSharePrefernces(TODAY_CASES);
                String today_deaths = getFromSharePrefernces(TODAY_DEATHS);
                Log.d("cases",today_cases);
                Log.d("cases2",today_deaths);
                Integer new_cases = Integer.valueOf(countryModel.getTodayCases()) - Integer.valueOf(today_cases);
                Integer new_deaths = Integer.valueOf(countryModel.getTodayDeaths()) - Integer.valueOf(today_deaths);

                if (new_cases > 0){
                    String message = String.format("There is %d new Cases", new_cases);
                    createNotification(countryModel,message);
                    saveInSharedPrefernces(TODAY_CASES,countryModel.getTodayCases());
                }

                if(new_deaths > 0){
                    String message = String.format("There is %d new Deaths", new_deaths);
                    createNotification(countryModel,message);
                    saveInSharedPrefernces(TODAY_DEATHS,countryModel.getTodayDeaths());
                }
                jobFinished(parms, true);
            }
        })).start();
    }
    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return true;
    }

    /** GET THE LATEST INFO VIRUS ABOUT YOUR CURRENT LOCATION (COUNTRY)**/
    private void getCurrentCountryInfo(String iso) {
        String url  = String.format("https://corona.lmao.ninja/v2/countries/%s",iso);
        Log.d("url",url);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                                String countryName = jsonObject.getString("country");
                                String cases = jsonObject.getString("cases");
                                String todayCases = jsonObject.getString("todayCases");
                                String deaths = jsonObject.getString("deaths");
                                String todayDeaths = jsonObject.getString("todayDeaths");
                                String recovered = jsonObject.getString("recovered");
                                String active = jsonObject.getString("active");
                                String critical = jsonObject.getString("critical");

                                JSONObject object = jsonObject.getJSONObject("countryInfo");
                                String flagUrl = object.getString("flag");

                                countryModel = new CountryModel(flagUrl,countryName,cases,todayCases,deaths,todayDeaths,recovered,active,critical);
                        } catch (JSONException e) {
                            Log.d("error",e.getMessage());
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    /**GET THE LATEST UPDATE OF INFO*/
    private String getFromSharePrefernces(String key) {
        return getApplicationContext().getSharedPreferences(PREFERNCES_TAG, Context.MODE_PRIVATE).getString(key, "0");
    }



    /** SAVE THE VIRUS INFO**/
    private void saveInSharedPrefernces(String key, String cases) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(PREFERNCES_TAG, Context.MODE_PRIVATE).edit();
        editor.putString(key, cases);
        editor.commit();
    }
}
