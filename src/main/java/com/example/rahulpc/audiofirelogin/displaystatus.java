package com.example.rahulpc.audiofirelogin;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class displaystatus extends AppCompatActivity {

    TextView ht;
    String friendname,friendnum;
    DatabaseReference mDatabase;
    List<uploadstatus> uploadList;
    ImageView ivplay,ivpause;
    public MediaPlayer player;
    ListView slv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaystatus);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ht = (TextView)findViewById(R.id.headtext);
        uploadList = new ArrayList<>();
        slv = (ListView)findViewById(R.id.slv);
        ivplay = (ImageView)findViewById(R.id.ivplay);
        ivpause = (ImageView)findViewById(R.id.ivpause);
        friendname = getIntent().getExtras().get("contact_name").toString();
        final String[] uploadname = new String[10];
        final String[] caption = new String[10];

        ht.setText(friendname+"'s"+ "  " + "Audio Status's");

        player = new MediaPlayer();

        friendnum = getPhoneNumber(friendname);
        if(!(friendnum.startsWith("+91")))
        {
            friendnum = "+91"+friendnum;
        }
        Toast.makeText(getApplicationContext(),friendnum,Toast.LENGTH_SHORT).show();



        mDatabase = FirebaseDatabase.getInstance().getReference(friendnum).child("status");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    uploadList.add(postSnapshot.getValue(uploadstatus.class));
                }

                for (int i = 0; i < uploadList.size(); i++) {

                    uploadname[i] = uploadList.get(i).getaudiofilename();
                    caption[i] = uploadList.get(i).getCaptiontext();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), (CharSequence) databaseError,Toast.LENGTH_SHORT).show();


            }
        });

      //  Toast.makeText(getApplicationContext(),uploadname[0],Toast.LENGTH_SHORT).show();
        CustomListAdapter adapter = new CustomListAdapter(this,uploadname,caption);
        slv.setAdapter(adapter);

        slv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (player.isPlaying()) {
                    if (player != null) {
                        player.pause();
                        playStatus(position);
                    }

                } else {
                    playStatus(position);
                }
            }
        });




    }

    public void onBackPressed()
    {
        if(player.isPlaying())
        {
            player.pause();
            player.release();
        }
        Intent i = new Intent(displaystatus.this,statuslist_frag.class);
        startActivity(i);
    }

    public void playStatus(int position) {
        uploadstatus clickedsong = uploadList.get(position);
        String url = clickedsong.getUrl();
      //  Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.reset();
            player.setDataSource(url);

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }

          player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mp) {
              player.start();
             // mp.start();
          }
          });
        player.prepareAsync();

    }

    public String getPhoneNumber(String name) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
       // String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = "+name;
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        assert c != null;
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if(ret==null)
            ret = "Unsaved";
        return ret;
    }
}
