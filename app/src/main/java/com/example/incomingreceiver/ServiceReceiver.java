package com.example.incomingreceiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.incomingreceiver.MainActivity.call_count;


public class ServiceReceiver extends BroadcastReceiver{

    final MediaRecorder uprecorder = new MediaRecorder();
    final MediaRecorder downrecorder=new MediaRecorder();
    static boolean init;
    long start_time,end_time;
    static final String path1=Environment.getExternalStorageDirectory().getAbsolutePath()+"/call records/recording.3GP",path2=Environment.getExternalStorageDirectory().getAbsolutePath()+"/call records/downlink.3GP";

    @Override
    public void onReceive(Context context, Intent intent){
        String uid=FirebaseAuth.getInstance().getUid();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("audio/"+uid);
        init=false;
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

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){         //call is picked up
                start_time = System.currentTimeMillis();
                Toast.makeText(context, "Call Received", Toast.LENGTH_LONG).show();
                // make sure the directory we plan to store the recording in exists

                File directory1 = new File(path1).getParentFile();
                File directory2 = new File(path2).getParentFile();
                if (!directory1.exists() && !directory1.mkdirs() || !directory2.exists() && !directory2.mkdirs()) {
                    throw new IOException("Path to file could not be created.");
                }

                ref.setValue("start");

//                uprecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
//                uprecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                uprecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                uprecorder.setOutputFile(path1);
//                uprecorder.prepare();
//                uprecorder.start();

//                downrecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
//                downrecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                downrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                downrecorder.setOutputFile(path2);
//                downrecorder.prepare();
//                downrecorder.start();
                init=true;

                String number=TelephonyManager.EXTRA_INCOMING_NUMBER;
                long totalTime = end_time - start_time;
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String formattedDate = dateFormat.format(date);

                Cell c = null;
                Row row = MainActivity.s.createRow(call_count);

                c = row.createCell(0);
                c.setCellValue(number);
                c = row.createCell(1);
                c.setCellValue(totalTime);
                c = row.createCell(2);
                c.setCellValue(formattedDate);
                call_count++;

            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                end_time = System.currentTimeMillis();
                Toast.makeText(context, "Phone Is Idle", Toast.LENGTH_LONG).show();
                ref.setValue("stop");
//                    uprecorder.stop();
//                    uprecorder.release();
//                    downrecorder.stop();
//                    downrecorder.release();
                    init=false;
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

}