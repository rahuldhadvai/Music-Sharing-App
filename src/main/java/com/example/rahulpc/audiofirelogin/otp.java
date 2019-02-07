package com.example.rahulpc.audiofirelogin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {
    EditText MobileNumber,OTPEditview;
    Button Submit,OTPButton;
    TextView Textview,Otp,tv;
    String phoneno;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    boolean mVerificationInProgress = false;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        MobileNumber = (EditText) findViewById(R.id.mobileNumber);
        Submit = (Button) findViewById(R.id.submit);
        OTPEditview = (EditText) findViewById(R.id.otp_editText);
        OTPButton = (Button) findViewById(R.id.otp_button);
        Textview = (TextView) findViewById(R.id.textView);
        Otp = (TextView) findViewById(R.id.otp);
        tv = (TextView)findViewById(R.id.textView4);

        mAuth = FirebaseAuth.getInstance();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                Toast.makeText(otp.this,"verification already done,please go back and login",Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(otp.this,"verification fail",Toast.LENGTH_LONG).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(otp.this,"invalid mobile no",Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(otp.this,"quota over" ,Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(otp.this,"Verification code sent to mobile",Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                MobileNumber.setVisibility(View.GONE);
                Submit.setVisibility(View.GONE);
                Textview.setVisibility(View.GONE);
                OTPButton.setVisibility(View.VISIBLE);
                OTPEditview.setVisibility(View.VISIBLE);
                Otp.setVisibility(View.VISIBLE);
                // ...
            }
        };



        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91"+MobileNumber.getText().toString(),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        otp.this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks
            }
        });

        OTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, OTPEditview.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(otp.this,"Verification done",Toast.LENGTH_LONG).show();
                            FirebaseUser user = task.getResult().getUser();
                            phoneno = user.getPhoneNumber();
                            Intent i = new Intent(otp.this,userNpass.class);
                            i.putExtra("phone_no",phoneno);
                            startActivity(i);


                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(otp.this,"Verification failed,code invalid",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

}
