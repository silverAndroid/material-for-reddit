package com.reddit.material;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.reddit.material.libraries.PageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ImageGalleryActivity extends AppCompatActivity {

    private static final String TAG = "ImageGallery";
    private static ImageGalleryAdapter adapter;
    private static ViewPager pager;
    private static PageIndicator indicator;

    public static ImageGalleryAdapter getAdapter() {
        return adapter;
    }

    public static ViewPager getPager() {
        return pager;
    }

    public static PageIndicator getIndicator() {
        return indicator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new ImageGalleryAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicator = (PageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        loadAlbum(getIntent().getStringExtra("albumURL"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadAlbum(final String url) {
        String[] urlArray = url.split("/");
        String apiURL = urlArray[3].equals("gallery") ? "https://api.imgur.com/3/gallery/album/" + urlArray[4] :
                "https://api.imgur.com/3/album/" + urlArray[4];
        final ArrayList<Image> images = new ArrayList<>();
        AsyncHttpClient imageAlbumClient = new AsyncHttpClient();
        imageAlbumClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        imageAlbumClient.addHeader("Authorization", "Client-ID " + APIKey.getInstance().getAPIKey(APIKey
                .IMGUR_CLIENT_ID_KEY));
        imageAlbumClient.get(getBaseContext(), apiURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray imagesJSON = response.getJSONObject("data").getJSONArray("images");
                    for (int i = 0; i < imagesJSON.length(); i++) {
                        JSONObject imageJSON = imagesJSON.getJSONObject(i);
                        Image image = new Image(imageJSON.getString("title"), imageJSON.getString("link"), imageJSON
                                .getInt("width"), imageJSON.getInt("height"));
                        images.add(image);
                    }
                    ImageGalleryAdapter adapter;
                    ViewPager pager;
                    (adapter = ImageGalleryActivity.getAdapter()).addAll(images);
                    (pager = ImageGalleryActivity.getPager()).setAdapter(adapter);
                    ImageGalleryActivity.getIndicator().setViewPager(pager);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AsyncHttpClient imageClient = new AsyncHttpClient();
                imageClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
                imageClient.addHeader("Authorization", "Client-ID " + APIKey.getInstance().getAPIKey(APIKey
                        .IMGUR_CLIENT_ID_KEY));
                imageClient.get("https://api.imgur.com/3/image/" + url.split("/")[4], new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject imageJSON = response.getJSONObject("data");
                            Image image = Util.generateImage(imageJSON);
                            images.add(image);

                            ImageGalleryAdapter adapter;
                            ViewPager pager;
                            (adapter = ImageGalleryActivity.getAdapter()).addAll(images);
                            (pager = ImageGalleryActivity.getPager()).setAdapter(adapter);
                            ImageGalleryActivity.getIndicator().setViewPager(pager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d(TAG, "onFailure: String " + url);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                AsyncHttpClient imageClient = new AsyncHttpClient();
                imageClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
                imageClient.addHeader("Authorization", "Client-ID " + APIKey.getInstance().getAPIKey(APIKey
                        .IMGUR_CLIENT_ID_KEY));
                imageClient.get("https://api.imgur.com/3/image/" + url.split("/")[4], new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject imageJSON = response.getJSONObject("data");
                            Image image = Util.generateImage(imageJSON);
                            images.add(image);

                            ImageGalleryAdapter adapter;
                            ViewPager pager;
                            (adapter = ImageGalleryActivity.getAdapter()).addAll(images);
                            (pager = ImageGalleryActivity.getPager()).setAdapter(adapter);
                            ImageGalleryActivity.getIndicator().setViewPager(pager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d(TAG, "onFailure: JSONObject " + url);
                    }
                });
            }
        });
    }
}
