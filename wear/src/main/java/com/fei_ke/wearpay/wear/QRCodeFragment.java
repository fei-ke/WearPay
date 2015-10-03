package com.fei_ke.wearpay.wear;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by fei-ke on 2015/9/28.
 */
public class QRCodeFragment extends Fragment {
    ImageView imageView;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_code, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    public void setCodeImage(Bitmap codeImage) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        imageView.setImageBitmap(codeImage);
    }
}
