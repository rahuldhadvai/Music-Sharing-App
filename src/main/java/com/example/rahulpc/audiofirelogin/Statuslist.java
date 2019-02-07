package com.example.rahulpc.audiofirelogin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


public class Statuslist extends AppCompatActivity {

    ListView lv;
    static String loginuser,phone_no;
    String fileName1 = "Audiofireloginstatus.txt";
    String fileName2 = "audiofireuserno.txt";
    String fileName3 = "audiofireuser.txt";
    public ArrayList<String> clist;
    public ArrayList<String> contactList;

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statuslist);

        lv = (ListView)findViewById(R.id.lv);
        btn = (Button)findViewById(R.id.btn);
        writeLoginstatusasTrue();
        clist = new ArrayList<String>();

       // clist = getContacts();

        ActivityCompat.requestPermissions(Statuslist.this,
                new String[]{Manifest.permission.READ_CONTACTS},
                1);




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.firstlist, clist);
        lv.setAdapter(adapter);

        try {
            loginuser = getIntent().getExtras().get("user_name").toString();
            phone_no = getIntent().getExtras().get("phone_no").toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        writeuserphoneno(phone_no);
        writeusername(loginuser);

        Toast.makeText(getApplicationContext(), "welcome  "+loginuser , Toast.LENGTH_SHORT).show();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Statuslist.this,original1.class);
                startActivity(i);
            }
        });

        // this block is user to update the user's login status when user clicks on logout button.
        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                writeLoginstatusasFalse();
                Intent i = new Intent(Statuslist.this,MainActivity.class);
                startActivity(i);
            }
        }); */

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(),"yay permission is granted",Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Statuslist.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void writeLoginstatusasTrue()
    {
        FileOutputStream fos;
        try {

            fos = openFileOutput(fileName1, Context.MODE_PRIVATE);
            fos.write("true".getBytes());
            //Toast.makeText(getApplicationContext(),"status updated",Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();

        } catch (Exception e) {e.printStackTrace();}
    }

    public void writeLoginstatusasFalse()
    {
        FileOutputStream fos;
        try {

            fos = openFileOutput(fileName1, Context.MODE_PRIVATE);
            fos.write("false".getBytes());
            //Toast.makeText(getApplicationContext(),"status updated",Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();

        } catch (Exception e) {e.printStackTrace();}
    }

    public void writeuserphoneno(String phone_no)
    {
        try {

            FileOutputStream fos;
            fos = openFileOutput(fileName2, Context.MODE_PRIVATE);
            fos.write(phone_no.getBytes());
            Toast.makeText(getApplicationContext(),"user number updated  as"+phone_no,Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();

        } catch (Exception e) {e.printStackTrace();}
    }

    public void writeusername(String loginuser)
    {
        try {

            FileOutputStream fos;
            fos = openFileOutput(fileName3, Context.MODE_PRIVATE);
            fos.write(loginuser.getBytes());
           // Toast.makeText(getApplicationContext(),"status updated",Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();

        } catch (Exception e) {e.printStackTrace();}

    }

    public ArrayList<String> getContacts() {

        Set<String> cset =  new TreeSet<String>();
       // Set<String> nset =  new HashSet<String>();
        contactList = new ArrayList<String>();
        // Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
        String phoneNumber;
        Cursor cursor;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String _ID = ContactsContract.Contacts._ID;
     /*   Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;  */


        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);


        assert cursor != null;
        while (cursor.moveToNext()) {

            String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

            cset.add(name);
           /* String contact_id = cursor.getString(cursor.getColumnIndex(_ID));


            int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
            if (hasPhoneNumber > 0) {
                //This is to read multiple phone numbers associated with the same contact
                Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                assert phoneCursor != null;
                while (phoneCursor.moveToNext()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    if(phoneNumber.startsWith("+91")) {
                        //  contactList.add(name);

                    }
                    else{
                        String pno = "+91"+phoneNumber;
                        //contactList.add(new contacts(name,pno));
                    }

                 //   nset.add(phoneNumber);
                }


               // phoneCursor.close();
            }  */
        }

        // Set<String> tree_Set = new TreeSet<String>(cset);

        contactList.addAll(cset);
        Toast.makeText(getApplicationContext(),"names = "+ contactList.size(), Toast.LENGTH_SHORT).show();
       // Toast.makeText(getApplicationContext(),"numbers = "+ nset.size(), Toast.LENGTH_SHORT).show();
        cursor.close();
        return contactList;

    }


}
