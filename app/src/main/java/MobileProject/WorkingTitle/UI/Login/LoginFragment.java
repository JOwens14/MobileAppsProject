package MobileProject.WorkingTitle.UI.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import MobileProject.WorkingTitle.RegisterActivity;
import MobileProject.WorkingTitle.UI.Register.Register_Verification_Fragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MobileProject.WorkingTitle.HomeActivity;
import MobileProject.WorkingTitle.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.login_login_button);
        Button registerButton = view.findViewById(R.id.login_register_button);

        loginButton.setOnClickListener(this::login);
        registerButton.setOnClickListener(this::register);


        return view;
    }

    private void register(View view) {
        Intent intent = new Intent(this.getContext(), RegisterActivity.class);
        startActivity(intent);

    }

    // TODO: must implement login which uses 	Credentials when implement stay login option

    public void login(View view) {
        AsyncTask<String, Void, String> task = null;
        //Construct a JSONObject to build a formatted message to send.
        TextView errorText = (TextView)getActivity().findViewById(R.id.textViewLoginError);
        String email = ((EditText)getActivity().findViewById(R.id.editText_EmailLogin)).getText().toString();
        String password = ((EditText)getActivity().findViewById(R.id.editText_PasswordLogin)).getText().toString();

        if (email.trim().length() <= 5 && !emailValidation(email)) {
            errorText.setText("Email is invalid!");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        JSONObject msg = new JSONObject();
        try {
            msg.put("email", email);
            msg.put("password", password);
        } catch (JSONException e) {
            //cancel will result in onCanceled not onPostExecute
//            cancel(true);
            Log.wtf("Error with JSON creation:", e.getMessage());
        }
        task = new PostWebServiceTask();
        task.execute(getString(R.string.base_url),
                getString(R.string.conn_login),
                msg.toString());
    }

    private boolean emailValidation(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private class PostWebServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            String endPoint = strings[1];
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

                wr.write(strings[2]);
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
            ((TextView) getActivity().findViewById(R.id.errorRegister)).setError(result);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject ob = new JSONObject(result);
                TextView resultShow = ((TextView) getActivity().findViewById(R.id.textViewLoginError));
                if (ob.getBoolean("success")) {
                    resultShow.setText("");
                    loginSuccessHelper();
                } else {
                    resultShow.setText("Email or Password not correct!");
                }

                resultShow.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loginSuccessHelper () {
        Intent intent = new Intent(this.getContext(), HomeActivity.class);
        startActivity(intent);
        Log.d("login", "clicked");
    }
}
