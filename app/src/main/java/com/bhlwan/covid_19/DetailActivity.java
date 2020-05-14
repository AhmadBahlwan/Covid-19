package com.bhlwan.covid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private  CountryModel currentCountry;
    @BindView(R2.id.tvCountry) TextView tvCountry;
    @BindView(R2.id.tvCases)TextView tvCases;
    @BindView(R2.id.tvRecovered)TextView  tvRecovered;
    @BindView(R2.id.tvCritical)TextView  tvCritical;
    @BindView(R2.id.tvActive) TextView tvActive;
    @BindView(R2.id.tvTodayCases)TextView tvTodayCases;
    @BindView(R2.id.tvDeaths)TextView tvTotalDeaths;
    @BindView(R2.id.tvTodayDeaths)TextView tvTodayDeaths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        currentCountry = (CountryModel) intent.getExtras().getSerializable("position");
        getSupportActionBar().setTitle("Details of "+currentCountry.getCountry());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvCountry.setText(currentCountry.getCountry());
        tvCases.setText(currentCountry.getCases());
        tvRecovered.setText(currentCountry.getRecovered());
        tvCritical.setText(currentCountry.getCritical());
        tvActive.setText(currentCountry.getActive());
        tvTodayCases.setText(currentCountry.getTodayCases());
        tvTotalDeaths.setText(currentCountry.getDeaths());
        tvTodayDeaths.setText(currentCountry.getTodayDeaths());
        tvTotalDeaths.setText(currentCountry.getDeaths());


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}