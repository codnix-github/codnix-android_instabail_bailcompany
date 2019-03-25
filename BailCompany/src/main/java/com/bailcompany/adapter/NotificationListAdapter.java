package com.bailcompany.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bailcompany.HistoryRequestListNew;
import com.bailcompany.R;
import com.bailcompany.model.NotificationModel;
import com.bailcompany.utils.Const;

import java.util.List;

/**
 * Created by codnix on 16/2/19.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<NotificationModel> notificationList;
    private NotificationClickListner listener;
    private boolean mReturn;
    private boolean isLoading;
    RecyclerView recyclerView;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    public NotificationListAdapter(Context context, RecyclerView recyclerView, List<NotificationModel> notificationList, boolean returnresult, NotificationClickListner listener) {
        this.context = context;
        this.listener = listener;
        this.notificationList = notificationList;
        mReturn = returnresult;
        this.recyclerView = recyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.recyclerView.getLayoutManager();
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {

                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return notificationList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification_details, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }

       /* View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification_details, parent, false);*/

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder itemHolder = (MyViewHolder) holder;

            final NotificationModel notiDetail = notificationList.get(position);

            if (notiDetail.getCreatedAt() != null && !notiDetail.getCreatedAt().equalsIgnoreCase("")) {
                String localdate = Const.getFormatedDate("yyyy-MM-dd hh:mm", "MM/dd/yyyy hh:mm", notiDetail.getCreatedAt(), true);
                itemHolder.tvNotificationDate.setText(localdate);
            } else {
                itemHolder.tvNotificationDate.setText("-");
            }

            String noteMsg = notiDetail.getMessage();
            String type = notiDetail.getType();

            if (type.equalsIgnoreCase("106") || type.equalsIgnoreCase("107") || type.equalsIgnoreCase("108")) {
                if (noteMsg.contains("has acknowledged at ")) {
                    String date = noteMsg.substring(noteMsg.indexOf(" and will be attending ") - 19, noteMsg.indexOf(" and will be attending "));
                    if (date != null && !date.trim().equalsIgnoreCase("")) {
                        String localdate = Const.getFormatedDate("yyyy-MM-dd hh:mm", "MM/dd/yyyy hh:mm", date.trim(), true);
                        noteMsg = noteMsg.replace(" on " + date.trim() + " and", " on " + localdate + " and");
                    }
                } else if (noteMsg.contains("has checked-in at ")) {
                    String date = noteMsg.substring(noteMsg.lastIndexOf(" on ") + 3);
                    if (date != null && !date.trim().equalsIgnoreCase("")) {
                        String localdate = Const.getFormatedDate("yyyy-MM-dd hh:mm", "MM/dd/yyyy hh:mm", date.trim(), true);
                        noteMsg = noteMsg.replace(" on " + date.trim(), " on " + localdate);
                    }


                }
            }

            itemHolder.tvMessage.setText(noteMsg);

            itemHolder.cvRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNotificationClick(notiDetail);
                }
            });
        }
        else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public interface NotificationClickListner {
        void onNotificationClick(NotificationModel notDetails);

    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cvRoot;
        private String id;
        private TextView tvMessage, tvNotificationDate;

        public MyViewHolder(View view) {
            super(view);

            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            tvNotificationDate = (TextView) view.findViewById(R.id.tvNotificationDate);
            cvRoot = (CardView) view.findViewById(R.id.cvRoot);
        }
    }
}
