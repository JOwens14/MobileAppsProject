package MobileProject.WorkingTitle.UI.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import MobileProject.WorkingTitle.UI.Register.RegisterActivity;

import MobileProject.WorkingTitle.model.ChatMessageNotification;
import MobileProject.WorkingTitle.model.Credentials;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import me.pushy.sdk.Pushy;

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
    private int attamped;


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
    public void onStart() {
        super.onStart();
        attamped = 0;
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {

            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.editText_EmailLogin);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.editText_PasswordLogin);
            passwordEdit.setText(password);
            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());
        }
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

        TextView login = view.findViewById(R.id.editText_EmailLogin);
        TextView password = view.findViewById(R.id.editText_PasswordLogin);

        login.setText("fakeemail@gmail.com");
        password.setText("logintest123");

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

        if (!emailValidation(email)) {
            errorText.setText("Email is invalid!");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        if (!passwordValidations(password)) {
            ((EditText)getActivity().findViewById(R.id.editText_PasswordLogin))
                    .setError("Password must at least eight characters, at least one letter and number!");
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
                getString(R.string.ep_pushy),
                msg.toString());
    }



    private class PostWebServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //get pushy token
            String deviceToken;


            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getActivity().getApplicationContext());

                //subscribe to a topic (this is a Blocking call)
                Pushy.subscribe("all", getActivity().getApplicationContext());
            }
            catch (Exception exc) {

                cancel(true);
                // Return exc to onCancelled
                return exc.getMessage();
            }

            //feel free to remove later.
            Log.d("LOGIN", "Pushy Token: " + deviceToken);

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
                jsonObj.put("token", deviceToken);
                ob = jsonObj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //build the url
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(url)
                    .appendPath("login")
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
            ((TextView) getActivity().findViewById(R.id.textViewLoginError)).setError(result);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject ob = new JSONObject(result);
                TextView resultShow = ((TextView) getActivity().findViewById(R.id.textViewLoginError));
                if (ob.getBoolean("success")) {
                    resultShow.setText("");
                    //feel free to remove later.
//                    Log.d("LOGIN_PUSHY", "Pushy Token: " + ob.getString("token"));
//                    Log.d("LOGIN_PUSHY", "JWT Token: " + ob.getString("jwtToken"));
                    loginSuccessHelper(ob.getString("firstname"),ob.getString("lastname"),
                            ob.getString("username"), ob.getString("jwtToken"), ob.getString("memberid"), ob.getString("token"));
                } else {
                    attamped++;
                    if (attamped <= 3)
                        resultShow.setText("Email or Password not correct!");
                    else {
                        resultShow.setText("Forgot password? Please Click here!");
                        forgotPassword();
                    }
                }

                resultShow.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void forgotPassword() {
        //TODO: implement forgot password fragment.
    }


    private void loginSuccessHelper (String firstname, String lastname, String username, String jwtToken, String memberid, String token) {
        String email = ((EditText)getActivity().findViewById(R.id.editText_EmailLogin)).getText().toString();
        String password = ((EditText)getActivity().findViewById(R.id.editText_PasswordLogin)).getText().toString();
        Credentials cr = new Credentials.Builder(email, password)
                .addFirstName(firstname)
                .addLastName(lastname)
                .addUsername(username)
                .addJwtToken(jwtToken)
                .addMemberid(memberid)
                .addDeviceToken(token)
                .build();
        if (((CheckBox)getActivity().findViewById(R.id.login_checkBox)).isChecked()) {
            saveCredentials(cr);
        }

        Intent mIntent = new Intent(this.getContext(), HomeActivity.class);
        mIntent.putExtra("userCr", cr);


        if (getArguments() != null) {

            if (getArguments().containsKey("type")) {
                if (getArguments().getString("type").equals("msg")) {
                    String msg = getArguments().getString("message");
                    String sender = getArguments().getString("sender");

                    ChatMessageNotification chat =
                            new ChatMessageNotification.Builder(sender, msg).build();
                    mIntent.putExtra("chatMessage", chat);
                }
            }

            // Friend request
            if (getArguments().containsKey("type")) {
                if (getArguments().getString("type").equals("friendRequest")) {
                    String msg = getArguments().getString("message");
                    String sender = getArguments().getString("sender");

                    ChatMessageNotification friend =
                            new ChatMessageNotification.Builder(sender, msg).build();
                    mIntent.putExtra("friendRequest", friend);
                }
            }
        }


        startActivity(mIntent);
        Log.d("login", "clicked");
    }

    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }

    private Boolean passwordValidations (String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean emailValidation(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void doLogin(Credentials credentials) {
        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();

        AsyncTask<String, Void, String> task;

        task = new PostWebServiceTask();
        task.execute(getString(R.string.base_url),
                getString(R.string.ep_pushy),
                msg.toString());
    }

}
