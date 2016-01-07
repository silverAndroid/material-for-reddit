package com.reddit.material;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Rushil Perera on 1/6/2016.
 */
public class ImageGalleryAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Image> imageURLs = new ArrayList<>();

    public ImageGalleryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ImageGalleryFragment.newInstance(imageURLs.get(position));
    }

    @Override
    public int getCount() {
        return imageURLs.size();
    }

    public void addAll(ArrayList<Image> imageURLs) {
        this.imageURLs.addAll(imageURLs);
    }
}
