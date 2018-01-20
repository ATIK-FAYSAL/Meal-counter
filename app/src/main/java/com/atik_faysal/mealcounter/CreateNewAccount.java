package com.atik_faysal.mealcounter;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.atik_faysal.backend.*;
import com.facebook.accountkit.ui.LoginType;
import static android.content.ContentValues.TAG;

/**
 * initComponent-->Void.    initialize all component and object,also call some method.
 * userInformation-->Void.  get userInformation from component.
 * onButtonClick-->Void.    start action by clicking on button.
 * checkUserInformation-->Boolean.  check user information,if  information follow condition then return true ,otherwise return false
 */


public class CreateNewAccount extends AppCompatActivity
{
        //component variable
        private EditText eName,eUserName,ePassword,eAddress,eEmail;
        private TextView txtSign,txtProceed;

        private String name,userName,address,password,email;
        private  String phoneNumber;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.create_account);
                initComponent();//initialize all component and variable
        }

        private void initComponent()
        {
                //component initialize
                eName = findViewById(R.id.txtName);
                eUserName = findViewById(R.id.txtUserName);
                eEmail = findViewById(R.id.txtEmail);
                eAddress = findViewById(R.id.txtAddress);
                ePassword = findViewById(R.id.txtPassword);
                txtSign = findViewById(R.id.txtSignIn);
                txtProceed = findViewById(R.id.txtProceed);

                //calling method
                userInformation();
                onButtonClick();
        }

        private void userInformation()
        {
                name = eName.getText().toString();
                userName = eUserName.getText().toString();
                email = eEmail.getText().toString();
                password = ePassword.getText().toString();
                address = eAddress.getText().toString();
        }

        private void onButtonClick()
        {
                txtSign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                });

                txtProceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                PhoneNumberVerification phoneNumberVerification = new PhoneNumberVerification(CreateNewAccount.this);
                                phoneNumberVerification.onLogin(LoginType.PHONE);

                        }
                });
        }

        private boolean checkUserInformation(String name,String userName,String email,String password)
        {
                boolean flag = true;
                Pattern ps = Pattern.compile("[a-zA-Z]");
                Matcher ms = ps.matcher(name);
                if(!ms.matches())
                {
                        eName.setError("Invalid Name");
                        flag = false;
                        eName.requestFocus();
                }

                if(!email.contains("@"))
                {
                        eEmail.setError("Invalid email");
                        flag = false;
                }

                if(password.length()<6)
                {
                        ePassword.setError("Too short");
                        flag = false;
                }

                if(userName.length()<5)
                {
                        eUserName.setError("User name must be in 6 to 12 character");
                        flag = false;
                }

                if(name.length()<3)
                {
                        eName.setError("Invalid name");
                        flag = false;
                }

                return flag;
        }


}
