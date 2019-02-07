package com.example.rahulpc.audiofirelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.app.Activity.RESULT_OK;


public class Upload_frag extends Fragment {

    public int REQUEST_CODE = 1234;
    public static final String FB_STORAGE_PATH1="Uploaded audio/";
    public static final String FB_STORAGE_PATH="Recorded audio/";
    private static final String LOG_TAG="record_log";
    public String mFileName=null;
    public Uri choosenaudio;
    TextView songname;
    ImageView imageupload,imagerecord,play,repeat,pause;
    ProgressDialog mprogress;
    EditText captiontext;
    public MediaRecorder mRecorder;
    SeekBar myseekbar;
    public Uri recorduri;
    public DatabaseReference mDatabase;
    public StorageReference mStorage;
    public String phoneno;
    public MediaPlayer mp;
    private Handler mHandler = new Handler();
    String fileName2 = "audiofireuserno.txt";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_upload, container, false);

        mp = new MediaPlayer();

        mFileName= Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName+="/recorded_audio.3gp";
        captiontext = (EditText)rootView.findViewById(R.id.text);
        mprogress = new ProgressDialog(getActivity());
        imageupload = (ImageView)rootView.findViewById(R.id.imageupload);
        imagerecord = (ImageView)rootView.findViewById(R.id.imagerecord);
        mStorage = FirebaseStorage.getInstance().getReference();
        phoneno = getLoginUserno();
        play = (ImageView)rootView.findViewById(R.id.myplay);
        pause = (ImageView)rootView.findViewById(R.id.mypause);
        repeat = (ImageView)rootView.findViewById(R.id.imagerepeat);
        songname = (TextView)rootView.findViewById(R.id.songname);
        myseekbar = (SeekBar)rootView.findViewById(R.id.seek);
        mDatabase = FirebaseDatabase.getInstance().getReference(phoneno);
      //  Button rbtn = (Button)rootView.findViewById(R.id.btnrecord);

        myseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mp.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();


            }
        }); // Important


        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();

            }
        });

        final FloatingActionButton  fab = (FloatingActionButton)rootView.findViewById(R.id.fabsend);


        imageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAudio();
            }
        });

        imagerecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    Toast.makeText(getActivity(),"recording started",Toast.LENGTH_SHORT).show();
                    startRecording();
                    return true;
                }
                else if(event.getAction()== MotionEvent.ACTION_UP)
                {
                    Toast.makeText(getActivity(),"recording stopped",Toast.LENGTH_SHORT).show();
                    stopRecording();

                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = captiontext.getText().toString();
                if(choosenaudio !=null && recorduri == null) {
                    uploadstatus(choosenaudio, text);
                    choosenaudio = null;
                }
                if(recorduri !=null && choosenaudio == null) {
                    uploadrecordedstatus(recorduri, text);
                    recorduri = null;
                }
            }
        });



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choosenaudio== null && recorduri==null)
                    Toast.makeText(getActivity(),"you did'nt select or record anything to play",Toast.LENGTH_SHORT).show();
                else
                {
                    if(choosenaudio != null && recorduri == null) {
                        try {
                            playsong(choosenaudio);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(recorduri !=null && choosenaudio == null )
                    {
                        try {
                            playsong(recorduri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp.isPlaying())
                {
                    if(mp!=null)
                    {
                        mp.pause();
                        pause.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp.isPlaying())
                {
                    mp.seekTo(0);
                    updateProgressBar();
                }
            }
        });

        return rootView;


    }


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        songname.setText("Recorded Audio");
        captiontext.setVisibility(View.VISIBLE);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        recorduri= Uri.fromFile(new File(mFileName));
    }

    public void chooseAudio() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mp3");
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode ==RESULT_OK && data != null && data.getData() != null) {
            choosenaudio = data.getData();//declared above Uri audio
            String songName = getFileAtt(choosenaudio);
            songname.setText(songName);
            captiontext.setVisibility(View.VISIBLE);

        }
    }

    public String getFileAtt(Uri audioname) {
        Cursor returnCursor;
        returnCursor = getActivity().getContentResolver().query(audioname, null, null, null, null);

        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        //int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

        returnCursor.moveToFirst();

        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;

    }

    public void uploadstatus(final Uri audiofile, final String captext)
    {
        if(audiofile != null) {
            mprogress.setTitle("uploading your audio...");
            mprogress.show();
            final String audiofilename = getFileAtt(audiofile);

            StorageReference filepath1 = mStorage.child(FB_STORAGE_PATH1 + audiofilename);

            filepath1.putFile(audiofile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mprogress.dismiss();
                    uploadstatus upload_status = new uploadstatus(captext, audiofilename, taskSnapshot.getDownloadUrl().toString());
                    mDatabase.child("status").child(mDatabase.push().getKey()).setValue(upload_status);
                    Toast.makeText(getActivity(), "uploaded successfully !!", Toast.LENGTH_SHORT).show();
                }

            })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            mprogress.setMessage("uploading..  " + (int) progress + "%");
                        }
                    });


        }
        else
        {
            Toast.makeText(getActivity(), "please select an audio", Toast.LENGTH_SHORT).show();
        }

    }

    public void uploadrecordedstatus(final Uri audiofile, final String captext)
    {
        mprogress.setTitle("uploading your audio...");
        mprogress.show();

        long time = System.currentTimeMillis();

        StorageReference filepath1 = mStorage.child(FB_STORAGE_PATH + "audio1"+time);

        filepath1.putFile(audiofile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mprogress.dismiss();
                uploadstatus upload_status = new uploadstatus(captext,"Recorded Audio",taskSnapshot.getDownloadUrl().toString());
                mDatabase.child("status").child(mDatabase.push().getKey()).setValue(upload_status);
                Toast.makeText(getActivity(),"uploaded successfully !!",Toast.LENGTH_SHORT).show();
            }

        })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        mprogress.setMessage("uploaded  " + (int) progress + "%");
                    }
                });

    }

    // playing the recorded or choosen audio

    public void playsong(Uri choosenaudio) throws IOException {
        mp.reset();
        mp.setDataSource(getActivity(),choosenaudio);
        pause.setVisibility(View.VISIBLE);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mp.prepareAsync();
        myseekbar.setProgress(0);
        myseekbar.setMax(100);
        updateProgressBar();


    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

             /*// Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration)); */

            // Updating progress bar
            int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            myseekbar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
    String getLoginUserno(){
        FileInputStream fis;
        try {

            fis = getActivity().openFileInput(fileName2);
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

