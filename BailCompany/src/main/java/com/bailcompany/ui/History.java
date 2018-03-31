package com.bailcompany.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bailcompany.HistoryRequestList;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.utils.Const;
import com.bailcompany.web.WebAccess;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class History extends CustomFragment {
    ArrayList<String> historyItem = new ArrayList<String>();
    @SuppressLint("InflateParams")
    private ListView historyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_main, null);

        historyList = (ListView) v.findViewById(R.id.history_list);
        historyItem.add("All Posting Agent Transactions");
        if (!Const.Menu.SHOW_LIMTED_MENU) {
            historyItem.add("My Sent Transfer Bond Requests");
            historyItem.add("My Accepted Transfer Bond Requests");
            historyItem.add("My Sent Referral Bail Requests");
            historyItem.add("My Accepted Referral Bail Requests");
        }

        historyList.setAdapter(new IncomingListAdapter());

        historyList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String title = "Bail Request";
                String item = historyItem.get(position);
                if (item.equalsIgnoreCase("My Sent Transfer Bond Requests")) {
                    title = "My Sent Transfer Bond Requests";

                } else if (item
                        .equalsIgnoreCase("My Accepted Transfer Bond Requests")) {
                    title = "My Accepted Transfer Bond Requests";

                } else if (item.equalsIgnoreCase("My Sent Referral Bail Requests")) {
                    title = "My Sent Referral Bail Requests";

                } else if (item.equalsIgnoreCase("My Accepted Referral Bail Requests")) {
                    title = "My Accepted Referral Bail Requests";

                } else if (item
                        .equalsIgnoreCase("All Posting Agent Transactions")) {
                    title = "All Posting Agent Transactions";

                }
                startActivity(new Intent(getActivity(),
                        HistoryRequestList.class).putExtra("title", title));

            }
        });
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (WebAccess.hireReferBailAgent || WebAccess.hireTransferBondAgent) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("InflateParams")
    private class IncomingListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return historyItem.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return historyItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.history_item, null);
            LinearLayout lp = (LinearLayout) convertView.findViewById(R.id.lp);
            if (position % 2 == 0)
                lp.setBackgroundColor(Color.parseColor("#F1EFEF"));
            else
                lp.setBackgroundColor(Color.WHITE);

            ((TextView) convertView.findViewById(R.id.company_name))
                    .setText(historyItem.get(position));

            return convertView;
        }
    }
}
