package MobileProject.WorkingTitle.UI.Weather;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import MobileProject.WorkingTitle.R;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.androdocs.httprequest.HttpRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    static String CITY = "TACOMA,WA,US";
    String API = "9898def6c58ff9d8ce98771cd6ef8065";
    String UNITS = "imperial";
    String UNIT = "°F";
    String NUMBER_OF_DAYS = "5";

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;

    TextView day1DescTxt, day1MaxTxt, day1MinTxt, day1DateTxt, day2DescTxt,
            day2MaxTxt, day2MinTxt,day2DateTxt, day3DescTxt, day3MaxTxt, day3MinTxt, day3DateTxt,
            day4DescTxt, day4MaxTxt, day4MinTxt, day4DateTxt, day5DescTxt, day5MaxTxt,
            day5MinTxt, day5DateTxt;

    ImageView day1Image, day2Image, day3Image, day4Image, day5Image;

    public WeatherFragment() {
        // Required empty public constructor
    }
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

        day1Image = view.findViewById(R.id.day1Image);
        day1DescTxt = view.findViewById(R.id.day1Desc);
        day1MaxTxt = view.findViewById(R.id.day1Max);
        day1MinTxt = view.findViewById(R.id.day1Min);
        day1DateTxt = view.findViewById(R.id.day1Date);

        day2Image = view.findViewById(R.id.day2Image);
        day2DescTxt = view.findViewById(R.id.day2Desc);
        day2MaxTxt = view.findViewById(R.id.day2Max);
        day2MinTxt = view.findViewById(R.id.day2Min);
        day2DateTxt = view.findViewById(R.id.day2Date);

        day3Image = view.findViewById(R.id.day3Image);
        day3DescTxt = view.findViewById(R.id.day3Desc);
        day3MaxTxt = view.findViewById(R.id.day3Max);
        day3MinTxt = view.findViewById(R.id.day3Min);
        day3DateTxt = view.findViewById(R.id.day3Date);

        day4Image = view.findViewById(R.id.day4Image);
        day4DescTxt = view.findViewById(R.id.day4Desc);
        day4MaxTxt = view.findViewById(R.id.day4Max);
        day4MinTxt = view.findViewById(R.id.day4Min);
        day4DateTxt = view.findViewById(R.id.day4Date);

        day5Image = view.findViewById(R.id.day5Image);
        day5DescTxt = view.findViewById(R.id.day5Desc);
        day5MaxTxt = view.findViewById(R.id.day5Max);
        day5MinTxt = view.findViewById(R.id.day5Min);
        day5DateTxt = view.findViewById(R.id.day5Date);


        //Main weather
        weatherTask Weather = new weatherTask();
        Weather.setView(view);
        Weather.execute();

        //Extended Forecast
        ForecastTask forecast = new ForecastTask();
        forecast.setView(view);
        forecast.execute();

        ImageButton cityAddButton = view.findViewById(R.id.city_add_button);
        cityAddButton.setOnClickListener(this::addCity);

        // Inflate the layout for this fragment
        return view;
    }

    private void addCity(View view){
        NavController navController =
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_locations);
    }

    public static void setCity(String location) {
        CITY = location;
    }


    @Override
    public void onViewCreated(View view, Bundle args) {


        //sets the actionbar title to the contact name
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Weather");
        //actionBar.setDisplayHomeAsUpEnabled(false);

        //disables the bottom nav bar while in conversation
        BottomNavigationView navView = activity.findViewById(R.id.nav_view);
        navView.setVisibility(view.VISIBLE);

    }


    class ForecastTask extends AsyncTask<String, Void, String> {
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
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/forecast?q=" + CITY + "&mode=json&units=" +
                    UNITS + "&appid=" + API);
            return response;


        }

        @Override
        protected void onPostExecute(String result) {

            //Log.d("RESPONSE:" , result);
            try {
                JSONObject jsonObj = new JSONObject(result);

                //
                //get the 5-day forecast
                //

                //weather comes in 3 hour increments, 24/3 = 8, each day is 8 indexes
                JSONObject day1 = jsonObj.getJSONArray("list").getJSONObject(0);
                JSONObject day2 = jsonObj.getJSONArray("list").getJSONObject(7);
                JSONObject day3 = jsonObj.getJSONArray("list").getJSONObject(15);
                JSONObject day4 = jsonObj.getJSONArray("list").getJSONObject(23);
                JSONObject day5 = jsonObj.getJSONArray("list").getJSONObject(31);

                //Log.d("day 1", day1.toString());
                JSONObject day1Main = day1.getJSONObject("main");
                String day1maxTemp = day1Main.getString("temp_max");
                String day1minTemp = day1Main.getString("temp_min");
                JSONObject day1Weather = day1.getJSONArray("weather").getJSONObject(0);
                String day1WeatherDescription = day1Weather.getString("main");
                String date1 = day1.getString("dt_txt");

                JSONObject day2Main = day2.getJSONObject("main");
                String day2maxTemp = day2Main.getString("temp_max");
                String day2minTemp = day2Main.getString("temp_min");
                JSONObject day2Weather = day2.getJSONArray("weather").getJSONObject(0);
                String day2WeatherDescription = day2Weather.getString("main");
                String date2 = day2.getString("dt_txt");

                JSONObject day3Main = day3.getJSONObject("main");
                String day3maxTemp = day3Main.getString("temp_max");
                String day3minTemp = day3Main.getString("temp_min");
                JSONObject day3Weather = day3.getJSONArray("weather").getJSONObject(0);
                String day3WeatherDescription = day3Weather.getString("main");
                String date3 = day3.getString("dt_txt");

                JSONObject day4Main = day4.getJSONObject("main");
                String day4maxTemp = day4Main.getString("temp_max");
                String day4minTemp = day4Main.getString("temp_min");
                JSONObject day4Weather = day4.getJSONArray("weather").getJSONObject(0);
                String day4WeatherDescription = day4Weather.getString("main");
                String date4 = day4.getString("dt_txt");

                JSONObject day5Main = day5.getJSONObject("main");
                String day5maxTemp = day5Main.getString("temp_max");
                String day5minTemp = day5Main.getString("temp_min");
                JSONObject day5Weather = day5.getJSONArray("weather").getJSONObject(0);
                String day5WeatherDescription = day5Weather.getString("main");
                String date5 = day5.getString("dt_txt");

                //
                //set the info to the display
                //

                setWeatherImage(day1Image, day1WeatherDescription);
                day1DescTxt.setText(day1WeatherDescription);
                day1MaxTxt.setText("Max: " + day1maxTemp + UNIT);
                day1MinTxt.setText("Min: " + day1minTemp + UNIT);
                day1DateTxt.setText(date1.substring(5,10) + "-" + date1.substring(2,4));

                setWeatherImage(day2Image, day2WeatherDescription);
                day2DescTxt.setText(day2WeatherDescription);
                day2MaxTxt.setText("Max: " + day2maxTemp + UNIT);
                day2MinTxt.setText("Min: " + day2minTemp + UNIT);
                day2DateTxt.setText(date2.substring(5,10) + "-" + date2.substring(2,4));

                setWeatherImage(day3Image, day3WeatherDescription);
                day3DescTxt.setText(day3WeatherDescription);
                day3MaxTxt.setText("Max: " + day3maxTemp + UNIT);
                day3MinTxt.setText("Min: " + day3minTemp + UNIT);
                day3DateTxt.setText(date3.substring(5,10) + "-" + date3.substring(2,4));

                setWeatherImage(day4Image, day4WeatherDescription);
                day4DescTxt.setText(day4WeatherDescription);
                day4MaxTxt.setText("Max: " + day4maxTemp + UNIT);
                day4MinTxt.setText("Min: " + day4minTemp + UNIT);
                day4DateTxt.setText(date4.substring(5,10) + "-" + date4.substring(2,4));

                setWeatherImage(day5Image, day5WeatherDescription);
                day5DescTxt.setText(day5WeatherDescription);
                day5MaxTxt.setText("Max: " + day5maxTemp + UNIT);
                day5MinTxt.setText("Min: " + day5minTemp + UNIT);
                day5DateTxt.setText(date5.substring(5,10) + "-" + date5.substring(2,4));


                /* Views populated, Hiding the loader, Showing the main design */
                mView.findViewById(R.id.loader).setVisibility(View.GONE);
                mView.findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                Log.d("ERROR: ", e.toString());
                mView.findViewById(R.id.loader).setVisibility(View.GONE);
                mView.findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }

        private void setWeatherImage(ImageView img, String desc) {
            switch (desc) {
                case("Clear"):
                    img.setImageResource(R.drawable.clearimage);
                    break;
                case("Clouds"):
                    img.setImageResource(R.drawable.cloudyimage);
                    break;
                case("Rain"):
                    img.setImageResource(R.drawable.rainimage);
                    break;
            }
        }
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

            //Log.d("WEATHER RESPONSE:" , result);
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








