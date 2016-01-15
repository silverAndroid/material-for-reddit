package com.reddit.material;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.reddit.material.libraries.facebook.ZoomableDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ImageActivity extends AppCompatActivity {

    private static final String ARG_URL = "image_url";
    private static final String TAG = "ImageActivity";
    private ZoomableDraweeView imageView;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = getIntent().getStringExtra("url");
        imageView = (ZoomableDraweeView) findViewById(R.id.image);
        loading = (ProgressBar) findViewById(R.id.loading);

        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        imageView.setHierarchy(hierarchy);

        modifyImageURL(url);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

    private void modifyImageURL(String url) {
        final URL modifiedURL = new URL(url);

        boolean modifyURL = true;
        if (ConstantMap.getInstance().hasImageEndings(url))
            modifyURL = false;
        if (modifyURL) {
            if (url.matches("https?://www\\.flickr.*")) {
                AsyncHttpClient imageURLModifier = new AsyncHttpClient();
                imageURLModifier.setUserAgent(ConstantMap.getInstance().getUserAgent());
                RequestParams params = new RequestParams();
                params.put("method", "flickr.photos.search");
                params.put("format", "json");
                params.put("api_key", APIKey.getInstance().getAPIKey(APIKey.FLICKR_KEY));
                params.put("user_id", url.split("/")[4]);
                params.put("nojsoncallback", 1);
                imageURLModifier.get("https://www.flickr.com/services/rest", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String modifiedUrl = modifiedURL.getUrl();
                            String lowResURL = "";
                            JSONArray photosArray = response.getJSONObject("photos").getJSONArray("photo");
                            for (int i = 0; i < photosArray.length(); i++) {
                                JSONObject urlJSON = photosArray.getJSONObject(i);
                                if (urlJSON.getLong("id") == Long.parseLong(modifiedUrl.split("/")[5].trim())) {
                                    long farmID = urlJSON.getLong("farm");
                                    long serverID = urlJSON.getLong("server");
                                    long id = urlJSON.getLong("id");
                                    String secretID = urlJSON.getString("secret");
                                    lowResURL = String.format("https://farm%d.staticflickr.com/%d/%d_%s_n.jpg", farmID,
                                            serverID, id, secretID);
                                    modifiedUrl = String.format("https://farm%d.staticflickr.com/%d/%d_%s.jpg", farmID,
                                            serverID, id, secretID);
                                    break;
                                }
                            }
                            loadImage(lowResURL, modifiedUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        Log.e(TAG, "onFailure: String: " + this.getRequestURI(), throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        Log.e(TAG, "onFailure: JSONObject: " + this.getRequestURI(), throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray
                            errorResponse) {
                        Log.e(TAG, "onFailure: JSONArray: " + this.getRequestURI(), throwable);
                    }
                });
            } else if (url.matches("https?://(m\\.)?imgur.*")) {
                AsyncHttpClient imageClient = new AsyncHttpClient();
                imageClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
                imageClient.addHeader("Authorization", "Client-ID " + APIKey.getInstance().getAPIKey(APIKey
                        .IMGUR_CLIENT_ID_KEY));
                Log.d(TAG, "loadImage: " + url);
                imageClient.get("https://api.imgur.com/3/image/" + url.split("/")[3], new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String url = response.getJSONObject("data").getString("link");
                            String[] linkArray = url.split("/");
                            String[] lowResArray = linkArray[3].split("\\.");
                            String lowResURL = "https://i.imgur.com/" + lowResArray[0] + "t." + lowResArray[1];
                            loadImage(lowResURL, url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, "onFailure: URL: " + modifiedURL.getUrl(), throwable);
                    }
                });
            }
        } else {
            if (url.matches("https?://.*\\.imgur.*")) {
                String[] linkArray = url.split("/");
                String[] lowResArray = linkArray[3].split("\\.");
                String lowResUrl = "https://i.imgur.com/" + lowResArray[0] + "t." + lowResArray[1];
                loadImage(lowResUrl, url);
            } else
                loadImage(null, url);
        }
    }

    private void loadImage(String lowResURL, final String url) {

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                Log.e(TAG, "onFailure: Image URL: " + url, throwable);
                loading.setVisibility(View.GONE);
            }
        };

        ImageRequest lowResRequest = null;
        if (lowResURL != null)
            lowResRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(lowResURL))
                    .setResizeOptions(new ResizeOptions(2560, 2560))
                    .setProgressiveRenderingEnabled(true)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                    .setAutoRotateEnabled(true)
                    .build();

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(2560, 2560))
                .setProgressiveRenderingEnabled(true)
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setAutoRotateEnabled(true)
                .build();

        PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setControllerListener(listener);

        if (lowResRequest != null)
            controllerBuilder.setLowResImageRequest(lowResRequest);

        DraweeController controller = controllerBuilder.build();
        imageView.setController(controller);
    }
}
