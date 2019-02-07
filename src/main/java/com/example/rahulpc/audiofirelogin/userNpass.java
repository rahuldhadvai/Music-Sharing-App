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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class userNpass extends AppCompatActivity {

    EditText edun,edpass,phoneedit;
    TextView tvun,tvpass;
    Button btn;
    static String username,password;
    DatabaseReference mDatabase;
    public String phoneno;
    ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_npass);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        edun = (EditText)findViewById(R.id.name);
        edpass = (EditText)findViewById(R.id.pass);
        tvun = (TextView)findViewById(R.id.un);
        tvpass = (TextView)findViewById(R.id.pw);
        btn = (Button)findViewById(R.id.btn);
        phoneedit = (EditText)findViewById(R.id.phoneno);
        mprogress = new ProgressDialog(this);
        phoneno = getIntent().getExtras().get("phone_no").toString();
        mDatabase = FirebaseDatabase.getInstance().getReference(phoneno);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprogress.setMessage("processing...");
                mprogress.show();
                username = edun.getText().toString();
                password = edpass.getText().toString();
                //testing purpose delete this later.
               // phoneno = "+91" + phoneedit.getText().toString();
                if(!(TextUtils.isEmpty(username)) && !(TextUtils.isEmpty(password)))
                {
                   // mDatabase = FirebaseDatabase.getInstance().getReference(phoneno);
                    uploadDt upload = new uploadDt(username,password);
                    mDatabase.child("credentials").setValue(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mprogress.dismiss();
                            Intent i = new Intent(userNpass.this,Statuslist.class);
                            i.putExtra("user_name",username);
                            i.putExtra("phone_no",phoneno);
                            startActivity(i);

                        }
                    });

                }
                else
                    Toast.makeText(getApplicationContext(),"please enter valid username and password",Toast.LENGTH_SHORT).show();


            }
        });


    }
}
