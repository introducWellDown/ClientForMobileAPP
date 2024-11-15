package com.example.cattleapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FeedingAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String animalName = intent.getStringExtra("animalName");
        Toast.makeText(context, "Время кормить " + animalName, Toast.LENGTH_LONG).show();
    }
}
