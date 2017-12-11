package com.bailcompany.adapter;

/**
 * Created by admin on 12/8/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bailcompany.R;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.tools.ImageZoomDialog;
import com.bailcompany.ui.Defendant;
import com.bailcompany.utils.Const;
import com.bailcompany.web.WebAccess;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class DefendantListAdapter extends RecyclerView.Adapter<DefendantListAdapter.ViewHolder> implements Filterable {
    private ArrayList<DefendantModel> mArrayList;
    private ArrayList<DefendantModel> mFilteredList;
    private Context mContex;
    private boolean mReturn;

    public DefendantListAdapter(ArrayList<DefendantModel> arrayList, Context context, boolean returnresult) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
        mContex = context;
        mReturn = returnresult;
    }

    @Override
    public DefendantListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_defendant_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DefendantListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.id = mFilteredList.get(i).getId();
        viewHolder.tvName.setText(mFilteredList.get(i).getFirstName() + " " + mFilteredList.get(i).getLastName());
        if (mFilteredList.get(i).getDOB() != null && !mFilteredList.get(i).getDOB().trim().equalsIgnoreCase(""))
            viewHolder.tvBirthdate.setText("DOB: " + mFilteredList.get(i).getDOB());
        if (mFilteredList.get(i).getSSN() != null && !mFilteredList.get(i).getSSN().trim().equalsIgnoreCase(""))
            viewHolder.tvSSN.setText("SSN: " + mFilteredList.get(i).getSSN());
        //Log.d("Path=", WebAccess.PHOTO + mFilteredList.get(i).getPhoto());
        final String url = WebAccess.PHOTO + mFilteredList.get(i).getPhoto();

        Glide.with(mContex)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super
                            Bitmap> glideAnimation) {

                        viewHolder.ivProfilePic.setImageBitmap(resource);
                    }
                });

        viewHolder.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageZoomDialog addToQueueDialog = new ImageZoomDialog(mContex, url);
                addToQueueDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addToQueueDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                addToQueueDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addToQueueDialog.show();

            }
        });
        //  Glide.with(mContex).load(url).placeholder(R.drawable.ic_side_menu_normal).error(R.drawable.ic_action_name).into(viewHolder.ivProfilePic);
        viewHolder.rlDefendantList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReturn) {
                    Intent intent = new Intent();
                    intent.putExtra(Const.RETURN_FLAG, Const.RETURN_DEFENDANT_DETAIL);

                    intent.putExtra(Const.EXTRA_DATA, getDefendant(viewHolder.id));

                    ((Activity) mContex).setResult(RESULT_OK, intent);
                    ((Activity) mContex).finish();
                } else {
                    Intent intent = new Intent(mContex, Defendant.class);
                    intent.putExtra("defId", viewHolder.id);
                    mContex.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<DefendantModel> filteredList = new ArrayList<>();

                    for (DefendantModel androidVersion : mArrayList) {
                        if (androidVersion.getFirstName().toLowerCase().contains(charString) || androidVersion.getLastName().toLowerCase().contains(charString) || androidVersion.getSSN().toLowerCase().contains(charString) || androidVersion.getDOB().toLowerCase().contains(charString)) {
                            filteredList.add(androidVersion);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<DefendantModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private String id;
        private TextView tvName, tvBirthdate, tvSSN;
        private CircleImageView ivProfilePic;
        private RelativeLayout rlDefendantList;

        public ViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tvName);
            tvSSN = (TextView) view.findViewById(R.id.tvSSN);
            tvBirthdate = (TextView) view.findViewById(R.id.tvBirthdate);
            ivProfilePic = (CircleImageView) view.findViewById(R.id.ivProfilePic);
            rlDefendantList = (RelativeLayout) view.findViewById(R.id.rlDefendantList);
            id = "";

        }
    }
    private DefendantModel getDefendant(String id){
        for (DefendantModel androidVersion : mArrayList) {
            if (androidVersion.getId().toLowerCase().equalsIgnoreCase(id)) {
               return androidVersion;
            }
        }
        return null;
    }


}
