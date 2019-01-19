package com.example.incomingreceiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ServiceReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        String uid=FirebaseAuth.getInstance().getUid();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("audio/"+uid);
        try{
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Toast.makeText(context, "Phone Is Ringing", Toast.LENGTH_LONG).show();
                TelecomManager tm = (TelecomManager) context
                        .getSystemService(Context.TELECOM_SERVICE);

                int permission=ContextCompat.checkSelfPermission(context,Manifest.permission.ANSWER_PHONE_CALLS);
                if (permission!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context, "Answer call permission required", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    tm.acceptRingingCall();
                }
            }

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Toast.makeText(context, "Call Received", Toast.LENGTH_LONG).show();
                ref.setValue("start");

            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(context, "Phone Is Idle", Toast.LENGTH_LONG).show();
                ref.setValue("stop");
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

}