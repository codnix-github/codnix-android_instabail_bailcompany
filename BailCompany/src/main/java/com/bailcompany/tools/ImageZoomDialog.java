package com.bailcompany.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bailcompany.R;
import com.bumptech.glide.Glide;


/**
 * Created by admin on 11/27/2017.
 */

public class ImageZoomDialog extends Dialog implements View.OnClickListener {

    Activity mActivity;
    Context context;
    LinearLayout linearLayout;
    ImageView imageViewDailog;
    String imageurl;


    public ImageZoomDialog(Context activity,String url){
        super(activity);
        context=activity;
        imageurl=url;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailog_zoom_image);


        imageViewDailog = (ImageView)findViewById(R.id.imageviewDailog);

        Glide.with(context)
             .load(imageurl)
             .placeholder(R.drawable.ic_action_name)
             .into(imageViewDailog);
    }
    @Override
    public void onClick(View v) {
        dismiss();

    }

}