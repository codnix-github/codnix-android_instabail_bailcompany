package com.bailcompany;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.CompanyFilterModel;

import java.util.ArrayList;
import java.util.List;

public class CompanyFilterActivity extends CustomActivity {

    // dummy list of items to be populated manually
    public static List<String> uniqueCompanyList = new ArrayList<>();
    public static List<String> selectedCompanyList = new ArrayList<>();
    public static SparseBooleanArray previousItemStateArray = new SparseBooleanArray();
    public SparseBooleanArray itemStateArray = new SparseBooleanArray();
    public List<CompanyFilterModel> listCompanies = new ArrayList<>();
    public RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Button btnApplyFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_company);
        itemStateArray = previousItemStateArray;
        recyclerView = (RecyclerView) findViewById(R.id.rvFilter);
        btnApplyFilter = (Button) findViewById(R.id.btnApplyFilter);
        FilterAdapter adapter = new FilterAdapter();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        fillItems();

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.action_bar_bg));
        }
        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousItemStateArray = itemStateArray;
                selectedCompanyList.clear();
                for (int i = 0; i < uniqueCompanyList.size(); i++) {
                    if (itemStateArray.get(i, false)) {
                        selectedCompanyList.add(uniqueCompanyList.get(i));
                    }
                }
                Intent returnIntent = new Intent();
                //  returnIntent.putExtra("result",result);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });


        adapter.loadItems(listCompanies);
    }

    private void fillItems() {
        for (int x = 0; x < uniqueCompanyList.size(); x++) {
            CompanyFilterModel model = new CompanyFilterModel();
            model.setPosition(x + 1);
            model.setCompanyName(uniqueCompanyList.get(x));
            listCompanies.add(model);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InflateParams")
    private class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {


        private List<CompanyFilterModel> items = new ArrayList<>();

        FilterAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            int layoutForItem = R.layout.row_filter_company_list;
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(layoutForItem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            if (items == null) {
                return 0;
            }
            return items.size();
        }

        void loadItems(List<CompanyFilterModel> tournaments) {
            this.items = tournaments;
            notifyDataSetChanged();
        }


        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            CheckedTextView mCheckedTextView;

            ViewHolder(View itemView) {
                super(itemView);
                mCheckedTextView = (CheckedTextView) itemView.findViewById(R.id.checked_text_view);
                itemView.setOnClickListener(this);
            }

            void bind(int position) {
                // use the sparse boolean array to check
                if (!itemStateArray.get(position, false)) {
                    mCheckedTextView.setChecked(false);
                } else {
                    mCheckedTextView.setChecked(true);
                }
                mCheckedTextView.setText(String.valueOf(items.get(position).getCompanyName()));
            }

            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();
                if (!itemStateArray.get(adapterPosition, false)) {
                    mCheckedTextView.setChecked(true);
                    itemStateArray.put(adapterPosition, true);
                } else {
                    mCheckedTextView.setChecked(false);
                    itemStateArray.put(adapterPosition, false);
                }
            }

        }
    }

}
