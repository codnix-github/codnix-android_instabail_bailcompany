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
import com.bailcompany.utils.Dates;
import com.bailcompany.web.WebAccess;
import com.bumptech.glide.Glide;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by codnix on 16/11/17.
 */

public class DefendantListAdapter extends RecyclerView.Adapter<DefendantListAdapter.MyViewHolder>
        implements Filterable {
    DateTimeFormatter inputDateFormat;
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
        inputDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
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
        if (defDetail.getDOB() != null && !defDetail.getDOB().trim().equalsIgnoreCase("")) {
            String formattedDate = Const.getFormatedDate("yyyy-MM-dd", "MM/dd/yyyy", defDetail.getDOB()
                    .toString(), false);
            if (formattedDate != null && !formattedDate.trim().equalsIgnoreCase(""))
                holder.tvBirthdate.setText("DOB: " + formattedDate);
        }
        if (defDetail.getSSN() != null && !defDetail.getSSN().trim().equalsIgnoreCase(""))
            holder.tvSSN.setText("SSN: " + defDetail.getSSN());
        //Log.d("Path=", WebAccess.PHOTO + mFilteredList.get(i).getPhoto());
        final String url = WebAccess.PHOTO + defDetail.getPhoto();
        holder.ivTimeAlert.setVisibility(View.GONE);
        //tvLastUpdated
        if (!mReturn) {
            if (!defDetail.getDefUserId().equalsIgnoreCase("")) {
                holder.tvUserName.setText("User Name : " + defDetail.getUserName());
                // holder.btnLoginDetail.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rounded_primary));
                holder.btnLoginDetail.setBackgroundResource(R.drawable.button_rounded_primary);
                //   holder.tvLastUpdated.setText("Last updated "+defDetail.getLastAvailableTime());
                if (defDetail.getLastAvailableTime() != null && !defDetail.getLastAvailableTime().trim().equalsIgnoreCase("")) {
                    holder.tvLastUpdated.setText("Last updated : " + Dates.formatDate(context, inputDateFormat.parseDateTime(defDetail.getLastAvailableTime())));
                    int diff = Dates.getDifferentFromCurrentTime(inputDateFormat.parseDateTime(defDetail.getLastAvailableTime()), Dates.UNIT_MINUTE);
                    if (diff > 30) {
                        holder.ivTimeAlert.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivTimeAlert.setVisibility(View.GONE);
                    }
                }


            } else {
                holder.tvUserName.setText("");
                holder.btnLoginDetail.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_self_assign_small));
            }
        }

        holder.btnLoginDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLoginButtonClick(defDetail);
            }
        });


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


        void onLoginButtonClick(DefendantModel contact);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone;
        public ImageView thumbnail;
        ImageView ivTimeAlert;
        private String id;
        private TextView tvName, tvBirthdate, tvSSN, tvUserName, tvLastUpdated;
        private Button btnLoginDetail;
        private CircleImageView ivProfilePic;
        private RelativeLayout rlDefendantList;
        private Bitmap bitmap;

        public MyViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tvName);
            tvSSN = (TextView) view.findViewById(R.id.tvSSN);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvLastUpdated = (TextView) view.findViewById(R.id.tvLastUpdated);
            tvBirthdate = (TextView) view.findViewById(R.id.tvBirthdate);
            ivProfilePic = (CircleImageView) view.findViewById(R.id.ivProfilePic);
            rlDefendantList = (RelativeLayout) view.findViewById(R.id.rlDefendantList);
            btnLoginDetail = (Button) view.findViewById(R.id.btnLoginDetail);
            ivTimeAlert = (ImageView) view.findViewById(R.id.ivTimeAlert);
            id = "";

         /*   btnLoginDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onLoginButtonClick(defendantListFiltered.get(getAdapterPosition()));
                }
            });
*/
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
