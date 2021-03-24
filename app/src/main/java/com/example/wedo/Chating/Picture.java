package com.example.wedo.Chating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wedo.R;

public class Picture extends AppCompatActivity {

    public String id, nick, profilePath, userEmail, userID, userPass, orderNick, TitleProfile, people, picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");    //주제
        nick = extras.getString("nick");
        profilePath = extras.getString("profilePath");  //프로필
        orderNick = extras.getString("orderNick");
        userEmail = extras.getString("userEmail");
        userID = extras.getString("userID");
        userPass = extras.getString("userPass");
        TitleProfile = extras.getString("TitleProfile");
        people = extras.getString("people");
        picture = extras.getString("picture");
        System.out.println("사진사진왜안떠!!: "+picture);

        imgPicture();
    }

    public void imgPicture(){
        ImageView imgPicture = (ImageView) findViewById(R.id.img_picture);
        Uri uri = Uri.parse(picture);
        Glide.with(getApplicationContext())
                .load(uri)
                .into(imgPicture);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), InviteesChating.class);
        intent.putExtra("id", id);
        intent.putExtra("nick", nick);
        intent.putExtra("orderNick", orderNick);
        intent.putExtra("profilePath", profilePath);
        intent.putExtra("userID", userID);
        intent.putExtra("userPass", userPass);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("TitleProfile", TitleProfile);
        intent.putExtra("people", people);
        startActivity(intent);
        finish();
    }
}