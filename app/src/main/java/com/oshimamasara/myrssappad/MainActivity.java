package com.oshimamasara.myrssappad;


import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple activity showing the use of menu items in
 * a {@link RecyclerView} widget.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityのログ：";
    private List<Object> mRecyclerViewItems = new ArrayList<>();
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // グルグルマーク
            //Fragment loadingScreenFragment = new LoadingScreenFragment();
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.add(R.id.fragment_container, loadingScreenFragment);
            //transaction.commit();

            final Button btn = findViewById(R.id.button);

            if (savedInstanceState == null) {

                // XML - JSON, SAVE Json File
                xmljson();

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // JSON データ読み込み、表示
                        addMenuItemsFromJson();
                        loadMenu();

                        btn.setVisibility(View.INVISIBLE); //タップでボタン非表示に
                    }
                });
            }
        }
    }

    public List<Object> getRecyclerViewItems() {
        return mRecyclerViewItems;
    }

    private void loadMenu() {
        Fragment newFragment = new RecyclerViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void addMenuItemsFromJson() {
        try {
            String jsonDataString = readJsonDataFromFile();
            Log.i(TAG,"jsonDtaString:" + jsonDataString);

            JSONObject menuItemsJsonObject = new JSONObject(jsonDataString);

            JSONArray menuItemsJsonArray = menuItemsJsonObject.getJSONArray("items");
            Log.i(TAG,"jsonArray:" + menuItemsJsonArray);

            for (int i = 0; i < menuItemsJsonArray.length(); ++i) {

                JSONObject menuItemObject = menuItemsJsonArray.getJSONObject(i);

                String menuItemName = menuItemObject.getString("title");
                String menuPubDate = menuItemObject.getString("pubDate");
                String menuItemDescription = menuItemObject.getString("description");
                String menuItemLink = menuItemObject.getString("link");

                MenuItem menuItem = new MenuItem(menuItemName, menuPubDate, menuItemDescription,
                        menuItemLink);
                mRecyclerViewItems.add(menuItem);
            }
        } catch (IOException | JSONException exception) {
            Log.e(MainActivity.class.getName(), "Unable to parse JSON file.", exception);
        }
    }


    private String readJsonDataFromFile() throws IOException {

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = openFileInput("FeedData.json");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
                Log.i(TAG,"JSON読み込み："+jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return new String(builder);
    }


    private void xmljson() {
        Ion.with(getApplicationContext()).load("https://api.rss2json.com/v1/api.json?rss_url=https://blog.codecamp.jp/feed.xml").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    //SAVE
                    text = obj.toString();
                    FileOutputStream fileOutputStream = openFileOutput("FeedData.json", MODE_PRIVATE);
                    fileOutputStream.write(text.getBytes());
                    fileOutputStream.close();
                    Toast.makeText(getApplicationContext(), "Data Set OK", Toast.LENGTH_LONG).show();

                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }




}
