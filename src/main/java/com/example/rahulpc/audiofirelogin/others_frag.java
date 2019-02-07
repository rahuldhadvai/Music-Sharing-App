package com.example.rahulpc.audiofirelogin;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class others_frag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    Button sharebtn;
    ListView lv;
    String fileName1 = "Audiofireloginstatus.txt";
    public ArrayAdapter<String> adapter;
    public Set<String> finalset = new HashSet<String>();
    public DatabaseReference qDatabase,zDatabase;
    public ArrayList<String> clist = new ArrayList<>();
    public ArrayList<String> mlist = new ArrayList<>();
    String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_others, container, false);

        getLoaderManager().initLoader(14, null, this);

        qDatabase = FirebaseDatabase.getInstance().getReference();
        sharebtn = (Button)rootView.findViewById(R.id.btn);
        lv = (ListView)rootView.findViewById(R.id.otherslist);

        adapter = new ArrayAdapter<String>(getActivity(),R.layout.firstlist,mlist);
        lv.setAdapter(adapter);

        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // writeLoginstatusasFalse();

                Intent myintent = new Intent(Intent.ACTION_SEND);
                myintent.setType("text/plain");
                myintent.putExtra(Intent.EXTRA_SUBJECT,"Wave In");
                myintent.putExtra(Intent.EXTRA_TEXT,"an exclusive music sharing app "+"\n"+"https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en");

                startActivity(Intent.createChooser(myintent,"share using...."));
            }
        });
        return rootView;
    }

    public void writeLoginstatusasFalse()
    {
        Toast.makeText(getActivity(),"logged out",Toast.LENGTH_SHORT).show();
    }


    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        return new CursorLoader(getContext(), CONTENT_URI, null,null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        cursor.moveToFirst();

        HashMap<String,String> myset = new HashMap<String, String>();
        while (!cursor.isAfterLast())
        {
            String nam = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
            String num = cursor.getString(cursor.getColumnIndex(NUMBER));
            cursor.moveToNext();
            myset.put(nam,num);


        }

      //  final Set<String> finalset = new HashSet<String>();
        for (Map.Entry<String,String> entry : myset.entrySet()) {
            final String rahulnam = entry.getKey();
            String rahulnum = entry.getValue();

            if(rahulnum.contains("#"))
                continue;


            if(!(rahulnum.startsWith("+91")))
            {
                rahulnum = "+91"+rahulnum;
            }
            final String finalValuenum = rahulnum;
            qDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(finalValuenum)) {
                           // Toast.makeText(getActivity(), rahulnam, Toast.LENGTH_SHORT).show();

                            zDatabase = FirebaseDatabase.getInstance().getReference(finalValuenum).child("status");


                            zDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    mlist.clear();
                                    adapter.notifyDataSetChanged();
                                   // Set<String> finalset = new HashSet<String>();
                                    if(dataSnapshot.hasChildren()) {

                                        Toast.makeText(getActivity(), rahulnam, Toast.LENGTH_SHORT).show();

                                        finalset.add(rahulnam);
                                        mlist.clear();
                                        mlist.addAll(finalset);
                                        adapter.notifyDataSetChanged();

                                      //  listadd(rahulnam);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        }



    }

    public void listadd(String rahulnam)
    {
      //  finalset.add(rahulnam);
        mlist.clear();
      //   mlist.addAll(finalset);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}

