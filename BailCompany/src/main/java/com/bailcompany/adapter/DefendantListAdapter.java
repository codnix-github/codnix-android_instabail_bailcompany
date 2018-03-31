package com.bailcompany.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bailcompany.R;
import com.bailcompany.model.DefendantModel;
import com.bailcompany.ui.Defendant;
import com.bailcompany.utils.Const;
import com.bailcompany.web.WebAccess;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ravi on 16/11/17.
 */

public class DefendantListAdapter extends RecyclerView.Adapter<DefendantListAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<DefendantModel> defendantList;
    private List<DefendantModel> defendantListFiltered;
    private DefendantClickListner listener;
    private boolean mReturn;

    public DefendantListAdapter(Context context, List<DefendantModel> contactList, boolean returnresult, DefendantClickListner listener) {
        this.context = context;
        this.listener = listener;
        this.defendantList = contactList;
        mReturn = returnresult;
        this.defendantListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_defendant_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final DefendantModel defDetail = defendantListFiltered.get(position);
        holder.tvName.setText(defDetail.getFirstName() + " " + defDetail.getLastName());
        if (defDetail.getDOB() != null && !defDetail.getDOB().trim().equalsIgnoreCase(""))
            holder.tvBirthdate.setText("DOB: " + Const.getFormatedDate("yyyy-MM-dd", "MM/dd/yyyy", defDetail.getDOB()
                    .toString(), false));
        if (defDetail.getSSN() != null && !defDetail.getSSN().trim().equalsIgnoreCase(""))
            holder.tvSSN.setText("SSN: " + defDetail.getSSN());
        //Log.d("Path=", WebAccess.PHOTO + mFilteredList.get(i).getPhoto());
        final String url = WebAccess.PHOTO + defDetail.getPhoto();
        if (!mReturn) {
            if (!defDetail.getDefUserId().equalsIgnoreCase("")) {
                holder.tvUserName.setText("User Name : " + defDetail.getUserName());
                holder.btnLoginDetail.setBackground(ContextCompat.getDrawable(context, R.drawable.more_btn));
            } else {
                holder.tvUserName.setText("");
                holder.btnLoginDetail.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_self_assign_small));
            }
        }

        if (mReturn)
            holder.btnLoginDetail.setVisibility(View.GONE);
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.default_profile_image)

                .error(R.drawable.default_profile_image)

                //    .apply(RequestOptions.circleCropTransform())
                .into(holder.ivProfilePic);
    }

    @Override
    public int getItemCount() {
        return defendantListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    defendantListFiltered = defendantList;
                } else {
                    List<DefendantModel> filteredList = new ArrayList<>();
                    for (DefendantModel defModel : defendantList) {

                        if (defModel.getFirstName().toLowerCase().contains(charString) || defModel.getLastName().toLowerCase().contains(charString) || defModel.getSSN().toLowerCase().contains(charString) || defModel.getDOB().toLowerCase().contains(charString)) {
                            filteredList.add(defModel);
                        }
                    }
                    defendantListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = defendantListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                defendantListFiltered = (ArrayList<DefendantModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface DefendantClickListner {
        void onContactSelected(DefendantModel contact);

        void onLoginButtonClick(DefendantModel contact);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone;
        public ImageView thumbnail;

        private String id;
        private TextView tvName, tvBirthdate, tvSSN, tvUserName;
        private Button btnLoginDetail;
        private CircleImageView ivProfilePic;
        private RelativeLayout rlDefendantList;
        private Bitmap bitmap;

        public MyViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tvName);
            tvSSN = (TextView) view.findViewById(R.id.tvSSN);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvBirthdate = (TextView) view.findViewById(R.id.tvBirthdate);
            ivProfilePic = (CircleImageView) view.findViewById(R.id.ivProfilePic);
            rlDefendantList = (RelativeLayout) view.findViewById(R.id.rlDefendantList);
            btnLoginDetail = (Button) view.findViewById(R.id.btnLoginDetail);
            id = "";
            rlDefendantList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(defendantListFiltered.get(getAdapterPosition()));
                }
            });
            btnLoginDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onLoginButtonClick(defendantListFiltered.get(getAdapterPosition()));
                }
            });

            rlDefendantList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mReturn) {
                        Intent intent = new Intent();
                        intent.putExtra(Const.RETURN_FLAG, Const.RETURN_DEFENDANT_DETAIL);

                        intent.putExtra(Const.EXTRA_DATA, defendantListFiltered.get(getAdapterPosition()));

                        ((Activity) context).setResult(RESULT_OK, intent);
                        ((Activity) context).finish();
                    } else {
                        Intent intent = new Intent(context, Defendant.class);
                        intent.putExtra("defId", defendantListFiltered.get(getAdapterPosition()).getId());
                        context.startActivity(intent);
                    }

                }
            });


        }
    }
}
