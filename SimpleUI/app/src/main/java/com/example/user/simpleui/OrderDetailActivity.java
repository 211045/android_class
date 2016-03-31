package com.example.user.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailActivity extends AppCompatActivity {

    private String url;
    private String address;

    TextView note;
    TextView storeInfo;
    TextView menu;
    ImageView photo;
    ImageView staticMapImageView;
    WebView webView;
    Switch imageWebViewSwitch;  ////Homework3
    MapFragment mapFragment;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView)findViewById(R.id.noteView);
        storeInfo = (TextView)findViewById(R.id.storeInfoView);
        menu = (TextView)findViewById(R.id.menuView);
        photo = (ImageView)findViewById(R.id.photoView);
        staticMapImageView = (ImageView)findViewById(R.id.staticMapImageView);
        webView = (WebView)findViewById(R.id.webView);
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });

        ////Homework3
        imageWebViewSwitch = (Switch)findViewById(R.id.imageWebViewSwitch);
        imageWebViewSwitch.setChecked(false);  //(Default) set the switch to OFF {State: ON / OFF}

        //(Default) set { "staticMapImageView" : Visible }  , { "webView" : not Visible}
        staticMapImageView.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        ////Homework3

        note.setText(getIntent().getStringExtra("note"));
        //storeInfo.setText(getIntent().getStringExtra("storeInfo"));
        String storeInformation = getIntent().getStringExtra("storeInfo");
        storeInfo.setText(storeInformation);
        String menuResult = getIntent().getStringExtra("menu");

        // 於menuView(TextView)中，顯示訂單中的詳細品項與數量
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
            menu.setText(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 取得該店家的地址  // storeInformation = 店名,地址
        address = storeInformation.split(",")[1];

        // 將該店家地址 --> 透過Google API得到回傳URL資訊 (從 /geometry/location/ {"lat", "lng"} JSON格式字串中，取出經緯度座標)
        // -->  1. 使用  webView.loadUrl(url);    2.將url轉成BitMap呈現於ImageView中
        (new GeoCodingTask()).execute(address);

        //double[] locations = Utils.addressToLatLng(address);
        //String debugLog = "lat: " + String.valueOf(locations[0]) + "/lng: " + String.valueOf(locations[1]);
        //(new ImageLoadingTask(staticMapImageView)).execute(Utils.getStaticMapUrl(locations, 17));

        //Thread顯示圖片
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //byte[] bytes = Utils.urlToBytes(url);
                //String result = new String(bytes);
                //Log.d("debug", result);
                //Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                //photo.setImageBitmap(bmp);

                double[] locations = Utils.addressToLatLng(address);
                String debugLog = "lat: " + String.valueOf(locations[0]) + "/lng: " + String.valueOf(locations[1]);
                Log.d("debug", debugLog);
            }
        });
        thread.start();
        */

        /*
        String url = getIntent().getStringExtra("photoURL");
        if (url != null)
            Picasso.with(this).load(url).into(photo);
        */

        // 秀Camera拍照上傳的圖片 by Image View  // 會藉由使用 Class ( ImageLoadingTask )
        url = getIntent().getStringExtra("photoURL");

        if (url != null)
        {
            (new ImageLoadingTask(photo)).execute(url);
        }

        ////Homework3
        imageWebViewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) //isChecked = false, 只呈現ImageView地圖
                {
                    staticMapImageView.setVisibility(View.VISIBLE);  // 確保ImageView地圖是Visible狀態
                    webView.setVisibility(View.GONE);  //將WebView 地圖設為 not visible
                }
                else //isChecked = true, 只呈現 WebView地圖
                {
                    staticMapImageView.setVisibility(View.GONE);  //將ImageView地圖設為 not visible
                    webView.setVisibility(View.VISIBLE);  // 確保WebView地圖是Visible狀態
                }
            }
        });////Homework3

        //另一種顯示圖片方式
        /*new AsyncTask<String, Void, byte[]>()
        {
            @Override
            protected byte[] doInBackground(String... params)
            {
                String url = params[0];
                return Utils.urlToBytes(url);
            }

            @Override
            protected void onPostExecute(byte[] bytes)
            {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                photo.setImageBitmap(bmp);
                super.onPostExecute(bytes);
            }
        }.execute(url);
        */
    }

    class GeoCodingTask extends AsyncTask<String, Void, byte[]>
    {
        private double[] latlng;
        private String url;

        @Override
        protected byte[] doInBackground(String... params){
            String address = params[0];
            latlng = Utils.addressToLatLng(address);
            url = Utils.getStaticMapUrl(latlng, 17);
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes)
        {
            //載入 WebView地圖
            webView.loadUrl(url);
            //載入 ImageView地圖
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImageView.setImageBitmap(bmp);

            LatLng location = new LatLng(latlng[0], latlng[1]);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

            String[] storeInfos = getIntent().getStringExtra("storeInfo").split(",");
            map.addMarker(new MarkerOptions()
                            .title(storeInfos[0])
                            .snippet(storeInfos[1])
                            .position(location)
            );
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(OrderDetailActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                    return false;
                }
            });

            super.onPostExecute(bytes);
        }
    }

    class ImageLoadingTask extends AsyncTask<String, Void, byte[]>
    {
        ImageView imageView;

        @Override
        protected byte[] doInBackground(String... params){
            String url = params[0];
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes)
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bmp);
            super.onPostExecute(bytes);
        }

        public ImageLoadingTask(ImageView imageView)
        {
            this.imageView = imageView;
        }
    }
}
