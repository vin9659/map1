package com.example.map1;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.map1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SMSOne extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    EditText pn;
    Button send_sms;
    StringBuilder sb1, sb2;
    String loc;
    int MY_PERMISSION_REQUEST_SEND_SMS = 1;
    private double wayLat=0.0, wayLong=0.0;

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsone);

        pn = findViewById(R.id.phNo);
        send_sms = findViewById(R.id.btn);

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SMSOne.this);

        send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = (getFilesDir().toString() + "/medical_data.txt");
                StringBuilder sb1 = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();

                try {
                    BufferedReader ir = new BufferedReader(new FileReader(filename));
                    String in_ms;
                    while((in_ms = ir.readLine()) != null) {
                        sb1.append(in_ms);
                        sb1.append("\n");
                    }
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(SMSOne.this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            if(location != null) {
                                SMSOne obj = new SMSOne();
                                wayLat=location.getLatitude();
                                wayLong=location.getLongitude();
                                Toast.makeText(SMSOne.this,"value is +", Toast.LENGTH_LONG).show();
                                obj.sb2.append(wayLat);
                                obj.sb2.append("\n");
                                obj.sb2.append(wayLong);
                                obj.sb2.append("\n");
                                loc = sb2.toString();
                            }
                        }
                    });
                }
                catch (IOException | SecurityException e) {
                    e.printStackTrace();
                }
                String message = sb1.toString();
                String phno = pn.getText().toString();
                if (ContextCompat.checkSelfPermission(SMSOne.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SMSOne.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SEND_SMS);
                } else {
                    if (phno.length() == 10) {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(phno, null, message, sentPI, deliveredPI);
                    } else {
                        Toast.makeText(SMSOne.this, "Please enter a phone number.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(SMSOne.this, "Sent: Yay!", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(SMSOne.this, "Sent: Nope; generic failure.", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(SMSOne.this, "Sent: Nope; no service.", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(SMSOne.this, "Sent: Nope; null PDU.", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(SMSOne.this, "Sent: Nope; radio off.", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(SMSOne.this, "Delivered: Yay!", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(SMSOne.this, "Delivered: Nope.", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
    }

    /*protected void sendLoc() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();
        try {
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            String lat = new Double(location.getLatitude()).toString();
            String lon = new Double(location.getLongitude()).toString();
            SMSOne obj = new SMSOne();
            obj.sb.append(lat);
            obj.sb.append("\n");
            obj.sb.append(lon);
            obj.sb.append("\n");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(SMSOne.this, "GPS Enabled.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(SMSOne.this, "GPS Disabled.", Toast.LENGTH_LONG).show();
        }
    }*/
}