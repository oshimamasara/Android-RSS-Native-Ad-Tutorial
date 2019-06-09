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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
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

    public static final int NUMBER_OF_ADS = 1;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn = findViewById(R.id.button);

        if (savedInstanceState == null) {
            // グルグルマーク
            //Fragment loadingScreenFragment = new LoadingScreenFragment();
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.add(R.id.fragment_container, loadingScreenFragment);
            //transaction.commit();

                // XML - JSON, SAVE Json File
                xmljson();

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // JSON データ読み込み、表示
                        addMenuItemsFromJson();
                        //loadMenu();
                        loadMenu();
                        //load Ad
                        loadNativeAds();

                        btn.setVisibility(View.INVISIBLE); //タップでボタン非表示に
                    }
                });

                // AdMob
                MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        }
    }



    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/8135179316");
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        // 広告表示
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }


    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (mRecyclerViewItems.size() / mNativeAds.size()) + 1;
        int index = 6;
        for (UnifiedNativeAd ad: mNativeAds) {
            mRecyclerViewItems.add(index, ad);
            index = index + offset;
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

















