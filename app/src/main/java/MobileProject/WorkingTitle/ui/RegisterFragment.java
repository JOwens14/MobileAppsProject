package MobileProject.WorkingTitle.ui;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import MobileProject.WorkingTitle.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private AsyncTask<String, Integer, String> mTask;

    public RegisterFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mTextView = new TextView[3];
//        mTextView[0] = findViewById(R.id.textView);
//        mButton = findViewById(R.id.buttonHelloStatus);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_register, container, false);
        view.findViewById(R.id.button_back_to_Login).setOnClickListener(this::register);

        return view;
    }

    private void validations () {
//        if ()
    }

    private void register(View view) {
        AsyncTask<String, Void, String> task = null;
        //Construct a JSONObject to build a formatted message to send.
        TextView errorText = (TextView)getActivity().findViewById(R.id.errorRegister);
        String firstName = ((EditText)getActivity().findViewById(R.id.editText_FirstName)).getText().toString();
        String lastName = ((EditText)(getActivity().findViewById(R.id.editText_LastName))).getText().toString();
        String userName = ((EditText)getActivity().findViewById(R.id.editText_Username)).getText().toString();
        String password = ((EditText)getActivity().findViewById(R.id.editText_Password)).getText().toString();
        String repassword = ((EditText)getActivity().findViewById(R.id.editText_Password2)).getText().toString();
        String email = ((EditText)getActivity().findViewById(R.id.editText_Email)).getText().toString();

        // TODO: use validation function
        if (!password.equals(repassword)) {
            ((TextView)getActivity().findViewById(R.id.textViewPasswordMatch)).setVisibility(View.VISIBLE);
        }

        if (!emailValidation(email)) {
            errorText.setText("Email is invalid!");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        // TODO: validations

        JSONObject msg = new JSONObject();
        try {
            msg.put("first", firstName);
            msg.put("last", lastName);
            msg.put("username", userName);
            msg.put("email", email);
            msg.put("password", password);
        } catch (JSONException e) {
            //cancel will result in onCanceled not onPostExecute
//            cancel(true);
            Log.wtf("Error with JSON creation:", e.getMessage());
        }
        task = new PostWebServiceTask();
        task.execute(getString(R.string.base_url),
                getString(R.string.conn_register),
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
                TextView resultShow = ((TextView) getActivity().findViewById(R.id.errorRegister));
                if (ob.getBoolean("success")) {
                    resultShow.setText("Register success! Will implement verification later!");

                    Register_Verification_Fragment nextFrag= new Register_Verification_Fragment();

                    Bundle arguments = new Bundle();
                    String email = ((EditText)getActivity().findViewById(R.id.editText_Email)).getText().toString();
                    arguments.putString("email",email);
                    nextFrag.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_resister_fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                } else {
                    resultShow.setText("Email or Nickname have taken!");
                }

                resultShow.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
