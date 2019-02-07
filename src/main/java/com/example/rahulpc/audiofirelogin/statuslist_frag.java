package com.example.rahulpc.audiofirelogin;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.Toast;

public class statuslist_frag extends Fragment {

    public ListView listview;
    public List<contacts> mylist;
    public List<String> filterlist = new ArrayList<>();
    public List<String> deletionlist;
    public List<String> statusfilter;
    public ProgressDialog mprogress;
    public ArrayList<String> clist = new ArrayList<>();
    public ArrayList<String> mlist = new ArrayList<>();
    public DatabaseReference mDatabase,sDatabase,fDatabase;
    public String[] finalcontacts;
    public ArrayAdapter<String> adapter;
    public ArrayList<String> contactList,con,friendslist;
  //  public ArrayList<String> dlist = new ArrayList<>();
    public Set<String> displayset = new HashSet<String>();

   // ContactsController cc;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_statuslist, container, false);
        listview = (ListView)rootView.findViewById(R.id.contactslist);
        mylist = new ArrayList<>();
        deletionlist = new ArrayList<>();
        statusfilter = new ArrayList<>();

       // cc = new ContactsController("hello");
       // cc.sayhello();
        adapter = new ArrayAdapter<String>(getActivity(),R.layout.firstlist,clist);
        listview.setAdapter(adapter);

        mprogress = new ProgressDialog(getActivity());
       mDatabase = FirebaseDatabase.getInstance().getReference();

        filterstatus();

     //   getuploadedcontacts1();

     //  displayfinalcontacts();

