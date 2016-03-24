package com.example.user.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 0; //定義固定(final)常數，變數全大寫
    private static final int REQUEST_CODE_CAMERA = 1;

    TextView textView;
    EditText editText;
    CheckBox hideCheckBox;

    SharedPreferences sp;  //類似一張白紙
    SharedPreferences.Editor editor;  //類似一隻筆

    ListView listView;  //清單
    Spinner spinner;  //下拉式選單

    ImageView photoView;

    String menuResult;

    List<ParseObject> queryResults;

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

        photoView = (ImageView)findViewById(R.id.imageView);

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

/*        Parse.enableLocalDatastore(this);

        Parse.initialize(this);

        ParseObject testObject = new ParseObject("HomeworkParse");
        testObject.put("sid", "And26306");  //HomeworkParse
        testObject.put("email", "u211045@taipower.com.tw");  //HomeworkParse
        //testObject.saveInBackground();
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                {
                    Toast.makeText(MainActivity.this, "onCreate OK", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "onCreate Fail", Toast.LENGTH_LONG).show();
                }
            }
        });
*/    }

    private void setListView()
    {
        //String[] data = {"1","2","3","4","5"};
        /*
        String[] data = Utils.readFile(this, "history.txt").split("\n");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        */

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    // Log.d("Debug", "setListView");
                    return;
                }
                // Log.d("Debug", "setListView");

                queryResults = list;

                //Homework2
                String totalJSONObjectToString = "";  // raw data
                //String recordListParseObjectString = "";  // 解析後的data
                //

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();

                for (int i = 0; i < queryResults.size(); i++)
                {
                    ParseObject object = queryResults.get(i);
                    String note = object.getString("note");
                    String storeInfo = object.getString("storeInfo");
                    String menu = object.getString("menu");

                    //Homework2
                    //String oneOrderInfo = "";
                    String oneOrderSum_String = ""; // 每筆飲料訂單的總杯數 初始值 0
                    int oneOrderSum_Int = 0;
                    //totalJSONObjectToString = totalJSONObjectToString + "\n" + menu;
                    totalJSONObjectToString = menu;

                    try {
                        JSONArray oneStoreOrderJSONArray = new JSONArray(menu);
                        for (int j = 0; j < oneStoreOrderJSONArray.length(); j++)
                        {
                            JSONObject order = oneStoreOrderJSONArray.getJSONObject(j);

                            // "Key" defined from JSONObject from Parse Server's Data : "name" & "l" & "m"
                            /*String name = order.getString("name");
                            String l = String.valueOf(order.getInt("l"));
                            String m = String.valueOf(order.getInt("m"));
                            recordListParseObjectString = recordListParseObjectString + name + "\t\t" + "l:" + l + "\t\t" + "m:" + m + "\n"; //記錄所有訂單內容
                            oneOrderInfo = oneOrderInfo + name + "\t\t" + "l:" + l + "\t\t" + "m:" + m + "\n"; //顯示一張訂單內容
                            */

                            oneOrderSum_Int += order.getInt("l") + order.getInt("m");
                            // 判斷 飲料訂單的數量為空值的情況
                            /*if(order.isNull("l"))
                                oneOrderSum_Int = oneOrderSum_Int;
                            else
                                oneOrderSum_Int = oneOrderSum_Int + order.getInt("l");

                            if(order.isNull("m"))
                                oneOrderSum_Int = oneOrderSum_Int;
                            else
                                oneOrderSum_Int = oneOrderSum_Int + order.getInt("m");*/
                        }
                        oneOrderSum_String = String.valueOf(oneOrderSum_Int);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    try {
                        JSONArray oneStoreOrderJSONArray = new JSONArray(menu);
                        for (int j = 0; j < oneStoreOrderJSONArray.length(); j++)
                        {
                            JSONObject order = oneStoreOrderJSONArray.getJSONObject(j);

                            // "Key" defined from JSONObject from Parse Server's Data : "name" & "lNumber" & "mNumber"
                            /*String name = order.getString("name");
                            String lNumber = String.valueOf(order.getInt("lNumber"));
                            String mNumber = String.valueOf(order.getInt("mNumber"));
                            recordListParseObjectString = recordListParseObjectString + name + "\t\t" + "lNumber:" + lNumber + "\t\t" + "mNumber:" + mNumber + "\n"; //記錄所有訂單內容
                            oneOrderInfo = oneOrderInfo + name + "\t\t" + "lNumber:" + lNumber + "\t\t" + "mNumber:" + mNumber + "\n"; //顯示一張訂單內容
                            */

                            oneOrderSum_Int += order.getInt("lNumber") + order.getInt("mNumber");
                            // 判斷 飲料訂單的數量為空值的情況
                            /*if(order.isNull("lNumber"))
                                oneOrderSum_Int = oneOrderSum_Int;
                            else
                                oneOrderSum_Int = oneOrderSum_Int + order.getInt("lNumber");

                            if(order.isNull("mNumber"))
                                oneOrderSum_Int = oneOrderSum_Int;
                            else
                                oneOrderSum_Int = oneOrderSum_Int + order.getInt("mNumber");*/
                        }
                        oneOrderSum_String = String.valueOf(oneOrderSum_Int);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    //

                    Map<String, String> item = new HashMap<String, String>();

                    item.put("note", note);
                    item.put("storeInfo", storeInfo);
                    //item.put("drinkNum", "15");

                    // Homework2
                    //item.put("drinkNum", totalJSONObjectToString);
                    item.put("drinkNum", oneOrderSum_String);
                    //

                    data.add(item);
                }

                String[] from = {"note", "storeInfo", "drinkNum"};
                int[] to = {R.id.note, R.id.storeInfo, R.id.drinkNum};

                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.listview_item, from, to);

                listView.setAdapter(adapter);
            }
        });
    }

    private void setSpinner()
    {
        //String[] data = {"1","2","3","4","5"};
        /*
        String[] data = getResources().getStringArray(R.array.storeInfo);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);
        */
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                String[] stores = new String[list.size()];
                for (int i = 0; i < list.size(); i++)
                {
                    ParseObject object = list.get(i);
                    stores[i] = object.getString("name") + "," + object.getString("address");

                    ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, stores);
                    spinner.setAdapter(storeAdapter);
                }
            }
        });
    }

    public void submit(View view)
    {
        //Toast.makeText(this, "Hello world", Toast.LENGTH_LONG).show();
        String text = editText.getText().toString();

/*
        ParseObject orderObject = new ParseObject("Order");
        orderObject.put("note", text);
        orderObject.put("storeInfo", spinner.getSelectedItem());
        orderObject.put("menu", menuResult);

        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                {
                    Toast.makeText(MainActivity.this, "Submit OK", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Submit Fail", Toast.LENGTH_LONG).show();
                }
            }
        });
*/
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
                //textView.setText(data.getStringExtra("result"));
                menuResult = data.getStringExtra("result");

                try {
                    JSONArray array = new JSONArray(menuResult);

                    String text = "";

                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject order = array.getJSONObject(i);

                        String name = order.getString("name");
                        String lNumber = String.valueOf(order.getInt("lNumber"));
                        String mNumber = String.valueOf(order.getString("mNumber"));

                        text = text + name + "lNumber:" + lNumber + "mNumber:" + mNumber + "\n";
                    }
                    textView.setText(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == REQUEST_CODE_CAMERA)
        {
            if (resultCode == RESULT_OK)
            {
                photoView.setImageURI(Utils.getPhotoUri());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_take_photo)
        {
            Toast.makeText(this, "take photo", Toast.LENGTH_LONG).show();
            goToCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToCamera()
    {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoUri());

        //startActivity(intent);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }
}
