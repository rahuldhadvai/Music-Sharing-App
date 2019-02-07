package com.example.rahulpc.audiofirelogin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    public VideoView vv;
    Button createactbtn, loginbtn;
    String fileName1 = "Audiofireloginstatus.txt";
    String fileName2 = "audiofireuserno.txt";
    String fileName3 = "audiofireuser.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        vv = (VideoView) findViewById(R.id.vv);
        createactbtn = (Button) findViewById(R.id.createactbtn);
        loginbtn = (Button) findViewById(R.id.loginbtn);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bgvideo22);

        vv.setVideoURI(uri);
        vv.start();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
        createactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, otp.class);
                startActivity(i);
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, login.class);
                startActivity(i);
            }
        });

        boolean login_status = getLoginStatus();

        String login_userno = getLoginUserno();
        String login_username = getLoginUsername();

        if(login_status && !(TextUtils.isEmpty(login_userno)))
        {
            Intent i = new Intent(MainActivity.this,Statuslist.class);
            i.putExtra("user_name",login_username);
            i.putExtra("phone_no",login_userno);
            startActivity(i);
        }
    }

   Boolean getLoginStatus(){
       FileInputStream fis;
       try {

           fis = openFileInput(fileName1);
           InputStreamReader isr = new InputStreamReader(fis);
           // READ STRING OF UNKNOWN LENGTH
           StringBuilder sb = new StringBuilder();
           char[] inputBuffer = new char[2048];
           int l;
           // FILL BUFFER WITH DATA
           while ((l = isr.read(inputBuffer)) != -1) {
               sb.append(inputBuffer, 0, l);
           }
           // CONVERT BYTES TO STRING
           String readString = sb.toString();
           fis.close();
           return readString.contentEquals("true");

       } catch (IOException e) {
           e.printStackTrace();
       }
       return false;
   }

    String getLoginUserno(){
        FileInputStream fis;
        try {

            fis = openFileInput(fileName2);
            InputStreamReader isr = new InputStreamReader(fis);
            // READ STRING OF UNKNOWN LENGTH
            StringBuilder sb = new StringBuilder();
            char[] inputBuffer = new char[2048];
            int l;
            // FILL BUFFER WITH DATA
            while ((l = isr.read(inputBuffer)) != -1) {
                sb.append(inputBuffer, 0, l);
            }
            // CONVERT BYTES TO STRING
            String readString = sb.toString();
            fis.close();
            return readString;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    String getLoginUsername(){
        FileInputStream fis;
        try {

            fis = openFileInput(fileName3);
            InputStreamReader isr = new InputStreamReader(fis);
            // READ STRING OF UNKNOWN LENGTH
            StringBuilder sb = new StringBuilder();
            char[] inputBuffer = new char[2048];
            int l;
            // FILL BUFFER WITH DATA
            while ((l = isr.read(inputBuffer)) != -1) {
                sb.append(inputBuffer, 0, l);
            }
            // CONVERT BYTES TO STRING
            String readString = sb.toString();
            fis.close();
            return readString;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
