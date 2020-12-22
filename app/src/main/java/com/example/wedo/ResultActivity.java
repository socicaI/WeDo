package com.example.wedo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        String id = "";


        Bundle extras = getIntent().getExtras();

        id = extras.getString("id");



        TextView textView = (TextView) findViewById(R.id.textView_result);

        String str = id ;
        textView.setText(str);

    }
}