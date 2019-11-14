package MobileProject.WorkingTitle;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import MobileProject.WorkingTitle.model.Credentials;
import MobileProject.WorkingTitle.utils.PushReceiver;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import MobileProject.WorkingTitle.UI.Conversations.Conversation;
import MobileProject.WorkingTitle.UI.Conversations.ConversationFragment;
import MobileProject.WorkingTitle.UI.Login.LoginActivity;
import me.pushy.sdk.Pushy;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration AppBarConfiguration;
    private ColorFilter mDefault;
    private HomePushMessageReceiver mPushMessageReciever;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mDefault = ((Toolbar) findViewById(R.id.toolbar)).getNavigationIcon().getColorFilter();
        Pushy.listen(this);
        //Log.d("start home", " you started the home activity");
        setContentView(R.layout.activity_home);

        //Hide the notification icon whenever home activity loaded..
        ((BottomNavigationView)findViewById(R.id.nav_view)).getMenu().getItem(3).setVisible(false);
        ((BottomNavigationView)findViewById(R.id.nav_view)).setItemIconTintList(null);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("userCr")) {
                Credentials cr = (Credentials) getIntent().getExtras().get("userCr");
                Log.d("LOGIN_USER", "JWT Token: " + cr.getJwtToken());
            }
            if (getIntent().getExtras().containsKey("type")) {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .setGraph(R.navigation.mobile_navigation, getIntent().getExtras());
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




    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new HomePushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReciever, iFilter);
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

                if (intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {


                    String sender = intent.getStringExtra("SENDER");
                    String messageText = intent.getStringExtra("MESSAGE");

                    // TODO: Update/display notification icon when received messages.
                    BottomNavigationView navView = findViewById(R.id.nav_view);
                    navView.getMenu().getItem(3).setVisible(true);
                    Log.d("HOME", sender + ": " + messageText);
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



}
