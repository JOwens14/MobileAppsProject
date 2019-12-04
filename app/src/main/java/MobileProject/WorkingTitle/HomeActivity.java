package MobileProject.WorkingTitle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import MobileProject.WorkingTitle.model.Contacts;
import MobileProject.WorkingTitle.model.Credentials;
import MobileProject.WorkingTitle.model.EnumsDefine;
import MobileProject.WorkingTitle.utils.PushReceiver;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationView;

import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

import MobileProject.WorkingTitle.UI.Conversations.Conversation;
import MobileProject.WorkingTitle.UI.Conversations.ConversationFragment;
import MobileProject.WorkingTitle.UI.Login.LoginActivity;
import me.pushy.sdk.Pushy;





public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration AppBarConfiguration;
    private ColorFilter mDefault;
    private HomePushMessageReceiver mPushMessageReciever;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    public static double LAT = 0;
    public static double LONG = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mDefault = ((Toolbar) findViewById(R.id.toolbar)).getNavigationIcon().getColorFilter();
        Pushy.listen(this);
        //Log.d("start home", " you started the home activity");
        setContentView(R.layout.activity_home);

        //Hide the notification icon whenever home activity loaded..
        //((BottomNavigationView)findViewById(R.id.nav_view)).getMenu().getItem(3).setVisible(false);
        ((BottomNavigationView)findViewById(R.id.nav_view)).setItemIconTintList(null);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("userCr")) {
                Credentials cr = (Credentials) getIntent().getExtras().get("userCr");
                //Log.d("LOGIN_USER", "JWT Token: " + cr.getJwtToken());
                //Log.d("LOGIN_USER", "JWT Token: " + cr.getJwtToken());
                //Load contacts first time.
                //TODO reload when new friend added.
                if (!getIntent().getExtras().containsKey("contacts")) {
                    loadContacts(cr.getMemberId());
                }
            }
            if (getIntent().getExtras().containsKey("type")) {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .setGraph(R.navigation.mobile_navigation, getIntent().getExtras());
            }

            // Display friend request alert
            if (getIntent().getExtras().containsKey("type")) {
                if (getIntent().getExtras().getString("type").equals("friendRequest")) {
                    BottomNavigationView navView = findViewById(R.id.nav_view);
                    navView.getMenu().getItem(1).setIcon(R.drawable.ic_chat_red_24dp);
                    navView.setItemIconTintList(null);
                }
            }


//            if(getIntent().getExtras().containsKey("chatMessage")) {
//                Navigation.findNavController(this, R.id.nav_host_fragment)
//                        .setGraph(R.navigation.mobile_navigation, getIntent().getExtras());
                //TODO: getmessage then open fragment_conversation.
//                if (args.getChatMessage() != null) {
//                    MobileNavigationDirections.ActionGlobalNavChat directions =
//                            MobileNavigationDirections.actionGlobalNavChat(args.getJwt(),
//                                    args.getCredentials().getEmail() );
//                    directions.setMessage(args.getChatMessage());
//                    navController.navigate(directions);
//                } else {
//                    navigationView.setNavigationItemSelectedListener(this::onNavigationSelected);
//                }

