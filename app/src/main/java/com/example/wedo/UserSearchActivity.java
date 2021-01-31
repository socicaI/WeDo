package com.example.wedo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

/**
 * 초대할 사용자를 검색으로 해당 사용자를 보여주는 클래스
 */
public class UserSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        EditText editTextTextPersonName = (EditText)findViewById(R.id.editTextTextPersonName);
        editTextTextPersonName.clearFocus();
    }
}