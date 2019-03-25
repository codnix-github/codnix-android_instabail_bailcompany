package com.bailcompany.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bailcompany.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;


/**
 * Created by admin on 11/27/2017.
 */

public class ImageZoomDialog extends Dialog implements View.OnClickListener {

    Activity mActivity;
    Context context;
    LinearLayout linearLayout;
    ImageView imageViewDailog;
    String imageurl;
    private String title;


    public ImageZoomDialog(Context activity, String url) {
        super(activity);
        context = activity;
        imageurl = url;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dailog_zoom_image);

        imageViewDailog = (ImageView) findViewById(R.id.imageviewDailog);
        TextView tvTitle= (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(getTitle());
        Glide.with(context)
                .load(imageurl)
                .asBitmap()
                .placeholder(R.drawable.default_profile_image)
                .error(R.drawable.default_profile_image)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super
                            Bitmap> glideAnimation) {
                        imageViewDailog.setImageBitmap(resource);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        dismiss();

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}