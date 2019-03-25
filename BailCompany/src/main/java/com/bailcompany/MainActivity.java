package com.bailcompany;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.AgentModel;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.Feed;
import com.bailcompany.model.User;
import com.bailcompany.ui.AgentList;
import com.bailcompany.ui.BadDebtMembers;
import com.bailcompany.ui.BailCalendar;
import com.bailcompany.ui.BlackListMembers;
import com.bailcompany.ui.ContactUs;
import com.bailcompany.ui.DefendantList;
import com.bailcompany.ui.FugitiveAgent;
import com.bailcompany.ui.GetAnAgent;
import com.bailcompany.ui.History;
import com.bailcompany.ui.IncomingBailRequest;
import com.bailcompany.ui.IncomingFugitveRequest;
import com.bailcompany.ui.IncomingRequest;
import com.bailcompany.ui.InstantChat;
import com.bailcompany.ui.InstantGroupChat;
import com.bailcompany.ui.LeftNavAdapter;
import com.bailcompany.ui.NotificationListActivity;
import com.bailcompany.ui.ReferBail;
import com.bailcompany.ui.SelfAssigned;
import com.bailcompany.ui.ShowAllOnMap;
import com.bailcompany.ui.TransferBond;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.ImageLoader;
import com.bailcompany.utils.ImageLoader.ImageLoadedListener;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.Methods;
import com.bailcompany.utils.ObjectSerializer;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.Utility;
import com.bailcompany.utils.Utils;
import com.bailcompany.web.WebAccess;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The Activity MainActivity will launched at the start of the app.
 */
