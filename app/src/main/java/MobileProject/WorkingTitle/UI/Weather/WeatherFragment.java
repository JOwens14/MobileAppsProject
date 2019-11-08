package MobileProject.WorkingTitle.UI.Weather;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

import MobileProject.WorkingTitle.R;
import MobileProject.WorkingTitle.UI.Conversations.Conversation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    String CITY = "TACOMA,WA,US";
    String API = "9898def6c58ff9d8ce98771cd6ef8065";
    String UNITS = "imperial";
    String UNIT = "°F";

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;


    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        addressTxt = view.findViewById(R.id.address);
        updated_atTxt = view.findViewById(R.id.updated_at);
        statusTxt = view.findViewById(R.id.status);
        tempTxt = view.findViewById(R.id.temp);
        temp_minTxt = view.findViewById(R.id.temp_min);
        temp_maxTxt = view.findViewById(R.id.temp_max);
        sunriseTxt = view.findViewById(R.id.sunrise);
        sunsetTxt = view.findViewById(R.id.sunset);
        windTxt = view.findViewById(R.id.wind);
        pressureTxt = view.findViewById(R.id.pressure);
        humidityTxt = view.findViewById(R.id.humidity);

        weatherTask Weather = new weatherTask();
        Weather.setView(view);
        Weather.execute();

        ImageButton cityAddButton = (ImageButton) view.findViewById(R.id.city_add_button);
        cityAddButton.setOnClickListener(this::addCity);

        // Inflate the layout for this fragment
        return view;
    }

    private void addCity(View view){
        NavController navController =
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_locations);
    }


    @Override
    public void onViewCreated(View view, Bundle args) {


        //sets the actionbar title to the contact name
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Weather");
        //actionBar.setDisplayHomeAsUpEnabled(false);

        //disables the bottom nav bar while in conversation
//        BottomNavigationView navView = activity.findViewById(R.id.nav_view);
//        navView.setVisibility(view.GONE);

    }

    class weatherTask extends AsyncTask<String, Void, String> {
        View mView;

        public void setView(View view) {
            mView = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            mView.findViewById(R.id.loader).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.mainContainer).setVisibility(View.GONE);
            mView.findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=" + UNITS + "&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            //Log.d("RESPONSE:" , result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + UNIT;
                String tempMin = "Min Temp: " + main.getString("temp_min") + UNIT;
                String tempMax = "Max Temp: " + main.getString("temp_max") + UNIT;
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity") + "%";

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed") + "mph";
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");


                /* Populating extracted data into our views */
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);

                /* Views populated, Hiding the loader, Showing the main design */
                mView.findViewById(R.id.loader).setVisibility(View.GONE);
                mView.findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                Log.d("ERROR: ", e.toString());
                mView.findViewById(R.id.loader).setVisibility(View.GONE);
                mView.findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }




}








