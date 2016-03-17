package com.example.user.simpleui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public void done(View view)
    {
        JSONArray array = getData();

        Intent data = new Intent();
        //data.putExtra("result","order done");
        data.putExtra("result", array.toString());
        setResult(RESULT_OK, data);
        finish();
    }

    public JSONArray getData()
    {
        LinearLayout rootLinearLayout = (LinearLayout)findViewById(R.id.root);
        int count = rootLinearLayout.getChildCount();

        JSONArray array = new JSONArray();

        for (int i = 0; i < count -1; i++)
        {
            LinearLayout ll = (LinearLayout)rootLinearLayout.getChildAt(i);  //取得LinearLayout(horizontal)
            TextView drinkNameTextView = (TextView)ll.getChildAt(0);
            Button lButton = (Button)ll.getChildAt(1);
            Button mButton = (Button)ll.getChildAt(2);

            String drinkName = drinkNameTextView.getText().toString();
            int lNumber = Integer.parseInt(lButton.getText().toString());
            int mNumber = Integer.parseInt(mButton.getText().toString());

            try {
                JSONObject object = new JSONObject();

                object.put("name", drinkName);  //JSON Object的key:value
                object.put("lNumber", lNumber);
                object.put("mNumber", mNumber);

                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public void cancel(View view)
    {
        //Toast.makeText(this, "Bye", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Debug", "Drink Menu onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Debug", "Drink Menu onPause");
    }
}
