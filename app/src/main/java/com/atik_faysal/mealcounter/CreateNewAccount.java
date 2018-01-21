package com.atik_faysal.mealcounter;


import android.content.Intent;
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
import com.atik_faysal.model.*;
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
        //String variable declaration
        private String name,userName,address,password,email;

        //class object declaration
        private PhoneNumberVerification numberVerification;
        private UserInformationModel userInformationModel;
        private InsertMemberInformation insertMemberInformation;
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

                //object initialize
                //numberVerification = new PhoneNumberVerification(this);
                userInformationModel = new UserInformationModel();
                insertMemberInformation = new InsertMemberInformation(this);
                numberVerification = new PhoneNumberVerification();

                //calling method
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
                                //userInformation();
                                /*if(checkUserInformation(name,userName,email,password))
                                {
                                        startActivity(new Intent(CreateNewAccount.this,PhoneNumberVerification.class));
                                        userInformationModel.setName("atik");
                                        userInformationModel.setEmail("atik@gmail.com");
                                        userInformationModel.setAddress("dhaka");
                                        userInformationModel.setUserName("atik1404");
                                        userInformationModel.setPassword("123456");
                                }*/
                                //numberVerification.setUserInformation("atik","atik1404","atik@gmail.com","dhaka","12345");
                                /*Intent intent = new Intent(CreateNewAccount.this,PhoneNumberVerification.class);
                                intent.putExtra("name","atik");
                                intent.putExtra("userName","atik1404");
                                intent.putExtra("email","atik@gmail.com");
                                intent.putExtra("address","dhaka");
                                intent.putExtra("password","atik123");
                                startActivity(intent);*/

                               InsertMemberInformation memberInformation = new InsertMemberInformation(CreateNewAccount.this);
                                memberInformation.execute("insertMember","atik","atik123","atik@gmail.com","01794037303","dhaka","12345");
                        }
                });
        }

        private boolean checkUserInformation(String name,String userName,String email,String password)
        {
                boolean flag = true;

                if(password.length()<6)
                {
                        ePassword.setError("Too short");
                        ePassword.requestFocus();
                        flag = false;
                }

                if(eAddress.getText().toString().isEmpty())
                {
                        eAddress.setError("Invalid address");
                        flag = false;
                        eAddress.requestFocus();
                }

                if(!email.contains("@"))
                {
                        eEmail.setError("Invalid email");
                        eEmail.requestFocus();
                        flag = false;
                }

                if(name.length()<3)
                {
                        eName.setError("Invalid name");
                        eName.requestFocus();
                        flag = false;
                }

                for(char c : name.toCharArray()){
                        if(Character.isDigit(c)){
                                eName.setError("Invalid Name");
                                flag = false;
                                eName.requestFocus();
                        }
                }

                if(userName.length()<5)
                {
                        eUserName.setError("User name must be in 6 to 12 character");
                        eUserName.requestFocus();
                        flag = false;
                }
                return flag;
        }

}
