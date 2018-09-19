package com.team.lezomadetana.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.team.lezomadetana.BuildConfig;
import com.team.lezomadetana.R;
import com.team.lezomadetana.api.Client;
import com.team.lezomadetana.api.Service;
import com.team.lezomadetana.fragment.FragmentChat;
import com.team.lezomadetana.fragment.FragmentHome;
import com.team.lezomadetana.fragment.FragmentListOffer;
import com.team.lezomadetana.fragment.FragmentPayment;
import com.team.lezomadetana.fragment.FragmentPaymentCharge;
import com.team.lezomadetana.fragment.FragmentSetting;
import com.team.lezomadetana.model.receive.User;
import com.team.lezomadetana.model.receive.UserCredentialResponse;
import com.team.lezomadetana.utils.CircleTransform;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RaThierry on 28/08/2018.
 **/

public class MainActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Toolbar toolbar;
    public DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navView;
    private TextView textViewName;
    private TextView textViewInfo;
    private ImageView imageViewBg;
    private ImageView imageViewProfile;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    public boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // instance handler
        mHandler = new Handler();

        // set view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navView = navigationView.getHeaderView(0);
        textViewName = (TextView) navView.findViewById(R.id.name);
        textViewInfo = (TextView) navView.findViewById(R.id.website);
        imageViewBg = (ImageView) navView.findViewById(R.id.img_header_bg);
        imageViewProfile = (ImageView) navView.findViewById(R.id.img_profile);

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserUpdateInfoActivity.class));
            }
        });

        // set toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load navigation menu header data
        loadNavigationHeader();

        // initializing navigation menu
        initNavigationMenu();

        // show user image
        showAvatarImage();

        // instance'state
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_BUSINESS;
            loadDefaultFragment();
        }


    }

    private void showAvatarImage() {
        Service api = Client.getClient(BaseActivity.ROOT_MDZ_USER_API).create(Service.class);

        // create basic authentication
        String auth = BasicAuth();

        // get user
        final UserCredentialResponse cUser = getCurrentUser(this);

        // send query
        Call<JsonObject> call = api.getUserById(auth, cUser.getId());
        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    JsonObject jUser = response.body().getAsJsonObject();
                    User user = new Gson().fromJson(jUser, User.class);
                    Log.d("pouaaa", user.toString());

                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty() && !TextUtils.isEmpty(user.getProfileImageUrl())) {
                        applyProfilePicture(imageViewProfile, user.getProfileImageUrl());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        // this code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex == 1 || navItemIndex == 2 || navItemIndex == 4 || navItemIndex == 5) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_BUSINESS;
                loadDefaultFragment();
                return;
            } else if (navItemIndex == 3) {
                // back to payment fragment
                navItemIndex = 2;
                CURRENT_TAG = TAG_PAYMENT;
                loadDefaultFragment();
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        if (navItemIndex == 0 || navItemIndex == 1 || navItemIndex == 2 || navItemIndex == 3 || navItemIndex == 4 || navItemIndex == 5) {
            getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemMenuSearch = menu.findItem(R.id.action_search);
        MenuItem itemMenuPayment = menu.findItem(R.id.action_payment);
        MenuItem itemMenuInfo = menu.findItem(R.id.action_information);
        if (navItemIndex == 0 || navItemIndex == 1 || navItemIndex == 2 || navItemIndex == 3 || navItemIndex == 4 || navItemIndex == 5) {
            itemMenuSearch.setEnabled(false);
            itemMenuSearch.setVisible(false);
            itemMenuPayment.setEnabled(false);
            itemMenuPayment.setVisible(false);
            itemMenuInfo.setEnabled(false);
            itemMenuInfo.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                //showLongToast(MainActivity.this, "item SEARCH");
                break;
            case R.id.action_payment:
                //showLongToast(MainActivity.this, "item PAYMENT");
                break;
            case R.id.action_information:
                showLongToast(MainActivity.this, getResources().getString(R.string.activity_main_menu_info));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        showLongToast(_context, "0) requestCode: " + requestCode +
                "\nresultCode: " + resultCode + "\ndata: " + data);

        // result
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // verification
        if (result != null) {
            if (result.getContents() == null) {
                showLongToast(_context, "1) result.getContents(): " + result.getContents());
            } else {
                showLongToast(_context, "2) result.getContents(): " + result.getContents());
                // save user id
                SharedPreferences.Editor editor = sharedPrefForPaymentSendMoney.edit();
                editor.putString(PREFS_KEY_ID_USER_FROM_SCAN, result.getContents());
                editor.commit();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // use this to call PaymentFragment
    public void launchPaymentFragment() {
        navItemIndex = 2;
        CURRENT_TAG = TAG_PAYMENT;
        loadDefaultFragment();
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * Show long toast
     */
    public void showLongToast(Context context, String message) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website (or another info)
     */
    private void loadNavigationHeader() {
        // set username (phone number) and another info (region) correct values
        textViewName.setText(getCurrentUser(MainActivity.this).getName());
        textViewInfo.setText(getCurrentUser(MainActivity.this).getUsername());

        // loading background image
        Glide.with(this).load(getImage("nav_menu_header_bg"))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewBg);

        // loading profile image
        Glide.with(this).load(getImage("ic_account_circle_white"))
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewProfile);
    }

    /***
     * Initialize navigation menu
     */
    private void initNavigationMenu() {
        // setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // this method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    // replacing the menu_main_activity content with ContentFragment Which is our Inbox View;
                    case R.id.nav_business:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_BUSINESS;
                        break;
                    case R.id.nav_offer:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_OFFER;
                        break;
                    case R.id.nav_payment:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_PAYMENT;
                        break;
                    case R.id.nav_payment_menu:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PAYMENT_MENU;
                        break;
                    case R.id.nav_chat:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_CHAT;
                        break;
                    case R.id.nav_setting:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SETTING;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_logOut:
                        drawerLayout.closeDrawers();
                        logoutUser();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                // checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                // load default fragment
                loadDefaultFragment();
                return true;
            }
        });

        // action open/close drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        // setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadDefaultFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawerLayout.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the menu_main_activity content by replacing fragments
                Fragment fragment = getDefaultFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // if mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // close drawer on item click
        drawerLayout.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    // select navigation's menu
    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    // set toolBar title
    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    // get default fragment
    private Fragment getDefaultFragment() {
        switch (navItemIndex) {
            case 0:
                FragmentHome business = new FragmentHome();
                return business;
            case 1:
                FragmentListOffer offer = new FragmentListOffer();
                return offer;
            case 2:
                FragmentPayment payment = new FragmentPayment();
                return payment;
            case 3:
                FragmentPaymentCharge charge = new FragmentPaymentCharge();
                return charge;
            case 4:
                FragmentChat chat = new FragmentChat();
                return chat;
            case 5:
                FragmentSetting setting = new FragmentSetting();
                return setting;
            default:
                return new FragmentHome();
        }
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
