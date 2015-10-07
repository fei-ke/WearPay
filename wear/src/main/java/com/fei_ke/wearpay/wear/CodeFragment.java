package com.fei_ke.wearpay.wear;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by fei-ke on 2015/10/7.
 */
public class CodeFragment extends Fragment {
    protected ImageView imageView;
    protected ImageView imageViewIcon;
    protected ProgressBar progressBar;

    public static BarcodeFragment newBarcodeFragment(int iconRes) {

        BarcodeFragment fragment = new BarcodeFragment();
        Bundle args = new Bundle();
        args.putInt("ICON_RES", iconRes);
        fragment.setArguments(args);
        return fragment;
    }

    public static QRCodeFragment newQRCodeFragment(int iconRes) {
        QRCodeFragment fragment = new QRCodeFragment();
        Bundle args = new Bundle();
        args.putInt("ICON_RES", iconRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imageView = (ImageView) view.findViewById(R.id.imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        imageViewIcon = (ImageView) view.findViewById(R.id.imageViewIcon);
        Bundle args = getArguments();
        int icon_res = args.getInt("ICON_RES");
        imageViewIcon.setImageResource(icon_res);
    }

    public void setCodeImage(Bitmap codeImage) {
        if (!isResumed()) return;
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        imageView.setImageDrawable(new BitmapDrawable(getResources(), codeImage));
    }
}
