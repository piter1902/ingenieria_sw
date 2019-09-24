package com.example.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.firstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        android.util.Log.d("TAG", message);
        startActivity(intent);
    }

    public void goToRadio(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        switch(view.getId()){
            case R.id.button1:
                intent.putExtra(EXTRA_MESSAGE, ((RadioButton)view).getText().toString());
                break;
            case R.id.button2:
                intent.putExtra(EXTRA_MESSAGE, ((RadioButton)view).getText().toString());
                break;
            case R.id.button3:
                intent.putExtra(EXTRA_MESSAGE, ((RadioButton)view).getText().toString());
                break;
        }
        //android.util.Log.d("TAG", message);
        startActivity(intent);
    }
}
