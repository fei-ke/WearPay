package com.fei_ke.wearpay.wear;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by fei-ke on 2015/9/28.
 */
public class QRCodeFragment extends Fragment {
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        imageView = new ImageView(getActivity());
        imageView.setImageResource(R.mipmap.ic_launcher);
        return imageView;
    }

    public void setCodeImage(Bitmap codeImage) {
        imageView.setImageBitmap(codeImage);
    }
}
