package com.bailcompany.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bailcompany.R;
import com.bailcompany.model.Feed;
import com.bailcompany.utils.Const;
import com.bailcompany.web.WebAccess;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Adapter class for the ListView displayed in the left navigation drawer.
 */
@SuppressLint("InflateParams")
public class LeftNavAdapter extends BaseAdapter {

    /**
     * The items.
     */
    private ArrayList<Feed> items;

    /**
     * The context.
     */
    private Context context;

    /**
     * The is first.
     */
    private boolean isFirst;

    /**
     * Instantiates a new left navigation adapter.
     *
     * @param context the context of activity
     * @param items   the array of items to be displayed on ListView
     */
    public LeftNavAdapter(Context context, ArrayList<Feed> items) {
        this.context = context;
        this.items = items;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Feed getItem(int arg0) {
        return items.get(arg0);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.left_nav_item, null);

        Feed f = getItem(position);
        TextView lbl = (TextView) convertView.findViewById(R.id.txt_lbl);
        lbl.setText(f.getTitle());
        lbl.setCompoundDrawablesWithIntrinsicBounds(f.getImageNormal(), 0, 0, 0);
        if (f.getDesc() != null) {
            convertView.setBackgroundColor(context.getResources().getColor(
                    R.color.saperator_yellow));
            lbl.setCompoundDrawablesWithIntrinsicBounds(f.getImageSelected(),
                    0, 0, 0);

        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        if (f.getTitle().equalsIgnoreCase(Const.Menu.INCOMING_REFFER_BAIL)) {
            if (WebAccess.referBailBadge) {
                ImageView badges = (ImageView) convertView
                        .findViewById(R.id.badge_new);
                badges.setVisibility(View.VISIBLE);
            }
        } else if (f.getTitle().equalsIgnoreCase(
                Const.Menu.INCOMING_TRANSFER_BOND_REQUEST)) {
            if (WebAccess.tranferBondBadge) {
                ImageView badges = (ImageView) convertView
                        .findViewById(R.id.badge_new);
                badges.setVisibility(View.VISIBLE);
            }
        } else if (f.getTitle().equalsIgnoreCase(Const.Menu.SENT_FUGITIVE_REQUEST)) {
            if (WebAccess.fugitiveBadge) {
                ImageView badges = (ImageView) convertView
                        .findViewById(R.id.badge_new);
                badges.setVisibility(View.VISIBLE);
            }
        } else if (f.getTitle().equalsIgnoreCase(Const.Menu.INSTANT_CHAT)) {
            if (WebAccess.instant) {
                ImageView badges = (ImageView) convertView
                        .findViewById(R.id.badge_new);
                badges.setVisibility(View.VISIBLE);
            }
        } else if (f.getTitle().equalsIgnoreCase(Const.Menu.INSTANT_GROUP_CHAT)) {
            if (WebAccess.instantGroup) {
                ImageView badges = (ImageView) convertView
                        .findViewById(R.id.badge_new);
                badges.setVisibility(View.VISIBLE);
            }
        } else {
            ImageView badges = (ImageView) convertView
                    .findViewById(R.id.badge_new);
            badges.setVisibility(View.GONE);
        }
        return convertView;
    }

}
