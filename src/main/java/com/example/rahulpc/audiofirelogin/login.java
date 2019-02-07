package com.example.rahulpc.audiofirelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity {

    EditText pn,pass;
    TextView pntext,pw;
    Button btn;
    String phoneno,password,originalpassword,loginuser;
    DatabaseReference mDatabase,sDatabase;
    ProgressDialog mprogress;
    public List<uploadDt> uploadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pn = (EditText) findViewById(R.id.pn);
        pass = (EditText) findViewById(R.id.pass);
        pntext = (TextView) findViewById(R.id.pntext);
        pw = (TextView) findViewById(R.id.pw);
        btn = (Button) findViewById(R.id.btn);
        mprogress = new ProgressDialog(this);
        uploadList = new ArrayList<>();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mprogress.setMessage("Authenticating...");
                mprogress.show();
                phoneno = pn.getText().toString();
                password = pass.getText().toString();
                if(TextUtils.isEmpty(phoneno))
                {
                    mprogress.dismiss();
                    Toast.makeText(getApplicationContext(),"enter phone number",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mprogress.dismiss();
                    Toast.makeText(getApplicationContext(),"enter password",Toast.LENGTH_SHORT).show();
                    return;
                }


                sDatabase = FirebaseDatabase.getInstance().getReference();
                sDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean numberpresence = dataSnapshot.hasChild("+91"+phoneno);
                        if(!numberpresence) {
                            Toast.makeText(getApplicationContext(), "This number is not registered", Toast.LENGTH_LONG).show();
                            mprogress.dismiss();
                        }
                        else
                        {

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("+91" + phoneno);

                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    DataSnapshot ps = dataSnapshot.child("credentials");
                                    uploadList.add(ps.getValue(uploadDt.class));
                                    /*for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        uploadList.add(postSnapshot.getValue(uploadDt.class));

                                    } */
                                    originalpassword = uploadList.get(0).getPassword();
                                    loginuser = uploadList.get(0).getUsername();
                                    if(originalpassword.equals(password))
                                    {
                                        mprogress.dismiss();
                                        Intent i = new Intent(login.this,Statuslist.class);
                                        i.putExtra("user_name",loginuser);
                                        i.putExtra("phone_no","+91"+phoneno);
                                        startActivity(i);
                                    }
                                    else {
                                        mprogress.dismiss();
                                        Toast.makeText(getApplicationContext(), "you entered a wrong password", Toast.LENGTH_SHORT).show();
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "There is some error connecting to Database", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "There is some error connecting to Database", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });


    }

}
