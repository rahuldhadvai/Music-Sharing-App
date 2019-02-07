package com.example.rahulpc.audiofirelogin;

/* this class is used to retrive and update contact information */

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactsController extends Activity{

   ArrayList<String> contactList;

    String name;
   public ContactsController(String name)
    {

        this.name = name;

    }


    public ArrayList<String> getContacts() {

        contactList = new ArrayList<>();
       // Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
        String phoneNumber;
        Cursor cursor;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String _ID = ContactsContract.Contacts._ID;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;


        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null,null, null, null);


        assert cursor != null;
        while (cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

            String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));



            int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
            if (hasPhoneNumber > 0) {
                //This is to read multiple phone numbers associated with the same contact
                Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
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

                }
                contactList.add(name);
                phoneCursor.close();
            }
        }

        cursor.close();
        return contactList;

    }
}


