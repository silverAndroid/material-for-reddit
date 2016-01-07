package com.reddit.material;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.reddit.material.libraries.facebook.ZoomableDraweeView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageGalleryFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private Image image;


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
        ZoomableDraweeView draweeView = (ZoomableDraweeView) view.findViewById(R.id.image);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        draweeView.setHierarchy(hierarchy);
        ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);
        ConnectionSingleton.getInstance().loadImage(image.getLowResURL(), image.getUrl(), draweeView, loading);
        return view;
    }
}
