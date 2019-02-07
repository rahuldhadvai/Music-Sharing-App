package com.example.rahulpc.audiofirelogin;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.ValueEventListener;


public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] caption;


    public CustomListAdapter(Activity context, String[] itemname, String[] caption) {
        super(context, R.layout.mylist, itemname);

        this.context=context;
        this.itemname=itemname;
        this.caption = caption;

    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null,true);

        TextView nametext = (TextView) rowView.findViewById(R.id.nametext);
        TextView captiontext = (TextView) rowView.findViewById(R.id.captiontext);

        nametext.setText(itemname[position]);
        captiontext.setText(caption[position]);
        return rowView;

    }
}
