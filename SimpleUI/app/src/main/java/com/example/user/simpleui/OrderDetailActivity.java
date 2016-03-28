package com.example.user.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

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

        note.setText(getIntent().getStringExtra("note"));
        //storeInfo.setText(getIntent().getStringExtra("storeInfo"));
        String storeInformation = getIntent().getStringExtra("storeInfo");
        storeInfo.setText(storeInformation);
        String menuResult = getIntent().getStringExtra("menu");

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

        address = storeInformation.split(",")[1];

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

        url = getIntent().getStringExtra("photoURL");

        if (url != null)
        {
            (new ImageLoadingTask(photo)).execute(url);
        }


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
            double[] locations = Utils.addressToLatLng(address);
            url = Utils.getStaticMapUrl(locations, 17);
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes)
        {
            webView.loadUrl(url);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImageView.setImageBitmap(bmp);
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
