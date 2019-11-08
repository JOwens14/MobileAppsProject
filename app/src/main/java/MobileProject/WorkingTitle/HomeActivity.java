package MobileProject.WorkingTitle;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
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


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration AppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("start home", " you started the home activity");
        setContentView(R.layout.activity_home);

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

}
