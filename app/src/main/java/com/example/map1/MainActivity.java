package com.example.map1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import com.example.map1.R;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = new File(getFilesDir().toString() + "/medical_data.txt");
        if(!file.exists()) {
            Intent myIntent = new Intent(MainActivity.this, Form.class);
            MainActivity.this.startActivity(myIntent);
        }
        else {
            Intent myIntent = new Intent(MainActivity.this, MapOne.class);
            MainActivity.this.startActivity(myIntent);
        }
    }
}
