package com.fiek.hitchhikerkosova.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;

    public MyClusterItem(LatLng mPosition, String mTitle, String mSnippet) {
        this.mPosition = mPosition;
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
    }



    @NonNull
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
