package MobileProject.WorkingTitle.UI.Weather;


import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

import MobileProject.WorkingTitle.R;
import MobileProject.WorkingTitle.UI.Conversations.Conversation;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationsFragment extends Fragment {


    public LocationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle args) {
        //sets the actionbar title to the contact name
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Locations");

        //disables the bottom nav bar while in conversation
        BottomNavigationView navView = activity.findViewById(R.id.nav_view);
        navView.setVisibility(view.GONE);

    }

}
