package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DrinkMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
    }

    public void add(View view)
    {
        Button button = (Button) view;  //按下的view就是button ??
        int number = Integer.parseInt(button.getText().toString());  //取得button上的字串，並轉為數字
        number++;
        button.setText(String.valueOf(number));  //數字轉成String
    }

    public void cancel(View view)
    {
        //Toast.makeText(this, "Bye", Toast.LENGTH_LONG).show();
        finish();
    }
}
