package MobileProject.WorkingTitle.UI.NewPassword;


import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MobileProject.WorkingTitle.R;

import static android.view.PointerIcon.TYPE_NULL;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPasswordFragment extends Fragment {

    private String mCode;
    private String mEmail;
    private Boolean emailSent;
    private Boolean codeSent;
    private Boolean passwordCorrect;
    private AsyncTask<String, Integer, String> mTask;

    public NewPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_password, container, false);
        view.findViewById(R.id.newPW_email_button).setOnClickListener(this::sendEmail);
        view.findViewById(R.id.newPW_codeVerify_button).setOnClickListener(this::verifyCode);
        view.findViewById(R.id.newPW_resetPW_button).setOnClickListener(this::sendNewPW);

        emailSent = false;
        codeSent = false;
        passwordCorrect = false;

        return view;
    }



    private void sendEmail(View view) {

        AsyncTask<String, Void, String> task = null;


        EditText editEmail = (EditText)getActivity().findViewById(R.id.newPW_email);
        Button buttonEmail = (Button)getActivity().findViewById(R.id.newPW_email_button);
        TextView errorEmail = (TextView)getActivity().findViewById(R.id.error_newPW_email);

        EditText editCode = (EditText)getActivity().findViewById(R.id.newPW_code);
        Button buttonCode = (Button) getActivity().findViewById(R.id.newPW_codeVerify_button);


        //Construct a JSONObject to build a formatted message to send.
        mEmail = ((EditText)getActivity().findViewById(R.id.newPW_email)).getText().toString();

        if(emailSent) {
            errorEmail.setText("You have already sent an Email!");
            errorEmail.setVisibility(View.VISIBLE);
            return;
        }

        // TODO: use validation function
        if (!emailValidation(mEmail)) {
            errorEmail.setText("Email is invalid!");
            errorEmail.setVisibility(View.VISIBLE);
            return;
        }

        errorEmail.setVisibility(View.INVISIBLE);
        emailSent = true;

        Random rand = new Random();
        mCode = "";
        for (int i = 0; i < 6; i++) {
            mCode += rand.nextInt(10);
        }
        Log.v("Random code", mCode);

        // TODO: validations
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", mEmail);
            msg.put("code", mCode);
        } catch (JSONException e) {
            //cancel will result in onCanceled not onPostExecute
//            cancel(true);
            Log.wtf("Error with JSON creation:", e.getMessage());
        }
        task = new NewPasswordWebServiceTask();
        task.execute(getString(R.string.base_url),
                getString(R.string.ep_code_to_email),
                msg.toString());
    }

    private void verifyCode(View view) {


        EditText editCode = (EditText)getActivity().findViewById(R.id.newPW_code);
        Button buttonCode = (Button) getActivity().findViewById(R.id.newPW_codeVerify_button);
        TextView errorCode = (TextView)getActivity().findViewById(R.id.error_newPW_code);

        EditText pass1 = (EditText)getActivity().findViewById(R.id.newPW_password1);
        EditText pass2 = (EditText)getActivity().findViewById(R.id.newPW_password2);
        Button buttonPassword = (Button) getActivity().findViewById(R.id.newPW_resetPW_button);

        if(codeSent) {
            errorCode.setText("You have already comfirmed your email identity!");
            errorCode.setVisibility(View.VISIBLE);
            return;
        }else if(!(emailSent)) {
            errorCode.setText("You have not confirmed an email yet!");
            errorCode.setVisibility(View.VISIBLE);
            return;
        }

        String inputCode = ((EditText)getActivity().findViewById(R.id.newPW_code)).getText().toString();
        if(!inputCode.equals(mCode)) {
            errorCode.setText("Incorrect Code!");
            errorCode.setVisibility(View.VISIBLE);
            return;
        }

        errorCode.setVisibility(View.INVISIBLE);
        codeSent = true;
    }

    private void sendNewPW(View view) {

        AsyncTask<String, Void, String> task = null;


        EditText pass1 = (EditText)getActivity().findViewById(R.id.newPW_password1);
        EditText pass2 = (EditText)getActivity().findViewById(R.id.newPW_password2);
        Button buttonPassword = (Button) getActivity().findViewById(R.id.newPW_resetPW_button);
        TextView errorPW = (TextView)getActivity().findViewById(R.id.error_newPW_password);


        String password1 = ((EditText)getActivity().findViewById(R.id.newPW_password1)).getText().toString();
        String password2 = ((EditText)getActivity().findViewById(R.id.newPW_password2)).getText().toString();

        if(!(emailSent)) {
            errorPW.setText("You have not confirmed an email yet!");
            errorPW.setVisibility(View.VISIBLE);
            return;
        } else if(!(codeSent)) {
            errorPW.setText("You have not confirmed you email identity code yet!");
            errorPW.setVisibility(View.VISIBLE);
            return;
        }

        if (!passwordValidations(password1)) {
            errorPW.setText("Password must be eight characters, and at least one letter and number!");
            errorPW.setVisibility(View.VISIBLE);
            return;
        }
        if (!password1.equals(password2)) {
            errorPW.setText("Make sure you type in your new password twice correcly!");
            errorPW.setVisibility(View.VISIBLE);
            return;
        }

        errorPW.setVisibility(View.INVISIBLE);
        passwordCorrect = true;

        // TODO: validations
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", mEmail);
            msg.put("password", password1);
        } catch (JSONException e) {
            //cancel will result in onCanceled not onPostExecute
//            cancel(true);
            Log.wtf("Error with JSON creation:", e.getMessage());
        }
        task = new NewPasswordWebServiceTask();
        task.execute(getString(R.string.base_url),
                getString(R.string.ep_update_pw),
                msg.toString());
    }

    private boolean emailValidation(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private Boolean passwordValidations (String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


    private class NewPasswordWebServiceTask extends AsyncTask<String, Void, String> {

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

                Log.v("Did Async try work?", "Yes!");
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
            return;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (passwordCorrect) {
                try {
                    JSONObject ob = new JSONObject(result);
                    if (ob.getBoolean("success")) {

                        PasswordChangedFragment nextFrag = new PasswordChangedFragment();

                        Bundle arguments = new Bundle();
                        mEmail = ((EditText) getActivity().findViewById(R.id.newPW_email)).getText().toString();
                        arguments.putString("email", mEmail);
                        nextFrag.setArguments(arguments);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.new_pw_container, nextFrag, "findThisFragment")
                                .addToBackStack(null)
                                .commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
