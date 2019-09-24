package com.piter.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static final String EXTRA_MESSAGE = "com.piter.myfirstapp.MESSAGE";

    public void sendMessage(View v) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText et = (EditText) findViewById(R.id.edit_message);
        String message = et.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        android.util.Log.d("TAG", message);
        startActivity(intent);
    }
}
