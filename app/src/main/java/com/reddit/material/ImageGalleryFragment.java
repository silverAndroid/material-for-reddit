package com.reddit.material;


import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.reddit.material.libraries.facebook.ZoomableDraweeView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageGalleryFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "ImageGalleryFragment";

    private Image image;
    private ZoomableDraweeView imageView;
    private ProgressBar loading;


    public ImageGalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param image Image object.
     * @return A new instance of fragment ImageGalleryFragment.
     */
    public static ImageGalleryFragment newInstance(Image image) {
        ImageGalleryFragment fragment = new ImageGalleryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image = (Image) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_gallery, container, false);
        imageView = (ZoomableDraweeView) view.findViewById(R.id.image);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        imageView.setHierarchy(hierarchy);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        loadImage(image.getLowResURL(), image.getUrl());
        return view;
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
