package ru.android_school.h_h.themostspb.InfoPage.InfoActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ru.android_school.h_h.themostspb.R;

public class ImageFragment extends Fragment {

    private String URL;

    public static ImageFragment newInstance(String URL) {
        ImageFragment imageFragment = new ImageFragment();
        imageFragment.URL = URL;
        return imageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView = new ImageView(getContext());
        Glide.with(this)
                .load(getString(R.string.URL_host)+URL)
                .into(imageView);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }
}
