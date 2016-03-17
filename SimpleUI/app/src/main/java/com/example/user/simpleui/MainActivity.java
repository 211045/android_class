package com.example.user.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 0; //定義固定(final)常數，變數全大寫

    TextView textView;
    EditText editText;
    CheckBox hideCheckBox;

    SharedPreferences sp;  //類似一張白紙
    SharedPreferences.Editor editor;  //類似一隻筆

    ListView listView;  //清單
    Spinner spinner;  //下拉式選單

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //抓欄位的值，要強制轉型
        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        hideCheckBox = (CheckBox)findViewById(R.id.checkBox);

        listView = (ListView)findViewById(R.id.listView);
        spinner = (Spinner)findViewById(R.id.spinner);

        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);  //指定紙叫setting
        editor = sp.edit();

        editText.setText(sp.getString("editText", ""));  //取得紙上editText的設定，一開始預設為空白字串

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                editor.putString("editText", editText.getText().toString());  //儲存editText的文字到sp
                editor.apply();

                //偵測按下鍵盤上的ENTER ??，然後submit
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    submit(v);
                    return true;
                }
                return false;
            }
        });

        //虛擬鍵盤??
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit(v);
                    return true;
                }
                return false;
            }
        });

        hideCheckBox.setChecked(sp.getBoolean("hideCheckBox", false));  //取得紙上hideCheckBox的設定，一開始預設為不勾選

        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", hideCheckBox.isChecked());  //儲存hideCheckBox的勾選狀態到sp
                editor.apply();
            }
        });

        setListView();
        setSpinner();
    }

    private void setListView()
    {
        //String[] data = {"1","2","3","4","5"};
        String[] data = Utils.readFile(this, "history.txt").split("\n");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    private void setSpinner()
    {
        //String[] data = {"1","2","3","4","5"};
        String[] data = getResources().getStringArray(R.array.storeInfo);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);
    }

    public void submit(View view)
    {
        //Toast.makeText(this, "Hello world", Toast.LENGTH_LONG).show();
        String text = editText.getText().toString();

        Utils.writeFile(this, "history.txt", text + '\n');

        if (hideCheckBox.isChecked())
        {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            textView.setText("********");
            editText.setText("********");
            return;
        }
        textView.setText(text);
        editText.setText("");

        setListView();
    }

    public void goToMenu(View view)
    {
        //不同的Layout用Intent來溝通
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);  //使用intent從MainActivity(this)呼叫DrinkMenuActivity

        //startActivity(intent);
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MENU_ACTIVITY)
        {
            if (resultCode == RESULT_OK)
            {
                textView.setText(data.getStringExtra("result"));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Debug", "Main Menu onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Debug", "Main Menu onPause");
    }
}
