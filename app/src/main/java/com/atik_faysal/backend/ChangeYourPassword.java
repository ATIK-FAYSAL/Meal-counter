package com.atik_faysal.backend;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.ForgetPassword;
import com.atik_faysal.mealcounter.LogInActivity;
import com.atik_faysal.mealcounter.NeedSomeMethod;
import com.atik_faysal.mealcounter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import com.atik_faysal.backend.InformationCheckBackgroundTask.OnAsyncTaskInterface;
import static android.content.ContentValues.TAG;

/**
 * Created by USER on 1/28/2018.
 */

public class ChangeYourPassword extends AppCompatActivity
{
        private EditText eCode;
        private Button bVerify;
        private Button bResend;
        private TextView tTimer;


        private String phoneVerificationId;
        private PhoneAuthProvider.OnVerificationStateChangedCallbacks
                verificationCallbacks;
        private PhoneAuthProvider.ForceResendingToken resendToken;
        private FirebaseAuth fbAuth;
        private Timer timer;
        private AlertDialogClass dialogClass;
        private NeedSomeMethod someMethod;
        private CountDownTimer countDownTimer;
        private AlertDialog alertDialog;

        private String phoneNumber,userName;
        private String password;

        private static final String FILE_URL = "http://192.168.56.1/changePassword.php";
        private static String POST_DATA ;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.code_verification);
                fbAuth = FirebaseAuth.getInstance();
                initComponent();
        }

        private void initComponent()
        {
                eCode = findViewById(R.id.eCode);
                bVerify = findViewById(R.id.bVerify);
                bResend = findViewById(R.id.bResend);
                tTimer = findViewById(R.id.tTimer);


                dialogClass = new AlertDialogClass(this);
                someMethod = new NeedSomeMethod(this);

                userName = getIntent().getExtras().getString("userName");
                sendCode();
                startTimeCount();

                if(eCode.getText().toString().isEmpty())bVerify.setEnabled(false);

                eCode.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(eCode.getText().toString().length()<6)bVerify.setEnabled(false);
                                else bVerify.setEnabled(true);
                        }
                });

                bVerify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                verifyCode();
                                //changYourPassword();
                        }
                });
                bResend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                resendCode();
                        }
                });
        }

        private void startTimeCount()
        {
                countDownTimer = new CountDownTimer(30000, 1000){
                        int tCounter=59;
                        public void onTick(long millisUntilFinished){
                                tTimer.setText(String.valueOf(tCounter)+" Sec");
                                tCounter--;
                        }
                        public  void onFinish(){
                                finish();
                        }
                }.start();
        }

        private void sendCode() {

                phoneNumber = "01835897172";
                setUpVerificationCallbacks();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        verificationCallbacks);
        }

        private void setUpVerificationCallbacks() {

                verificationCallbacks =
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                @Override
                                public void onVerificationCompleted(
                                        PhoneAuthCredential credential) {
                                }

                                @Override
                                public void onVerificationFailed(FirebaseException e) {

                                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                // Invalid request
                                                Log.d(TAG, "Invalid credential: "
                                                        + e.getLocalizedMessage());
                                        } else if (e instanceof FirebaseTooManyRequestsException) {
                                                // SMS quota exceeded
                                                Log.d(TAG, "SMS Quota exceeded.");
                                        }
                                }

                                @Override
                                public void onCodeSent(String verificationId,
                                                       PhoneAuthProvider.ForceResendingToken token) {

                                        phoneVerificationId = verificationId;
                                        resendToken = token;
                                }
                        };
        }

        private void verifyCode() {

                String code = eCode.getText().toString();

                PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(phoneVerificationId, code);
                signInWithPhoneAuthCredential(credential);
        }

        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
                fbAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                                changYourPassword();
                                        } else {
                                                if (task.getException() instanceof
                                                        FirebaseAuthInvalidCredentialsException) {
                                                        // The verification code entered was invalid
                                                        Toast.makeText(ChangeYourPassword.this,"Error",Toast.LENGTH_SHORT).show();
                                                }
                                        }
                                }
                        });
        }
        private void resendCode() {

                if(countDownTimer!=null)
                {
                        countDownTimer.cancel();
                        tTimer.setText("");
                }
                phoneNumber = "01835897172"; //getIntent().getExtras().getString("phone");
                startTimeCount();
                setUpVerificationCallbacks();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        verificationCallbacks,
                        resendToken);
        }


        private void changYourPassword() {
                if(countDownTimer!=null)countDownTimer.cancel();

                View view = LayoutInflater.from(this).inflate(R.layout.pass_change, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                builder.setCancelable(false);

                final EditText ePassword;
                final Button bChange;


                ePassword = view.findViewById(R.id.ePassword);
                bChange = view.findViewById(R.id.bChange);

                if(ePassword.getText().toString().isEmpty())bChange.setEnabled(false);
                else bChange.setEnabled(true);

                ePassword.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                                if(ePassword.getText().toString().length()<6)bChange.setEnabled(false);
                                else if(ePassword.getText().toString().length()>15)bChange.setEnabled(false);
                                else bChange.setEnabled(true);
                        }
                });

                alertDialog = builder.create();
                alertDialog.show();

                bChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                try {
                                        POST_DATA = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                                                +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(ePassword.getText().toString(),"UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                }
                                InformationCheckBackgroundTask checkBackgroundTask = new InformationCheckBackgroundTask(ChangeYourPassword.this);
                                checkBackgroundTask.setOnResultListener(onAsyncTaskInterface);
                                checkBackgroundTask.execute(FILE_URL, POST_DATA);
                        }
                });

        }

        OnAsyncTaskInterface onAsyncTaskInterface = new OnAsyncTaskInterface() {
                @Override
                public void onResultSuccess(final String message) {
                        runOnUiThread(new Runnable() {
                                public void run() {
                                        switch (message) {
                                                case "failed":
                                                        Toast.makeText(ChangeYourPassword.this,"Error updating password,check your username",Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();
                                                        someMethod.closeActivity(ChangeYourPassword.this, ForgetPassword.class);
                                                        break;
                                                case "offline":
                                                        dialogClass.noInternetConnection();
                                                        alertDialog.dismiss();
                                                        break;
                                                default:
                                                        Toast.makeText(ChangeYourPassword.this,message,Toast.LENGTH_SHORT).show();
                                                        someMethod.closeActivity(ChangeYourPassword.this, LogInActivity.class);
                                                        alertDialog.dismiss();
                                                        break;
                                        }
                                }
                        });
                }
        };
}
