package com.bailcompany.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bailcompany.MainActivity;
import com.bailcompany.R;
import com.bailcompany.custom.CustomFragment;
import com.bailcompany.model.EventModel;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.agenda.AgendaAdapter;
import com.github.tibolte.agendacalendarview.agenda.AgendaView;
import com.github.tibolte.agendacalendarview.calendar.CalendarView;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
@SuppressLint("InflateParams")
public class BailCalendar extends CustomFragment {

    static int getCallTimeout = 50000;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    AgendaCalendarView mAgendaCalendarView;

    ArrayList<EventModel> arrEvents;
    List<CalendarEvent> eventList;
    AlertDialog dialog;
    View v;
    AgendaAdapter adapter;
    private CalendarPickerController mCalendarPickerController;
    private CalendarView mCalendarView;
    private AgendaView mAgendaView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);


        v = localInflater.inflate(R.layout.activity_calendar, null);
        setHasOptionsMenu(true);
        initValues(v);
        getEvents();
        // designCalendar();
        // showMonthView();
        //   getEvents();
        return v;
    }

    private void initValues(View v) {

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabAddEvent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddEventDialog(null);
            }
        });

    }

    private void viewEvents() {
        showProgressDialog("");
        mAgendaCalendarView = (AgendaCalendarView) v.findViewById(R.id.agenda_calendar_view);
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);
        eventList = new ArrayList<>();
        mockList();
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), new CalendarPickerController() {
            @Override
            public void onDaySelected(DayItem dayItem) {

            }

            @Override
            public void onEventSelected(CalendarEvent calendarEvent) {

                if (calendarEvent.getId() == 0) {
                    DayItem dayItem = calendarEvent.getDayReference();
                    openAddEventDialog(dayItem.getDate());
                } else {
                    dispEvent(calendarEvent.getId() - 1, calendarEvent.getDayReference().getDate());
                }

            }

            @Override
            public void onScrollToDate(Calendar calendar) {

            }
        });

        mAgendaView = (AgendaView) mAgendaCalendarView.findViewById(R.id.agenda_view);

        adapter = (AgendaAdapter) mAgendaView.getAgendaListView().getAdapter();


        dismissProgressDialog();

    }

    private void openAddEventDialog(Date date) {


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null)
            return;
        View dialogForm = inflater.inflate(R.layout.dialog_add_event, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogForm);
        builder.create();

        final EditText edtTitle = (EditText) dialogForm.findViewById(R.id.edtTitle);
        final EditText edtLocation = (EditText) dialogForm.findViewById(R.id.edtLocation);
        final EditText edtDescription = (EditText) dialogForm.findViewById(R.id.edtDescription);
        final EditText edtDate = (EditText) dialogForm.findViewById(R.id.edtDate);
        Button btnSaveEvent = (Button) dialogForm.findViewById(R.id.btnSaveEventDetails);
        addEventDateView(edtDate, date);
        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestParams param = new RequestParams();

                param.put("TemporaryAccessCode",
                        MainActivity.user.getTempAccessCode());
                param.put("UserName", MainActivity.user.getUsername());

                param.put("eventTitle", edtTitle.getText().toString());
                param.put("eventDescription", edtDescription.getText().toString());
                param.put("eventLocation", edtLocation.getText().toString());
                param.put("eventDate", edtDate.getText().toString());
                EventModel e = new EventModel();
                e.setSummary(edtTitle.getText().toString());
                e.setDescription(edtDescription.getText().toString());
                e.setLocation(edtLocation.getText().toString());
                e.setDatetime(edtDate.getText().toString().replace(" ", "T"));
                e.setDate(edtDate.getText().toString().replace(" ", "T"));
                arrEvents.add(e);

                if (dialog != null)
                    dialog.dismiss();
                saveEvent(param);

            }
        });

        dialog = builder.create();
        dialog.setTitle("Add Event");
        dialog.show();
    }

    void addEventDateView(final EditText edtDate, final Date defaultDate) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if (defaultDate != null) {
                edtDate.setText(dateFormat.format(defaultDate));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date convertedDate = new Date();
                try {
                    if (defaultDate != null) {
                        convertedDate = defaultDate;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                new SlideDateTimePicker.Builder(getFragmentManager())
                        .setListener(new SlideDateTimeListener() {

                            @Override
                            public void onDateTimeSet(Date date) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String dateString = sdf.format(date);
                                edtDate.setText(dateString);

                            }

                            @Override
                            public void onDateTimeCancel() {
                                // Overriding onDateTimeCancel() is optional.
                            }
                        })
                        .setIs24HourTime(true)
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .setInitialDate(convertedDate)
                        .build()
                        .show();

                //   android.support.v4.app.DialogFragment newFragment = new DatePickerFragment();
                // newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }


    private void mockList() {

        eventList.clear();
        String startdt;
        for (int i = 0; i < arrEvents.size(); i++) {

            startdt = arrEvents.get(i).getDatetime().trim();
            Date date = null;
            if (!startdt.equalsIgnoreCase("")) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    date = format.parse(startdt);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //  startdt
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = format.parse(arrEvents.get(i).getDate());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (date != null) {
                Calendar startTime = Calendar.getInstance();
                //  Calendar endTime1 = Calendar.getInstance();
                startTime.setTime(date);
                BaseCalendarEvent event1 = new BaseCalendarEvent(arrEvents.get(i).getSummary(), arrEvents.get(i).getDescription(), arrEvents.get(i).getLocation(),
                        ContextCompat.getColor(getActivity(), R.color.green), startTime, startTime, true);
                event1.setId(i + 1);
                eventList.add(event1);
            }

        }

        Log.d("TotaleVENT=", "" + eventList.size());
    }

    private void addLastAddedEvent() {

        String startdt;
        startdt = arrEvents.get(arrEvents.size() - 1).getDatetime().trim();
        Date date = null;
        if (!startdt.equalsIgnoreCase("")) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                date = format.parse(startdt);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            //  startdt
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                date = format.parse(arrEvents.get(arrEvents.size() - 1).getDate());

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (date != null) {
            Calendar startTime = Calendar.getInstance();
            //  Calendar endTime1 = Calendar.getInstance();
            startTime.setTime(date);
            BaseCalendarEvent event1 = new BaseCalendarEvent(arrEvents.get(arrEvents.size() - 1).getSummary(), arrEvents.get(arrEvents.size() - 1).getDescription(), arrEvents.get(arrEvents.size() - 1).getLocation(),
                    ContextCompat.getColor(getActivity(), R.color.green), startTime, startTime, true);
            event1.setId(arrEvents.size());

            eventList.add(event1);

            Log.d("TotaleVENT2=", "" + eventList.size());


            mockList();
            adapter.updateEvents(eventList);


        }


    }

    private void addLast() {
        mockList();
        adapter.updateEvents(eventList);
    }

    private String getFormatedDate(String dateToParse, String dateTimeToParse) {

        String startdt = dateTimeToParse;
        String returnDt = "";
        Date date = null;

        if (!startdt.equalsIgnoreCase("")) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                date = format.parse(dateTimeToParse);
                SimpleDateFormat retFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                if (date != null)
                    returnDt = retFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = format.parse(dateToParse);
                SimpleDateFormat retFormat = new SimpleDateFormat("MM/dd/yyyy");
                if (date != null)
                    returnDt = retFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        return returnDt;
    }

    private void dispEvent(long id, Date selecteddate) {

        AlertDialog dialog;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogForm = inflater.inflate(R.layout.dialog_event_details, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogForm);
        builder.create();

        TextView tvDate = (TextView) dialogForm.findViewById(R.id.tvDate);
        final TextView tvLocation = (TextView) dialogForm.findViewById(R.id.tvLocation);
        TextView tvDescription = (TextView) dialogForm.findViewById(R.id.tvDescription);
        LinearLayout llMap = (LinearLayout) dialogForm.findViewById(R.id.llLocation);

        tvDate.setText(getFormatedDate(arrEvents.get((int) id).getDate(), arrEvents.get((int) id).getDatetime()));

        if (!arrEvents.get((int) id).getLocation().equals("")) {
            llMap.setVisibility(View.VISIBLE);
            tvLocation.setText(arrEvents.get((int) id).getLocation());
        }

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q=" + tvLocation.getText().toString()));
                startActivity(i);
            }
        });
        tvDescription.setText(arrEvents.get((int) id).getDescription());


        dialog = builder.create();
        dialog.setTitle(arrEvents.get((int) id).getSummary());
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.call) {

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getEvents() {
        showProgressDialog("");
        RequestParams param = new RequestParams();

        param.put("TemporaryAccessCode",
                MainActivity.user.getTempAccessCode());
        param.put("UserName", MainActivity.user.getUsername());

        String url = WebAccess.MAIN_URL + WebAccess.GET_COMPANY_EVENTS;
        client.setTimeout(getCallTimeout);

        client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                Log.d("Error=",""+statusCode+responseBody);
                dismissProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


                try {
                    dismissProgressDialog();
                    String response2;
                    response2 = new String(responseBody);

                    Log.d("EventRes=",response2);
                    if (response2 != null) {
                        JSONObject json;
                        json = new JSONObject(response2);
                        if (json.optString("status").equalsIgnoreCase("1")) {

                          //  Log.d("Response:", response2);
                            arrEvents = new ArrayList<>();
                            JSONArray events = json.getJSONArray("eventdata");
                            for (int i = 0; i < events.length(); i++) {
                                EventModel e = new EventModel();
                                JSONObject obj = events.getJSONObject(i);
                                e.setSummary(obj.getString("summary"));
                                e.setDate(obj.getString("date"));
                                e.setDatetime(obj.getString("datetime"));
                                e.setDescription(obj.getString("description"));
                                e.setLocation(obj.getString("location"));
                                arrEvents.add(e);
                            }
                            viewEvents();
                        }

                    } else
                        Utils.showDialog(getActivity(), R.string.err_unexpect);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }


    public void saveEvent(RequestParams param) {


        showProgressDialog("");


        String url = WebAccess.MAIN_URL + WebAccess.ADD_COMPANY_EVENTS;
        client.setTimeout(getCallTimeout);

        client.post(getActivity(), url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {


                dismissProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


                try {
                    dismissProgressDialog();
                    String response2;
                    response2 = new String(responseBody);
                    if (response2 != null) {
                        JSONObject json;
                        json = new JSONObject(response2);
                        Utils.showDialog(getActivity(), json.optString("message"));

                        if (json.optString("status").equalsIgnoreCase("1")) {
                            viewEvents();

                        } else {
                            arrEvents.remove(arrEvents.size() - 1);
                        }


                    } else {
                        arrEvents.remove(arrEvents.size() - 1);
                        Utils.showDialog(getActivity(), R.string.err_unexpect);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    arrEvents.remove(arrEvents.size() - 1);
                    e.printStackTrace();
                }
            }

        });
    }
}
