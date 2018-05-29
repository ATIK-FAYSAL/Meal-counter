package com.atik_faysal.others;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.atik_faysal.mealcounter.R;


import de.hdodenhof.circleimageview.CircleImageView;

public class AboutUs extends AppCompatActivity implements View.OnClickListener
{
        CircleImageView imgFacebook,imgGmail,imgGithub,imgLinkedin;
        private static final String myFacebookProfile = "atikfaysal1404";
        private static final String myGmail = "atikfaysal1404@logo_gmail.com";
        private static final String myLinkedin = "https://www.linkedin.com/in/atik-faysal-368a6412b/";
        private static final String myGithub = "https://logo_github.com/ATIK-FAYSAL";
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.about_us);

                imgFacebook = findViewById(R.id.facebook);
                imgGmail = findViewById(R.id.gmail);
                imgGithub = findViewById(R.id.github);
                imgLinkedin = findViewById(R.id.linkedin);
                setToolbar();
        }

        //set a toolbar,above the page
        private void setToolbar()
        {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(R.drawable.icon_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
        }

        //show my facebook profile
        private void openFacebook()
        {
                Intent intent;
                try {
                        this.getPackageManager().getPackageInfo("com.facebook.katana",0);
                        intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"+myFacebookProfile));
                        startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                        intent =  new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.facebook.com/"+myFacebookProfile));
                        startActivity(intent);
                }
        }

        //send a mail to my mail address
        private void openGmail()
        {
                Intent page = new Intent(Intent.ACTION_SEND);
                page.setType("plain/text");
                page.putExtra(Intent.EXTRA_EMAIL, new String[]{myGmail});
                startActivity(page);
        }

        ////show my logo_github profile
        private void openGithub()
        {
                Uri uri = Uri.parse(myGithub);
                Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
        }

        //show my linkedin profile
        private void openLinkedin()
        {
                Uri uri = Uri.parse(myLinkedin);
                Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
        }

        @Override
        public void onClick(View view) {
                switch (view.getId())
                {
                        case R.id.facebook:
                                openFacebook();//open facebook app or open browser
                                break;
                        case R.id.gmail:
                                openGmail();//open logo_gmail box to send a mail
                                break;
                        case R.id.github:
                                openGithub();//open browser and show logo_github profile
                                break;
                        case R.id.linkedin:
                                openLinkedin();//opne browser and show linkedin profile
                                break;
                }
        }
}
