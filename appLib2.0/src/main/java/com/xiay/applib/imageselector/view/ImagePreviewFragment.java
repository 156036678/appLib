package com.xiay.applib.imageselector.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xiay.applib.R;
import com.xiay.applib.imageselector.ImageSelectorConfig;
import com.xiay.applib.imageselector.uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;

import cn.xiay.bean.HttpConfig;

/**
 * Created by dee on 15/11/25.
 */
public class ImagePreviewFragment extends Fragment {
    public static final String PATH = "path";

    public static ImagePreviewFragment getInstance(String path) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.image_selector_fragment_image_preview, container, false);
        final ImageView imageView = (ImageView) contentView.findViewById(R.id.preview_image);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
        String path=getArguments().getString(PATH);
        RequestManager manager= Glide.with(container.getContext());
        DrawableTypeRequest request;
        if (path.startsWith(ImageSelectorConfig.serverPathStartWith)){
            request= manager.load(HttpConfig.UrlHead+path);

        }else {
            request= manager.load(new File(path));

        }
        request.asBitmap()
                .into(new SimpleTarget<Bitmap>(480, 800) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        mAttacher.update();
                    }
                });
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (getActivity() instanceof ImagePreviewActivity ){
                    ImagePreviewActivity activity = (ImagePreviewActivity) getActivity();
                    activity.switchBarVisibility();

                }
            }
        });
        return contentView;
    }
}
