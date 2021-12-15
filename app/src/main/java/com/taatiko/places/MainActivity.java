package com.taatiko.places;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TaatikoPlaces.initialize(this, "TAATiko");

        TaatikoPlaces.getPrediction(this,  "feres");
    }
}