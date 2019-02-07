package com.example.rahulpc.audiofirelogin;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import java.util.HashMap;
import java.util.Map;



public class uploadstatus {

    String captiontext;
    public String audiofilename;
    public String url;

    HashMap<String, Object> timestampCreated;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)

    public uploadstatus(){}

    public uploadstatus(String captiontext,String audiofilename,String url) {
        this.captiontext = captiontext;
        this.audiofilename = audiofilename;
        this.url = url;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;

    }

    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }

    public String getCaptiontext(){return captiontext;}
    public String getaudiofilename() {
        return audiofilename;
    }
    public String getUrl() {
        return url;
    }
}