@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class MainActivity extends CustomActivity {

    /**
     * The drawer layout.
     */
    public static DrawerLayout drawerLayout;

    /**
     * ListView for left side drawer.
     */
    public static ListView drawerLeft;
    public static User user;
    public static boolean isLoggedIn = true;
    public static SharedPreferences sp;
    public static BailRequestModel bailReqModel;
    public static ArrayList<AgentModel> agentList = new ArrayList<AgentModel>();
    private static boolean isOnce;
    /**
     * ListView for left side drawer.
     */

    Bundle arg;
    String badgeMessage;
    SharedPreferences prefs;
    ArrayList<Feed> menuItems;
    /**
     * The drawer toggle.
     */
    private ActionBarDrawerToggle drawerToggle;
    private LeftNavAdapter adp;

    // public static int accessType = 0;
    private OnClickListener ocl = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            drawerLayout.closeDrawers();
            startActivity(new Intent(THIS, CompanyProfile.class).putExtra(
                    "user", user));
        }
    };
    // This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("6") || message.equalsIgnoreCase("8")
                    || message.equalsIgnoreCase("10")
                    || message.equalsIgnoreCase("13")
                    || message.equalsIgnoreCase("14")
                    || message.equalsIgnoreCase("20")
                    || message.equalsIgnoreCase("21")
                    || message.equalsIgnoreCase("28")) {
                drawerLeft.setAdapter(adp);
            }
        }
    };

    /*
     * (non-Javadoc)
     *
     * @see com.newsfeeder.custom.CustomActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Utility.checkPermission(THIS);
        // Thread.setDefaultUncaughtExceptionHandler(new
        // CustomExceptionHandler());
        isOnce = true;
        WebAccess.type = null;

        sp = PreferenceManager.getDefaultSharedPreferences(THIS);
        prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        WebAccess.referBailBadge = prefs.getBoolean("referrel", false);
        WebAccess.tranferBondBadge = prefs.getBoolean("bond", false);
        WebAccess.fugitiveBadge = prefs.getBoolean("fugitive", false);
        WebAccess.instant = prefs.getBoolean("instant", false);
        String usr = sp.getString("user", "");
        try {
            user = (User) ObjectSerializer.deserialize(usr);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//		if (MainActivity.user.getPackageId().equalsIgnoreCase("0")
//				|| !MainActivity.user.getPackageExpired().equalsIgnoreCase(""))
//			startActivity(new Intent(THIS, Subscription2Activity.class));

        Log.d("User", user == null ? "Null User" : "User not null");

        setupActionBar();
        // setupDrawer();
        showLocationAlert();
        getLocation();
        setupContainer();

    }

    private void showLocationAlert() {
        LocationManager lManager = (LocationManager) StaticData.appContext
                .getSystemService(Context.LOCATION_SERVICE);
        if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("GPS", "NOT AVAIL");
            String msg = "Your location is turned off, kindly click on OK button to turn location On.";

            Utils.showDialog(THIS, msg, "Ok", "cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        isLoggedIn = true;
        super.onResume();
        hideKeyboard();
        this.registerReceiver(mMessageReceiver, new IntentFilter(
                "update_drawer"));
        if (WebAccess.hireReferBailAgent || WebAccess.hireTransferBondAgent) {
            setupContainer2();
        }
        if (WebAccess.type != null && !WebAccess.fromFindMe
                && !WebAccess.hireTransferBondAgent
                && !WebAccess.hireReferBailAgent) {

            WebAccess.fromFindMe = false;
            if (WebAccess.type.equalsIgnoreCase("2")
                    || WebAccess.type.equalsIgnoreCase("12")

                    || WebAccess.type.equalsIgnoreCase("5")
                    || WebAccess.type.equalsIgnoreCase("4"))

                launchNext(getMenuIndexByName(Const.Menu.TRACK_AGENT), null);
            else if (WebAccess.type.equalsIgnoreCase("6"))
                launchNext(getMenuIndexByName(Const.Menu.TRANSFER_BOND), null);

            else if (WebAccess.type.equalsIgnoreCase("8"))
                launchNext(getMenuIndexByName(Const.Menu.INCOMING_REFFER_BAIL), null); //7

            else if (WebAccess.type.equalsIgnoreCase("10"))
                launchNext(getMenuIndexByName(Const.Menu.SENT_FUGITIVE_REQUEST), null); //11
            else if (WebAccess.type.equalsIgnoreCase("6"))
                launchNext(getMenuIndexByName(Const.Menu.HISTORY), null); //12
            else if (WebAccess.type.equalsIgnoreCase("13"))
                launchNext(getMenuIndexByName(Const.Menu.INSTANT_CHAT), null); //13

        }

        WebAccess.hireBailAgent = false;
        WebAccess.type = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * This method will setup the top title bar (Action bar) content and display
     * values. It will also setup the custom background theme for ActionBar. You
     * can override this method to change the behavior of ActionBar for
     * particular Activity
     */
    @Override
    protected void setupActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.theme_black);
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.action_bar_bg));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        getActionBar().setTitle(R.string.trck_agnt);

        // getActionBar().setBackgroundDrawable(getResources().getDrawable(theme));
    }

    private void getLocation() {

    }

    /**
     * Setup the drawer layout. This method also includes the method calls for
     * setting up the Left & Right side drawers.
     */
    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.home_icon, R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                hideKeyboard();
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.closeDrawers();

        setupLeftNavDrawer();

        drawerLayout.openDrawer(drawerLeft);
    }

    /**
     * Setup the left navigation drawer/slider. You can add your logic to load
     * the contents to be displayed on the left side drawer. It will also setup
     * the Header and Footer contents of left drawer. This method also apply the
     * Theme for components of Left drawer.
     */
    void hideKeyboard() {
        View view = THIS.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) THIS
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setupLeftNavDrawer() {
        drawerLeft = (ListView) findViewById(R.id.left_drawer);

        View header = getLayoutInflater().inflate(R.layout.left_nav_header,
                null);
        if (user != null) {
            ((TextView) header.findViewById(R.id.name)).setText(user.getName());
            ((TextView) header.findViewById(R.id.mail)).setText(user.getEmail()
                    + "");
            ((TextView) header.findViewById(R.id.company_name)).setText(user
                    .getCompanyName() + "");
            ((TextView) header.findViewById(R.id.phone_no)).setText(Utils
                    .getFormattedText(user.getPhone()));

            phoneFormat((TextView) header.findViewById(R.id.phone_no));
            final ImageView p = (ImageView) header
                    .findViewById(R.id.profile_pic);

            Bitmap bm = new ImageLoader(StaticData.getDIP(60),
                    StaticData.getDIP(60), ImageLoader.SCALE_FITXY).loadImage(
                    user.getPhoto(), new ImageLoadedListener() {

                        @Override
                        public void imageLoaded(Bitmap bm) {
                            if (bm != null)
                                p.setImageBitmap(ImageUtils
                                        .getCircularBitmap(bm));
                        }
                    });
            if (bm != null)
                p.setImageBitmap(ImageUtils.getCircularBitmap(bm));
            else
                p.setImageBitmap(null);

        }

        if (isOnce) {
            drawerLeft.addHeaderView(header);
            // launchNext(-1);
        }
        header.setOnClickListener(ocl);
        setMenuItems();

        adp = new LeftNavAdapter(this, menuItems);
        drawerLeft.setAdapter(adp);
        drawerLeft.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View arg1, int arg2,
                                    long arg3) {

                if (arg2 > 0) {
                    for (Feed f : menuItems)
                        f.setDesc(null);
                    menuItems.get(arg2 - 1).setDesc("");
                    adp.notifyDataSetChanged();
                }
                drawerLayout.closeDrawers();
               // Log.d("Argument=", "" + arg2);
                launchNext(menuItems.get(arg2 - 1).getIndex(), menuItems.get(arg2 - 1));
            }
        });
    }

    private void setMenuItems() {
        menuItems = new ArrayList<>();
        menuItems.add(new Feed(Const.Menu.GET_AN_AGENT, null, R.drawable.ic_agent_normal,
                R.drawable.ic_agent_selected, 1));
        menuItems.add(new Feed(Const.Menu.SELF_ASSIGNED, null, R.drawable.ic_agent_normal,
                R.drawable.ic_agent_selected, 2));
        menuItems.add(new Feed(Const.Menu.TRACK_AGENT, null, R.drawable.ic_tracking_normal,
                R.drawable.ic_tracking_selected, 3));

        menuItems.add(new Feed(Const.Menu.NOTIFICATIONS, null, R.drawable.notification_gray,
                R.drawable.ic_notification_selected, 19));


        if (!Const.Menu.SHOW_LIMTED_MENU) {
            menuItems.add(new Feed(Const.Menu.TRANSFER_BOND, null, R.drawable.ic_transfer_normal,
                    R.drawable.ic_transfer_selected, 4));
            menuItems.add(new Feed(Const.Menu.INCOMING_TRANSFER_BOND_REQUEST, null,
                    R.drawable.ic_request_normal, R.drawable.ic_request_selected, 5));

            menuItems.add(new Feed(Const.Menu.REFFER_BAIL, null, R.drawable.refer_bail,
                    R.drawable.refer_bail_white, 6));
            menuItems.add(new Feed(Const.Menu.INCOMING_REFFER_BAIL, null,
                    R.drawable.incoming_refer_bail,
                    R.drawable.incoming_refer_bail_white, 7));
        }
        menuItems.add(new Feed(Const.Menu.BLACK_LIST_MEMBER, null, R.drawable.black_grey,
                R.drawable.black_white, 8));
        if (!Const.Menu.SHOW_LIMTED_MENU) {
            menuItems.add(new Feed(Const.Menu.BAD_DEBT_MEMBER, null, R.drawable.bad_grey,
                    R.drawable.bad_white, 9));
            menuItems.add(new Feed(Const.Menu.GET_FUGITIVE_AGENT, null,
                    R.drawable.fugitive_agent_sel, R.drawable.fugitive_agent, 10));
            menuItems.add(new Feed(Const.Menu.SENT_FUGITIVE_REQUEST, null,
                    R.drawable.incoming_fugitive_agent_sel,
                    R.drawable.incoming_fugitive_agent, 11));
        }

        menuItems.add(new Feed(Const.Menu.DEFENDANTS, null, R.drawable.ic_agent_normal,
                R.drawable.ic_agent_selected, 12));
        menuItems.add(new Feed(Const.Menu.CALENDAR, null, R.drawable.ic_calendar_normal,
                R.drawable.ic_calendar_selected, 13));

        menuItems.add(new Feed(Const.Menu.HISTORY, null, R.drawable.history,
                R.drawable.history_white, 14));
        if (!Const.Menu.SHOW_LIMTED_MENU) {
            menuItems.add(new Feed(Const.Menu.INSTANT_CHAT, null, R.drawable.ic_contact_normal,
                    R.drawable.ic_contact_selected, 15));
            menuItems.add(new Feed(Const.Menu.INSTANT_GROUP_CHAT, null,
                    R.drawable.ic_contact_normal, R.drawable.ic_contact_selected, 16));
        }

        menuItems.add(new Feed(Const.Menu.CONTACT_US, null, R.drawable.ic_contact_normal,
                R.drawable.ic_contact_selected, 17));
        menuItems.add(new Feed(Const.Menu.LOGOUT, null, R.drawable.ic_logout_normal,
                R.drawable.ic_logout_selected, 18));

    }

    private void phoneFormat(final TextView ph) {
        int maxLengthofEditText = 14;
        ph.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                maxLengthofEditText)});

        String str = PhoneNumberUtils.formatNumber("+1"
                + ph.getText().toString());
        ph.setText(str.replace("+1-", "1-").replace("+1", "1-"));

        Log.e("ph.getText().toString()", "" + ph.getText().toString());
    }

    /**
     * Setup the right navigation drawer/slider. You can add your logic to load
     * the contents to be displayed on the right side drawer. It will also setup
     * the Header contents of right drawer.
     */

    private void launchNext(int pos, Feed selectedItem) {
        Fragment f = null;
        String title = null;
        switch (pos) {
            case 0:
                break;
            case 1:
                arg = new Bundle();
                arg.putString("companyId", user.getCompanyId());
                f = new GetAnAgent();
                f.setArguments(arg);
                title = getString(R.string.get_agnt);
                break;
            case 2:
                f = new SelfAssigned();
                title = getString(R.string.ass_req);
                break;
            case 3:
                f = new AgentList();
                arg = new Bundle();
                arg.putBoolean("isHired", true);

                f.setArguments(arg);
                break;
            case 4:
                f = new TransferBond();
                title = getString(R.string.trns_bond);
                break;

            case 5:
                f = new IncomingRequest();
                title = getString(R.string.incmng_req2);
                prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("bond", false);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("bond", false).commit();
                WebAccess.tranferBondBadge = prefs.getBoolean("bond", false);
                drawerLeft.setAdapter(adp);
                break;
            case 6:
                f = new ReferBail();
                title = getString(R.string.refer_bail);
                break;
            case 7:
                f = new IncomingBailRequest();
                title = getString(R.string.incoming_refer_bail_request);
                prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = prefs.edit();
                editor2.putBoolean("referrel", false).commit();
                WebAccess.referBailBadge = prefs.getBoolean("referrel", false);
                drawerLeft.setAdapter(adp);
                break;
            case 8:
                f = new BlackListMembers();
                title = getString(R.string.black_list_members);
                break;
            case 9:
                f = new BadDebtMembers();
                title = getString(R.string.bad_debt_members);
                break;
            case 10:
                f = new FugitiveAgent();
                title = getString(R.string.fugitive_agent);
                break;
            case 11:
                f = new IncomingFugitveRequest();
                title = getString(R.string.sent_fugitve_req);
                prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor3 = prefs.edit();
                editor3.putBoolean("fugitive", false).commit();
                WebAccess.fugitiveBadge = prefs.getBoolean("fugitive", false);
                drawerLeft.setAdapter(adp);
                break;

            case 12:
                f = new DefendantList();
                title = getString(R.string.defendants);

                break;
            case 19:
                f = new NotificationListActivity();
                title = getString(R.string.title_activity_notification_list);

                break;
            case 13:
                f = new BailCalendar();
                title = getString(R.string.calendar);
                break;
            case 14:
                f = new History();
                title = getString(R.string.history);
                break;
            case 15:
                f = new InstantChat();
                title = getString(R.string.instant_chat);
                SharedPreferences.Editor editor4 = prefs.edit();
                editor4.putBoolean("instant", false).commit();
                WebAccess.instant = prefs.getBoolean("instant", false);
                drawerLeft.setAdapter(adp);
                break;
            case 16:
                f = new InstantGroupChat();
                title = getString(R.string.inst_group_msg);
                SharedPreferences.Editor editor5 = prefs.edit();
                editor5.putBoolean("instantGroup", false).commit();
                WebAccess.instantGroup = prefs.getBoolean("instantGroup", false);
                drawerLeft.setAdapter(adp);
                break;
            case 17:
                f = new ContactUs();
                title = getString(R.string.contact_us);
                break;
            case 18:
                sp.edit().putBoolean("isFbLogin", false);
                sp.edit().putString("user", null).commit();
                finish();
                break;
            case -1:

                f = new AgentList();
                arg = new Bundle();
                arg.putBoolean("isHired", true);
                f.setArguments(arg);
                break;
            case -3:
                f = new ShowAllOnMap();
                title = getString(R.string.ag_near);
                break;
        }
        if (f != null) {
            while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            }
            if (pos == -1 || pos == 0)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, f).commit();
            else
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, f).addToBackStack(title)
                        .commit();
        }
    }

    /**
     * Setup the container fragment for drawer layout. This method will setup
     * the grid view display of main contents. You can customize this method as
     * per your need to display specific content.
     */
    private void setupContainer() {
        getSupportFragmentManager().addOnBackStackChangedListener(
                new OnBackStackChangedListener() {

                    @Override
                    public void onBackStackChanged() {
                        setActionBarTitle();
                    }
                });
        setupDrawer();
        // launchNext(-1);
    }

    private void setupContainer2() {
        // getSupportFragmentManager().addOnBackStackChangedListener(
        // new OnBackStackChangedListener() {
        //
        // @Override
        // public void onBackStackChanged() {
        // setActionBarTitle();
        // }
        // });

        launchNext(1, menuItems.get(0)); //1
    }

    private void setActionBarTitle() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getActionBar().setTitle(R.string.trck_agnt);
            return;
        }
        String title = getSupportFragmentManager().getBackStackEntryAt(
                getSupportFragmentManager().getBackStackEntryCount() - 1)
                .getName();
        getActionBar().setTitle(
                title == null ? getString(R.string.trck_agnt) : title);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.app.Activity#onConfigurationChanged(android.content.res.Configuration
     * )
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.newsfeeder.custom.CustomActivity#onCreateOptionsMenu(android.view
     * .Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu != null)
            menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */

    /* Called whenever we call invalidateOptionsMenu() */
    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // if(item.getItemId() == R.id.menu_map)
        // launchNext(-3);
        return super.onOptionsItemSelected(item);
    }

    // public void onRequestAccepted(BailRequestModel bailReqMod) {
    // bailReqModel = bailReqMod;
    // launchNext(-1);
    // getActionBar().setTitle(getString(R.string.app_name));
    // }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @SuppressLint("NewApi")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            isOnce = false;
            setupLeftNavDrawer();
            adp.notifyDataSetChanged();
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
                drawerLayout.openDrawer(drawerLeft);
                // setupContainer();
            } else
                finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOnce = false;
        isLoggedIn = false;


        this.unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Utility.checkPermission(this);

        } else {
            Methods.showToast(this, "Permission Denny", Toast.LENGTH_SHORT);
        }

    }


    private int getMenuIndexByName(String name) {
        int returnpos = 1;
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getTitle().equalsIgnoreCase(name)) {
                returnpos = menuItems.get(i).getIndex();
                break;
            }
        }
        return returnpos;
    }

}
