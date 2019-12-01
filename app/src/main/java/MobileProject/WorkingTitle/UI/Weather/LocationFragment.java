package MobileProject.WorkingTitle.UI.Weather;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import MobileProject.WorkingTitle.HomeActivity;
import MobileProject.WorkingTitle.R;



/**
 * A fragment representing a list of Items.
 * <p/>

 * interface.
 */
public class LocationFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LocationFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LocationFragment newInstance(int columnCount) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);


        //sets the actionbar title to the contact name
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Locations");


        //disables the bottom nav bar
        BottomNavigationView navView = activity.findViewById(R.id.nav_view);
        navView.setVisibility(view.GONE);


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this::showAddItemDialog);


        //Log.d("location list: ", Locations.LOCATIONS.toString());

        // Set the adapter
        if (view.findViewById(R.id.list) != null) {
            Context context = view.getContext();
            RecyclerView recyclerView = view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyLocationRecyclerViewAdapter(Locations.LOCATIONS, this::onClick));
        }
        return view;
    }


    //changes the current location and then loads the weather screen again
    private void onClick(final Locations.Location l) {
        WeatherFragment.setCity(l.location);
        NavController navController =
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.nav_weather);

    }


    private void showAddItemDialog(View view) {
        Context c = view.getContext();
        final EditText cityEditText = new EditText(c);
        cityEditText.setHint("City");
        final EditText stateEditText = new EditText(c);
        stateEditText.setHint("State");

        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(cityEditText);
        layout.addView(stateEditText);


        AlertDialog dialog = new AlertDialog.Builder(c, R.style.AlertDialog)
                .setTitle("New Location")
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String city = String.valueOf(cityEditText.getText());
                        String state = String.valueOf(stateEditText.getText());

                        //adds item to the location list.
                        //ONLY WORKS FOR WA CITIES SO FAR
                        Locations.addLocation(new Locations.Location(city + ","+ state +",US"));

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Locations.Location loco);
    }
}
