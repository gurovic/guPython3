package ru.gurovic.gupython3;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onClick(View v) {
        Log.d("METHOD", "Start-onClick");
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
    }



}
