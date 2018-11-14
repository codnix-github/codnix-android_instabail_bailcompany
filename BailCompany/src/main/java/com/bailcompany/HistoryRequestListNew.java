package com.bailcompany;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.interfaces.HistoryRequestListClickListner;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class HistoryRequestListNew extends CustomActivity implements HistoryRequestListClickListner {

    private static final boolean FETCH_ALL_REQUEST = false;
    public static ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
    public static String title;
    public static boolean IsBailRequest;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static int getCallTimeout = 50000;

    String message;
    JSONObject jsonObj;
    String key, apiUrl;
    boolean isSender;
    int page = 0;
    boolean loadingMore;
    IncomingListAdapter adapter;
    android.widget.SearchView svRequest;
    private RecyclerView incomingRequestList;
    private ArrayList<BailRequestModel> filteredData = null;
    private int FILTER_COMPANY_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_request_new);
        title = getIntent().getStringExtra("title");
        svRequest = (android.widget.SearchView) findViewById(R.id.svRequest);
        if (title.equalsIgnoreCase("My Sent Transfer Bond Requests")) {
            isSender = true;
            IsBailRequest = false;
            apiUrl = WebAccess.GET_SENT_BOND_REQUESTS_HISTORY;
        } else if (title.equalsIgnoreCase("My Accepted Transfer Bond Requests")) {
            apiUrl = WebAccess.GET_ACCEPTED_BOND_REQUESTS_HISTORY;
            isSender = false;
            IsBailRequest = false;
        } else if (title.equalsIgnoreCase("My Sent Referral Bail Requests")) {
            isSender = true;
            apiUrl = WebAccess.GET_SENT_REFER_BAIL_REQUESTS_HISTORY;
            IsBailRequest = false;
        } else if (title.equalsIgnoreCase("My Accepted Referral Bail Requests")) {
            apiUrl = WebAccess.GET_ACCEPTED_REFER_BAIL_REQUESTS_HISTORY;
            isSender = false;
            IsBailRequest = false;

        } else if (title.equalsIgnoreCase("All Posting Agent Transactions")) {
            apiUrl = WebAccess.GET_ALL_BAIL_REQUEST_HISTORY;
            isSender = false;
            IsBailRequest = true;

        }
        if (title != null) {
            setActionBar(title);
        }

        HistoryRequestList.IsBailRequest = IsBailRequest;

        getAllRequest();
        incomingRequestList = (RecyclerView) findViewById(R.id.incoming_request_list);
        incomingRequestList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        incomingRequestList.setHasFixedSize(true);

        // incomingRequestList.setAdapter(new IncomingListAdapter());
        adapter = new IncomingListAdapter(HistoryRequestListNew.this, incomingRequestList, bailReqList, this);
        incomingRequestList.setAdapter(adapter);
    /*    incomingRequestList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (title
                        .equalsIgnoreCase("My Accepted Referral Bail Requests")
                        || title.equalsIgnoreCase("My Sent Referral Bail Requests")) {
                    // if (MainActivity.accessType == 1
                    // || MainActivity.accessType == 3) {
                    startActivity(new Intent(HistoryRequestListNew.this,
                            HistoryReferRequestDetail.class)
                            .putExtra("bail", filteredData.get(position))
                            .putExtra("title", title)
                            .putExtra("position", position));
                    // } else
                    // Utils.showDialog(HistoryRequestList.this,
                    // "Denied Acces");
                } else if (title
                        .equalsIgnoreCase("My Accepted Transfer Bond Requests")
                        || title.equalsIgnoreCase("My Sent Transfer Bond Requests")) {
                    // if (MainActivity.accessType == 1
                    // || MainActivity.accessType == 3) {
                    startActivity(new Intent(HistoryRequestListNew.this,
                            HistoryBondRequestDetail.class)
                            .putExtra("bail", filteredData.get(position))
                            .putExtra("title", title)
                            .putExtra("position", position));
                    // } else
                    // Utils.showDialog(HistoryRequestList.this,
                    // "Denied Acces");
                } else {
                    startActivity(new Intent(HistoryRequestListNew.this,
                            HistoryRequestDetail.class).putExtra("bail",
                            filteredData.get(position)).putExtra("title", title));
                }
            }
        });*/
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (loadingMore) {
                    page++;
                    HistoryRequestListNew.bailReqList.add(null);
                    adapter.notifyItemInserted(HistoryRequestListNew.bailReqList.size() - 1);

                    getAllRequest();
                }
            }
        });

        search(svRequest);
    }

    private void search(android.widget.SearchView searchView) {
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();
        if (WebAccess.hireReferBailAgent || WebAccess.hireTransferBondAgent
                || WebAccess.hireBailAgent || WebAccess.selfAssignAgent) {
            finish();
        }
    }

    void hideKeyboard() {
        View view = THIS.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) THIS
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setActionBar(String title) {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(title);
    }

    void getAllRequest() {
        if (Utils.isOnline(HistoryRequestListNew.this)) {
            if (page == 0)
                showProgressDialog("");
            //showProgressDialog("");
            if (page == 0) {
                bailReqList.clear();

            }
            loadingMore = true;
            RequestParams param = new RequestParams();

            param.put("TemporaryAccessCode",
                    MainActivity.user.getTempAccessCode());
            param.put("UserName", MainActivity.user.getUsername());
            if (FETCH_ALL_REQUEST)
                param.put("Display_All", 1);
            param.put("Page", page);

            String url = WebAccess.MAIN_URL + apiUrl;
            client.setTimeout(getCallTimeout);
            client.post(HistoryRequestListNew.this, url, param,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                            if (page == 0)
                                dismissProgressDialog();

                            dismissProgressDialog();
                            Utils.showDialog(HistoryRequestListNew.this,
                                    R.string.err_unexpect);
                            loadingMore = true;
                            if (page > 0)
                                page--;
                        }

                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            if (page == 0) {
                                dismissProgressDialog();
                                WebAccess.AllBidListCompany.clear();
                            }
                            dismissProgressDialog();
                            try {
                                String response2;
                                loadingMore = true;
                                response2 = new String(responseBody);
                                JSONObject resObj;

                                resObj = new JSONObject(response2);

                                if (resObj != null) {
                                    if (resObj.optString("status")
                                            .equalsIgnoreCase("1")) {
                                        if (page != 0) {
                                            bailReqList.remove(bailReqList.size() - 1);
                                            adapter.notifyItemRemoved(bailReqList.size());

                                        }

                                        int previousLength = bailReqList.size();
                                        if (!isSender) {
                                            if (title
                                                    .equalsIgnoreCase("My Accepted Referral Bail Requests"))
                                                WebAccess
                                                        .getALLReferRequest(response2);
                                            else
                                                WebAccess
                                                        .getALLRequest(response2);
                                        } else {
                                            if (title
                                                    .equalsIgnoreCase("My Sent Referral Bail Requests"))
                                                WebAccess
                                                        .getALLReferBailRequestSender(response2);
                                            else

                                                WebAccess
                                                        .getALLRequestSender(response2);
                                        }

                                        if (previousLength == bailReqList.size()) {
                                            loadingMore = false;
                                        }
                                        if (bailReqList != null
                                                && bailReqList.size() > 0) {
                                            adapter.notifyDataSetChanged();
                                            adapter.setLoaded();
                                        } else {
                                            loadingMore = false;
                                            Utils.showDialog(
                                                    HistoryRequestListNew.this,
                                                    "No Bail Request Available")
                                                    .show();
                                            adapter.notifyDataSetChanged();
                                        }


                                    } else if (resObj.optString("status")
                                            .equalsIgnoreCase("3")) {
                                        Toast.makeText(
                                                THIS,
                                                "Session was closed please login again",
                                                Toast.LENGTH_LONG).show();
                                        MainActivity.sp.edit().putBoolean(
                                                "isFbLogin", false);
                                        MainActivity.sp.edit()
                                                .putString("user", null)
                                                .commit();
                                        startActivity(new Intent(
                                                HistoryRequestListNew.this,
                                                Launcher.class));
                                    } else {

                                        if (page != 0) {
                                            bailReqList.remove(bailReqList.size() - 1);
                                            adapter.notifyItemRemoved(bailReqList.size());

                                        }


                                        if (page == 0) {
                                            Utils.showDialog(
                                                    HistoryRequestListNew.this,
                                                    resObj.optString("message"))
                                                    .show();
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            if (page > 0)
                                                page--;
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    });

        } else
            Utils.noInternetDialog(HistoryRequestListNew.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == FILTER_COMPANY_CODE) {
            adapter.getFilter().filter(svRequest.getQuery());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        if (item.getItemId() == R.id.action_filters) {
            Intent i = new Intent(HistoryRequestListNew.this, CompanyFilterActivity.class);
            startActivityForResult(i, FILTER_COMPANY_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(BailRequestModel bail) {
        if (title
                .equalsIgnoreCase("My Accepted Referral Bail Requests")
                || title.equalsIgnoreCase("My Sent Referral Bail Requests")) {
            // if (MainActivity.accessType == 1
            // || MainActivity.accessType == 3) {
            startActivity(new Intent(HistoryRequestListNew.this,
                    HistoryReferRequestDetail.class)
                    .putExtra("bail", bail)
                    .putExtra("title", title)
                    .putExtra("position", getBailRequestPosition(bail.getAgentRequestId())));
            // } else
            // Utils.showDialog(HistoryRequestList.this,
            // "Denied Acces");
        } else if (title
                .equalsIgnoreCase("My Accepted Transfer Bond Requests")
                || title.equalsIgnoreCase("My Sent Transfer Bond Requests")) {
            // if (MainActivity.accessType == 1
            // || MainActivity.accessType == 3) {
            startActivity(new Intent(HistoryRequestListNew.this,
                    HistoryBondRequestDetail.class)
                    .putExtra("bail", bail)
                    .putExtra("title", title)
                    .putExtra("position", getBailRequestPosition(bail.getAgentRequestId())));
            // } else
            // Utils.showDialog(HistoryRequestList.this,
            // "Denied Acces");
        } else {
            startActivity(new Intent(HistoryRequestListNew.this,
                    HistoryRequestDetail.class).putExtra("bail",
                    bail).putExtra("title", title));
        }
    }

    public int getBailRequestPosition(int bailid) {

        for (int i = 0; i < bailReqList.size(); i++) {
            if (bailReqList.get(i).getAgentRequestId() == bailid) {
                return i;
            }
        }

        return -1;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    @SuppressLint("InflateParams")
    private class IncomingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        RecyclerView recyclerView;
        OnLoadMoreListener onLoadMoreListener;
        HistoryRequestListClickListner clickListner;
        private ArrayList<BailRequestModel> originalData = null;
        private boolean isLoading;
        private ItemFilter mFilter = new ItemFilter();
        private int visibleThreshold = 2;
        private int lastVisibleItem, totalItemCount;


        public IncomingListAdapter(Context context, RecyclerView recyclerView, ArrayList<BailRequestModel> data, HistoryRequestListClickListner clickListner) {
            filteredData = data;
            this.originalData = data;
            this.recyclerView = recyclerView;
            this.clickListner = clickListner;
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

        public void setLoaded() {
            isLoading = false;
        }


        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.onLoadMoreListener = mOnLoadMoreListener;

        }


        @Override
        public int getItemCount() {
            return filteredData.size();
        }

        @Override
        public int getItemViewType(int position) {
            return filteredData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

/*

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return filteredData.get(position);
        }
*/

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_request_item_new, parent, false);
                return new MyViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        /*  @Override
          public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
              View itemView = LayoutInflater.from(parent.getContext())
                      .inflate(R.layout.incoming_request_item_new, parent, false);

              return new MyViewHolder(itemView);
          }
  */
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof MyViewHolder) {
                final BailRequestModel requestdetail = filteredData.get(position);

                final MyViewHolder itemHolder = (MyViewHolder) holder;



          /*  if (position % 2 == 0)
                holder.lp.setBackgroundColor(Color.parseColor("#F1EFEF"));
            else
                holder.lp.setBackgroundColor(Color.WHITE);*/

                if (title.equalsIgnoreCase("My Sent Referral Bail Requests")
                        || title.equalsIgnoreCase("My Sent Transfer Bond Requests")
                        || title.equalsIgnoreCase("All Posting Agent Transactions")) {
                    itemHolder.company_name.setText(requestdetail.getDefendantName()
                            + "");
                } else {
                    itemHolder.company_name.setText(requestdetail
                            .getSenderCompanyName() + "");
                }

                itemHolder.date.setText(Utils.getRequiredDateFormatGMT("yyyy-MM-dd hh:mm:ss",
                        "MM/dd/yyyy", filteredData.get(position).getCreatedDate()));

                Bitmap bmp = new ImageLoader(StaticData.getDIP(100),
                        StaticData.getDIP(100), ImageLoader.SCALE_FITXY).loadImage(
                        filteredData.get(position).getSenderCompanyImage(),
                        new ImageLoadedListener() {

                            @Override
                            public void imageLoaded(Bitmap bm) {
                                Log.d("Bitmap", bm == null ? "Null Bitmap"
                                        : "Valid Bitmap");
                                if (bm != null)
                                    itemHolder.image.setImageBitmap(ImageUtils
                                            .getCircularBitmap(bm));
                            }
                        });
                if (bmp != null)
                    itemHolder.image.setImageBitmap(ImageUtils.getCircularBitmap(bmp));
                else
                    itemHolder.image.setVisibility(View.GONE);
                itemHolder.image.setImageBitmap(null);

                itemHolder.requestMainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListner.onClick(filteredData.get(position));
                    }
                });

                setupSteps(requestdetail, itemHolder);

            } else {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }


        }

        void setupSteps(BailRequestModel requestdetail, MyViewHolder itemHolder) {

            try {
                if (requestdetail.getIsComplete().equals("1")) {
                    if (requestdetail.getRequestCompletionTime().equals("")) {
                        itemHolder.requestProgress.setVisibility(View.GONE);
                        return;
                    }
                }

                itemHolder.tv_dispatch.setTextColor(getResources().getColor(R.color.grey_30));
                itemHolder.tv_accepted.setTextColor(getResources().getColor(R.color.grey_10));
                itemHolder.tv_arrival.setTextColor(getResources().getColor(R.color.grey_10));
                itemHolder.tv_completion.setTextColor(getResources().getColor(R.color.grey_10));
                itemHolder.image_accepted.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
                itemHolder.image_dispatch.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                itemHolder.image_arrival.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
                itemHolder.image_completion.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
                itemHolder.requestProgress.setVisibility(View.VISIBLE);
                itemHolder.tv_accepted.setText("Accepted");
                itemHolder.tv_completion.setText("Completed");


                itemHolder.rlStep2.setVisibility(View.VISIBLE);
                itemHolder.rlStep3.setVisibility(View.VISIBLE);
                itemHolder.rlStep4.setVisibility(View.VISIBLE);


                if (requestdetail.isIsAccept().equals("1")) {
                    itemHolder.image_accepted.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    itemHolder.tv_accepted.setTextColor(getResources().getColor(R.color.grey_30));
                }
                if (!requestdetail.getAgentArrivedTime().equals("")) {
                    itemHolder.image_arrival.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    itemHolder.tv_arrival.setTextColor(getResources().getColor(R.color.grey_30));
                    if (requestdetail.getAgentAcceptedTime().equals("")) {
                        itemHolder.requestProgress.setVisibility(View.GONE);
                        return;
                    }

                } else {

                }
                if (requestdetail.getIsComplete().equals("1")) {
                    if (requestdetail.getRequestCompletionTime().equals("")) {
                        itemHolder.requestProgress.setVisibility(View.GONE);
                        return;
                    }

                    itemHolder.image_completion.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    itemHolder.tv_completion.setTextColor(getResources().getColor(R.color.grey_30));

                    if (requestdetail.getAgentAcceptedTime().equals("")) {
                        itemHolder.requestProgress.setVisibility(View.GONE);
                        return;

                    }
                    if (requestdetail.getAgentArrivedTime().equals("")) {
                        itemHolder.requestProgress.setVisibility(View.GONE);
                        return;

                    }

                }


                if (requestdetail.getAgentId().equals(requestdetail.getSenderCompanyId())) {
                    itemHolder.image_accepted.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    itemHolder.tv_accepted.setTextColor(getResources().getColor(R.color.grey_30));
                    itemHolder.tv_accepted.setText("Self Assigned");

                }


                if (requestdetail.getIsAbort().equals("1")) {
                    // aborted
                    if (requestdetail.getIsCancel().equals("1")) {
                        //cancelled
                        itemHolder.image_completion.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        itemHolder.tv_completion.setTextColor(getResources().getColor(R.color.grey_30));
                        itemHolder.tv_completion.setText("Cancelled");

                    } else {
                        itemHolder.image_completion.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        itemHolder.tv_completion.setTextColor(getResources().getColor(R.color.grey_30));
                        itemHolder.tv_completion.setText("Aborted");

                    }

                    if (requestdetail.getAgentArrivedTime().equals("")) {
                        itemHolder.requestProgress.setVisibility(View.GONE);

                        return;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        @Override
        public Filter getFilter() {
            return mFilter;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView company_name, date;

            public ImageView image;
            public LinearLayout lp;

            RelativeLayout rlStep1, rlStep2, rlStep3, rlStep4;
            ImageView image_dispatch, image_accepted, image_arrival, image_completion;
            TextView tv_completion, tv_dispatch, tv_accepted, tv_arrival;
            LinearLayout requestProgress;
            ImageView line_first, line_second;
            CardView requestMainLayout;


            public MyViewHolder(View view) {
                super(view);

                image = (ImageView) view
                        .findViewById(R.id.image);

                date = (TextView) view.findViewById(R.id.date);
                company_name = (TextView) view.findViewById(R.id.company_name);
                lp = (LinearLayout) view.findViewById(R.id.lp);
                rlStep1 = (RelativeLayout) view.findViewById(R.id.rlStep1);
                rlStep2 = (RelativeLayout) view.findViewById(R.id.rlStep2);
                rlStep3 = (RelativeLayout) view.findViewById(R.id.rlStep3);
                rlStep4 = (RelativeLayout) view.findViewById(R.id.rlStep4);
                requestProgress = (LinearLayout) view.findViewById(R.id.requestProgress);
                requestMainLayout = (CardView) view.findViewById(R.id.requestMainLayout);


                image_dispatch = (ImageView) view.findViewById(R.id.image_dispatch);
                image_accepted = (ImageView) view.findViewById(R.id.image_accepted);
                image_arrival = (ImageView) view.findViewById(R.id.image_arrival);
                image_completion = (ImageView) view.findViewById(R.id.image_completion);

                tv_completion = (TextView) view.findViewById(R.id.tv_completion);
                tv_dispatch = (TextView) view.findViewById(R.id.tv_dispatch);
                tv_accepted = (TextView) view.findViewById(R.id.tv_accepted);
                tv_arrival = (TextView) view.findViewById(R.id.tv_arrival);


            }


        }

        class LoadingViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingViewHolder(View itemView) {
                super(itemView);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            }
        }


        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();
                final List<BailRequestModel> list;
                int count = 0;

                list = originalData;

                count = list.size();


                final ArrayList<BailRequestModel> nlist = new ArrayList<BailRequestModel>();

                String filterAgentName, filterDefName, filterCompanyName, filterBookingNo, filterDefSSN;

                for (int i = 0; i < count; i++) {

                    filterAgentName = list.get(i).getAgentName() != null ? list.get(i).getAgentName().toLowerCase() : "";
                    filterDefName = list.get(i).getDefendantName() != null ? list.get(i).getDefendantName().toLowerCase() : "";
                    filterCompanyName = list.get(i).getSenderCompanyName() != null ? list.get(i).getSenderCompanyName().toLowerCase() : "";
                    filterBookingNo = list.get(i).getDefBookingNumber() != null ? list.get(i).getDefBookingNumber().toLowerCase() : "";
                    filterDefSSN = list.get(i).getDefSSN() != null ? list.get(i).getDefSSN().toLowerCase() : "";

                    if (filterAgentName.contains(filterString) || filterDefName.contains(filterString) || filterCompanyName.contains(filterString) || filterBookingNo.contains(filterString) || filterDefSSN.contains(filterString)) {
                        nlist.add(list.get(i));
                    }

                }
                final ArrayList<BailRequestModel> nlist2 = new ArrayList<BailRequestModel>();


                for (int i = 0; i < nlist.size(); i++) {
                    filterCompanyName = nlist.get(i).getSenderCompanyName() != null ? nlist.get(i).getSenderCompanyName().toLowerCase() : "";
                    if (filterCompanyName != null) {
                        for (int j = 0; j < CompanyFilterActivity.selectedCompanyList.size(); j++) {
                            if (filterCompanyName.equalsIgnoreCase(CompanyFilterActivity.selectedCompanyList.get(j))) {
                                nlist2.add(nlist.get(i));
                                break;
                            }
                        }
                    }

                }


                results.values = nlist2;
                results.count = nlist2.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<BailRequestModel>) results.values;
                notifyDataSetChanged();
            }

        }
    }
}
