/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTV;
    private TextView mErrorTV;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mWeatherTV = findViewById(R.id.tv_weather_data);
        mErrorTV = findViewById(R.id.tv_error_message_display);
        mProgressBar = findViewById(R.id.pb_loading_indicator);

        loadWeatherData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mWeatherTV.setText("");
            loadWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class getWeatherDataAsync extends AsyncTask <String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            String location = strings[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                showWeatherDataView();
                for (String weatherString : weatherData ) {
                    mWeatherTV.append(weatherString + "\n\n");
                }
            }
            else {
                showErrorMessage();
            }

        }
    }

    private void loadWeatherData() {
        showWeatherDataView();
        String loc = SunshinePreferences.getPreferredWeatherLocation(this);
        new getWeatherDataAsync().execute(loc);
    }

    private void showWeatherDataView() {
        mWeatherTV.setVisibility(View.VISIBLE);
        mErrorTV.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mWeatherTV.setVisibility(View.INVISIBLE);
        mErrorTV.setVisibility(View.VISIBLE);
    }


}