       // getcontacts2();






        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(),displaystatus.class);
                i.putExtra("contact_name",((TextView)view).getText().toString());
                startActivity(i);
            }
        });


        return rootView;



    }



   /* public void displayfinalcontacts() {


        mDatabase = FirebaseDatabase.getInstance().getReference();
        con = new ArrayList<String>();

        con = getContacts();

        for(int i = 0; i< con.size() ;i++)
        {
           final String pn = getPhoneNumber(con.get(i));

            if(pn.startsWith("+"))
            {
                final int finalI = i;
                mDatabase.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {

                         final Set<String> displayset = new HashSet<String>();
                        // clist.clear();
                        // adapter.notifyDataSetChanged();
                        // displayset.clear();
                         if(dataSnapshot.hasChild(pn))
                         {

                             fDatabase = FirebaseDatabase.getInstance().getReference(pn).child("status");
                             fDatabase.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                     if(dataSnapshot.hasChildren()) {
                                         displayset.add(con.get(finalI));
                                         clist.clear();
                                         clist.addAll(displayset);
                                         adapter.notifyDataSetChanged();
                                         Toast.makeText(getActivity(),con.get(finalI),Toast.LENGTH_SHORT).show();
                                     }


                                 }

                                 @Override
                                 public void onCancelled(DatabaseError databaseError) {

                                 }
                             });
                         }
                         clist.clear();
                         clist.addAll(displayset);
                         adapter.notifyDataSetChanged();
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });
            }



        }
       clist.clear();
       clist.addAll(displayset);
       adapter.notifyDataSetChanged();

    }  */

    public void addtodisplayset(String name)
    {
       /* displayset.add(name);
        clist.clear();
        clist.addAll(displayset);
        adapter.notifyDataSetChanged();  */
    }

    private void filterstatus() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    deletionlist.add(postSnapshot.getKey());
                }
                String[] friends = new String[deletionlist.size()];

                for(int i =0; i< friends.length ; i++)
                {
                    sDatabase = FirebaseDatabase.getInstance().getReference(deletionlist.get(i)).child("status");

                    long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS);
                    Query oldItems = sDatabase.orderByChild("timestampCreated/timestamp").endAt(cutoff);
                    oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                                itemSnapshot.getRef().removeValue();
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

    public ArrayList<String> getContacts1() {

        Set<String> cset =  new TreeSet<String>();
        contactList = new ArrayList<String>();
        Cursor cursor;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;


        cursor = getActivity().getContentResolver().query(CONTENT_URI, null, null, null, null);


        assert cursor != null;
        while (cursor.moveToNext()) {

            String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

            cset.add(name);
        }

        // Set<String> tree_Set = new TreeSet<String>(cset);

        contactList.addAll(cset);
        cursor.close();
        return contactList;

    }

  /*  public void getcontacts2()
    {
        friendslist = new ArrayList<String>();
        friendslist = getContacts1();

     //   mDatabase = FirebaseDatabase.getInstance().getReference();

        for(int i = 0 ; i < friendslist.size() ; i++)
        {
            String rahul = getPhoneNumber(friendslist.get(i));

            final int finalI = i;

            clist.add(rahul);

            if(!(rahul.startsWith("+")))
            {
                rahul = "+91"+rahul;
            }

            if(rahul.startsWith("+"))
            {
                final String finalRahul = rahul;
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(finalRahul)) {
                            Toast.makeText(getActivity(), friendslist.get(finalI), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
        adapter.notifyDataSetChanged();



    }  */
    private void getuploadedcontacts1()
    {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mlist.add(postSnapshot.getKey());
                }
                String[] friends = new String[mlist.size()];
                for (int i = 0; i < friends.length; i++) {

                    fDatabase = FirebaseDatabase.getInstance().getReference(mlist.get(i)).child("status");
                    final int finalI = i;
                    fDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                              //  statusfilter.add(clist.get(finalI));
                                displaythis(mlist.get(finalI));
                               // Toast.makeText(getActivity(),statusfilter.get(finalI),Toast.LENGTH_SHORT).show();

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


    public void displaythis(String orinumber)
    {
        String contactname = null;

      //  Set<String> set = new HashSet<String>();

        if (contactExists(orinumber)) {
            contactname = getContactNameFromNumber(orinumber);
            displayset.add(contactname);
           // Toast.makeText(getActivity(), contactname, Toast.LENGTH_SHORT).show();
        }
        else {
            String fullnum = orinumber;
            fullnum = fullnum.substring(3);
            if (contactExists(fullnum)) {
                contactname = getContactNameFromNumber(fullnum);
               displayset.add(contactname);
              //  Toast.makeText(getActivity(), contactname, Toast.LENGTH_SHORT).show();
            }
        }

        clist.clear();
        clist.addAll(displayset);

        adapter.notifyDataSetChanged();

       /* finalcontacts = new String[filterlist.size()];
        for(int i =0 ; i< finalcontacts.length;i++)
        {
            finalcontacts[i] = filterlist.get(i);
            Toast.makeText(getActivity(),finalcontacts[i],Toast.LENGTH_SHORT).show();
        }

          ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.firstlist, finalcontacts);
        listview.setAdapter(adapter);  */
    }


    private String getContactNameFromNumber(String number) {
        String corresname = null;
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));

        Cursor cursor = getActivity().getContentResolver().query(uri,
                new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            corresname = cursor.getString(cursor
                    .getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }

        cursor.close();
        return corresname;
    }

    public boolean contactExists( String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
        Cursor cur = getActivity().getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            assert cur != null;
            if (cur.moveToFirst()) {
                return true;
            }
        }
        finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public String getPhoneNumber(String name) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
      //  String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = "+name;
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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


  /*  public void getContacts()
    {
        mprogress.setMessage("loading contacts .....");
        mprogress.show();
        String phoneNumber;
        Cursor cursor;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String _ID = ContactsContract.Contacts._ID;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;


        cursor = getActivity().getContentResolver().query(CONTENT_URI, null,null, null, null);


        assert cursor != null;
        while (cursor.moveToNext())
        {
             String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

            String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));



            int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
            if (hasPhoneNumber > 0) {
                //This is to read multiple phone numbers associated with the same contact
                Cursor phoneCursor = getActivity().getContentResolver().query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                assert phoneCursor != null;
                while (phoneCursor.moveToNext()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    if(phoneNumber.startsWith("+91")) {
                        mylist.add(new contacts(name, phoneNumber));

                    }
                    else{
                        String pno = "+91"+phoneNumber;
                        mylist.add(new contacts(name,pno));
                    }

                }
             phoneCursor.close();
            }
        }

        cursor.close();

        mprogress.dismiss();

       String[] uploads = new String[mylist.size()];
        for (int i = 0; i < uploads.length; i++) {
            uploads[i] = mylist.get(i).getName();

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, uploads);
        listview.setAdapter(adapter);

        Toast.makeText(getActivity(),"done",Toast.LENGTH_SHORT).show();



    } */

}

