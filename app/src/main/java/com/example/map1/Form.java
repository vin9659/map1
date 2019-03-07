package com.example.map1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.map1.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Form extends AppCompatActivity {
    EditText nme;
    EditText age;
    EditText ec;
    EditText ar;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        nme = findViewById(R.id.eTName);
        age = findViewById(R.id.eTAge);
        ec = findViewById(R.id.mLEC);
        ar = findViewById(R.id.mLAR);
        register = findViewById(R.id.btn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = checkDataEntered();
                if(x == 1) {
                    saveToTXT();
                    checkingStuff();
                    Intent hello = new Intent(Form.this, MapOne.class);
                    Form.this.startActivity(hello);
                }
            }
        });
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    int checkDataEntered() {
        int x = 1;
        if(isEmpty(nme)) {
            Toast.makeText(this, "Sorry, this is a mandatory field!", Toast.LENGTH_LONG).show();
            x = 0;
        }
        if(isEmpty(age)) {
            Toast.makeText(this, "Sorry, this is a mandatory field!", Toast.LENGTH_LONG).show();
            x = 0;
        }
        return x;
    }

    void saveToTXT() {
        try {
            File file = new File(getFilesDir().toString() + "/medical_data.txt");
            if(file.exists()) {
                file.delete();
            }
            FileWriter fw = new FileWriter(file);
            fw.append("Name: ");
            fw.append(nme.getText().toString());
            fw.append("\n");
            fw.append("Age: ");
            fw.append(age.getText().toString());
            fw.append("\n");
            if(!isEmpty(ec)) {
                fw.append("Existing Conditions: ");
                fw.append(ec.getText().toString());
                fw.append("\n");
            }
            if(!isEmpty(ar)) {
                fw.append("Allergies and Reactions: ");
                fw.append(ar.getText().toString());
                fw.append("\n");
            }
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void checkingStuff() {
        File file = new File(getFilesDir().toString() + "/medical_data.txt");
        if(file.exists()) {
            Toast.makeText(this, "Yay!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Nay...", Toast.LENGTH_LONG).show();
        }
    }

}