//            }
        }


        //takes away the back button on the top actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);



        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //navs to stuff
        AppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_conversationList, R.id.nav_connections, R.id.nav_weather)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.mobile_navigation, getIntent().getExtras());
        NavigationUI.setupActionBarWithNavController(this, navController, AppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //listener for the nav menu, needed for logout to work, (for other classes to use this, must use the implement)
        navView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();


        //this adjust the icon size for bottom nav menu
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(com.google.android.material.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, displayMetrics);
            // set width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, displayMetrics);
            //hacky crap fix for middle icon being a wierd size for some reason, its still bad
            if (i == 1) {
                // set height here
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics);
                // set width here
                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics);
            }
            iconView.setLayoutParams(layoutParams);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new HomePushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReciever, iFilter);

        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null){
            unregisterReceiver(mPushMessageReciever);
        }
    }



    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //if in a conversation fragment or settings fragment pressing back will return you to the conversation list, otherwise nothing will happen
        if (Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId() == R.id.nav_conversation ||
                Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId() == R.id.nav_settings) {
                    navController.navigate(R.id.nav_conversationList, getIntent().getExtras());
        }
        if (Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId() == R.id.nav_locations) {
            navController.navigate(R.id.nav_weather, getIntent().getExtras());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }


    //TOP RIGHT ACTION BAR MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                navController.navigate(R.id.nav_settings, getIntent().getExtras());
                return true;

            case R.id.action_logout:
                // logout
                logout();
                return true;

            case android.R.id.home:
                //action bar back pressed
                if (Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId() == R.id.nav_conversation) {
                    navController.navigate(R.id.nav_conversationList, getIntent().getExtras());
                } else {
                    return super.onOptionsItemSelected(item);
                    }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, AppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        //Log.d("SELECTED", String.valueOf(item.getItemId()));
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            //Home selected
            case R.id.nav_conversationList: {
                navController.navigate(R.id.nav_conversationList, getIntent().getExtras());
                break;
            }
            //Connections selected
            case R.id.nav_connections: {
                //((Toolbar) findViewById(R.id.toolbar)).getNavigationIcon().setColorFilter(mDefault);
                navController.navigate(R.id.nav_connections, getIntent().getExtras());
                break;
            }
            //logout selected, log out
            case R.id.nav_weather: {
                navController.navigate(R.id.nav_weather, getIntent().getExtras());
                break;
            }
        }
        return false;
    }

    //logs the user out
    private int logout() {
        new DeleteTokenAsyncTask().execute();
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

        //close the app
        finishAndRemoveTask();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //might need to null future credentials for security purposes
        return 1;
    }

    // Deleting the Pushy device token must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

            //unregister the device from the Pushy servers
            Pushy.unregister(HomeActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();

            //or close this activity and bring back the Login
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//            //Ends this Activity and removes it from the Activity back stack.
//            finish();
        }
    }

    private void loadContacts(String memberId) {
        AsyncTask<String, Void, String> task = null;
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", memberId);
        } catch (JSONException e) {
            //cancel will result in onCanceled not onPostExecute
//            cancel(true);
            Log.wtf("Error with JSON creation:", e.getMessage());
        }
        task = new PostWebServiceTask();
        task.execute(getString(R.string.base_url),
                getString(R.string.contacts),
                msg.toString());
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class HomePushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            NavController nc =
                    Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();
            if (nd.getId() != R.id.mobile_navigation) {
                if (intent.getExtras().getString("type").equals("friendRequest")) {
                    Contacts contacts = (Contacts)intent.getExtras().get("contacts");
                    Contacts.Contact newContact = new Contacts.Contact("",intent.getStringExtra("SENDER"),intent.getStringExtra("MESSAGE"));
                    newContact.setStatus(EnumsDefine.Status.FriendRequestFrom);
                    contacts.addItem(newContact);
                    getIntent().removeExtra("contacts");
                    intent.putExtra("contacts",contacts);
                    BottomNavigationView navView = findViewById(R.id.nav_view);
                    navView.getMenu().getItem(1).setIcon(R.drawable.ic_chat_red_24dp);
                    navView.setItemIconTintList(null);
                }

                if (intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE") && intent.getExtras().getString("type").equals("msg")) {

                    String sender = intent.getStringExtra("SENDER");
                    String messageText = intent.getStringExtra("MESSAGE");

                    // TODO: Update/display notification icon when received messages.
                    BottomNavigationView navView = findViewById(R.id.nav_view);
                    navView.getMenu().getItem(3).setVisible(true);
                    //Log.d("HOME", sender + ": " + messageText);
                    navView.setItemIconTintList(null);
                }
            }
        }
        /** Update the color of a specific menu item to the given color. */
        private void updateMenuItemIconColor(Menu menu, int itemId, int color) {
            MenuItem menuItem = menu.findItem(itemId);
            if (menuItem != null) {
                Drawable menuItemIcon = menuItem.getIcon();
                if (menuItemIcon != null) {
                    try {
                        menuItemIcon.mutate();
                        menuItemIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        menuItem.setIcon(menuItemIcon);
                    } catch (Exception e) {
                        Log.w("AIC", "Failed to update menu item color", e);
                    }
                }
            }
        }
    }



    private class PostWebServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //get pushy token
            String deviceToken;

            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            String endPoint = strings[1];
            JSONObject ob = new JSONObject();
            try {
                JSONObject jsonObj = new JSONObject(strings[2]);
                ob = jsonObj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //build the url
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(url)
                    .appendPath(endPoint)
                    .build();
            try {
                URL urlObject = new URL(uri.toString());
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr =
                        new OutputStreamWriter(urlConnection.getOutputStream());

                wr.write(ob.toString());
                wr.flush();
                wr.close();

                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            } catch (Exception e) {
                //cancel will result in onCanceled not onPostExecute
                cancel(true);
                return "Unable to connect, Reason: " + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return response;
        }


        @Override
        protected void onCancelled(String result) {
            super.onCancelled(result);
            Log.w("GET_CONTACT_ERROR", "Failed to get contact");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject ob = new JSONObject(result);
                if (ob.getBoolean("success")) {
                    saveContacts(ob);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    protected void saveContacts(JSONObject result) {
        Contacts contacts = new Contacts();
        //TODO should get contact then add to contacts
        JSONArray arr = null;
        JSONArray contactDetail = null;
        try {
            arr = result.getJSONArray("data");
            contactDetail = result.getJSONArray("contactsDetail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String,String> requestA_To_B = new HashMap<>();
        HashMap<String,String> requestB_To_A = new HashMap<>();
        HashMap<String,String> contactsStatus = new HashMap<>();
        Credentials cr = (Credentials) getIntent().getExtras().get("userCr");
        for (int j = 0; j < contactDetail.length(); j++) {
            try {
                String memberId_A = contactDetail.getJSONObject(j).getString("memberid_a");
                String memberId_B = contactDetail.getJSONObject(j).getString("memberid_b");
                String verified = contactDetail.getJSONObject(j).getString("verified");
                String status = contactDetail.getJSONObject(j).getString("status");


            if (memberId_A.equals(cr.getMemberId())) {
                requestA_To_B.put(memberId_B,verified);
            } else {
                requestB_To_A.put(memberId_A,verified);
            }
            } catch (JSONException es) {}
        }
        for (int i = 0; i < arr.length(); i++)
        {
            try {
                EnumsDefine.Status friend = EnumsDefine.Status.Connected;
                String memberId = arr.getJSONObject(i).getString("memberid");
                String username = arr.getJSONObject(i).getString("username");
                String email = arr.getJSONObject(i).getString("email");
                String token = arr.getJSONObject(i).getString("token");
                Contacts.Contact testing = new Contacts.Contact("", username,email);
                testing.setEmail(email);
                testing.setUsername(username);
                testing.setToken(token);
                testing.setMemberid(memberId);
                if(requestA_To_B.containsKey(memberId)){
                    friend = requestA_To_B.get(memberId).equals("1")?EnumsDefine.Status.Connected: EnumsDefine.Status.FriendRequestTo;
                } else {
                    friend = requestB_To_A.get(memberId).equals("1")?EnumsDefine.Status.Connected: EnumsDefine.Status.FriendRequestFrom;
                }
                testing.setStatus(friend);
                contacts.sortFriend();
                contacts.addItem(testing);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        getIntent().putExtra("contacts", contacts);
    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
//                                    Log.d("made it", "setting long and lat");
//                                    Log.d("tagINSIDE LONG",  LONG);
//                                    Log.d("tagINSIDE LAT",  LAT);
                                    LAT = (location.getLatitude());
                                    LONG = (location.getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            LAT = (mLastLocation.getLatitude());
            LONG = (mLastLocation.getLongitude());
        }
    };

    public static double getLAT() {
        return LAT;
    }

    public static double getLONG() {
        return LONG;
    }

}
