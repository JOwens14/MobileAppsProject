package MobileProject.WorkingTitle.UI.Connections;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import MobileProject.WorkingTitle.model.Contacts;
import MobileProject.WorkingTitle.model.Credentials;
import MobileProject.WorkingTitle.model.EnumsDefine;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import MobileProject.WorkingTitle.R;
import androidx.recyclerview.widget.RecyclerView;

public class ConnectionFragment extends Fragment {

//    private String mEmail;
//    private String mJwToken;
    private RecyclerView recyclerView;
    AsyncTask<String, Void, String> task = null;
    JSONObject msg;
    public ConnectionFragment() {}

    @Override
    public void onStart() {
        super.onStart();

        // Make the result text holder empty
        ((TextView)getActivity().findViewById(R.id.actionResult)).setText("");

        //ChatFragmentArgs args = ChatFragmentArgs.fromBundle(getArguments());
//        mEmail = "";//"fakeemail@gmail.com";
//        mJwToken = "";//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImZha2VlbWFpbEBnbWFpbC5jb20iLCJpYXQiOjE1NzM3NTgxMjgsImV4cCI6MTU3Mzg0NDUyOH0.pf3PF6N2o6xiWEfwNMg5q521I1zaM_mMpVryhUuDELo";


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connections, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle args) {
        Credentials userCr = (Credentials)getActivity().getIntent().getExtras().get("userCr");
        if (getArguments() != null) {
            Contacts.Contact contact = (Contacts.Contact) getArguments().get("connection");
            TextView detail = (TextView) view.findViewById(R.id.text_share);
            detail.setSingleLine(false);
            detail.setText("Contact Detail \n" + contact.getEmail() +"\n"+ contact.getUsername());

            //Build body paramaters
            msg = new JSONObject();
            try {
                msg.put("emailA", userCr.getEmail()); // request sender
                msg.put("nickname", userCr.getUsername()); // nickname - email request sender
                msg.put("email_nickname", contact.getEmail());  // request receiver
                msg.put("memberid_a", userCr.getMemberId());  //my id
                msg.put("memberid_b", contact.getMemberid());  // receiver id
                msg.put("token", userCr.getDeviceToken());       // my device token
                msg.put("reciverToken", contact.getToken());  // receiver device token
            } catch (JSONException e) {
                //cancel will result in onCanceled not onPostExecute
//            cancel(true);
                Log.wtf("Error with JSON creation:", e.getMessage());
            }

            displayAction(view, contact.getStatus());

            //sets the actionbar title to the contact name
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setTitle("Connection");
            //actionBar.setDisplayHomeAsUpEnabled(false);

            //disables the bottom nav bar while in conversation
//            BottomNavigationView navView = activity.findViewById(R.id.nav_view);
//            navView.setVisibility(view.GONE);
            view.findViewById(R.id.sendMessageTo).setOnClickListener(this::handleSendClick);
            view.findViewById(R.id.friend_request).setOnClickListener(this::sendFriendRequest);
            view.findViewById(R.id.remove_request).setOnClickListener(this::handleSendClick);
            view.findViewById(R.id.DenyFriendRequest).setOnClickListener(this::handleDeny);
            view.findViewById(R.id.AcceptFriendRequest).setOnClickListener(this::handleAccept);
            view.findViewById(R.id.BlockUser).setOnClickListener(this::handleBlockUser);
            view.findViewById(R.id.re_send_friend_request).setOnClickListener(this::resendFriendRequest);
        }
    }
    private void handleBlockUser(final View view) {

    }

    private void handleAccept(final View view) {
        try {
            msg.remove("actionType");
            msg.put("actionType", EnumsDefine.Status.FriendRequestFrom);  // type of request acction
        } catch (JSONException jx){}
        doAction();
    }

    private void handleDeny(final View view) {

    }


    public void showContactDetail(Contacts.Contact contact) {

    }

    private void handleSendClick(final View view) {

    }

    private void removeConnection(final View view) {

    }

    private void sendFriendRequest(final View view) {
        EditText email_nickname = (EditText)getActivity().findViewById(R.id.email_nickname_target);
        try {
            msg.remove("actionType");
            msg.put("actionType", EnumsDefine.Status.FriendRequestTo);  // type of request acction
            msg.remove("email_nickname");
            msg.put("email_nickname", email_nickname.getText().toString());
        } catch (JSONException jx){}
        doAction();
    }

    private void resendFriendRequest(final View view) {
        try {
            msg.remove("actionType");
            msg.put("actionType", EnumsDefine.Status.FriendRequestTo);  // type of request acction
        } catch (JSONException jx){}
        doAction();
    }



    private void displayAction(View view, EnumsDefine.Status status) {

        switch (status){
            case NewConnection:
//                Log.d("TestEnum", EnumsDefine.Status.NewConnection.toString());
                sendNewConnectionRequest(view, true);
                friendRequestActionVisibility(view, false);
                friendRequestToActionVisibility(view, false);
                sendMessageActionVisibility(view, false);
                break;
            case FriendRequestTo:
                friendRequestToActionVisibility(view, true);
                sendNewConnectionRequest(view, false);
                friendRequestActionVisibility(view, false);
                sendMessageActionVisibility(view, false);
                break;
            case FriendRequestFrom:
                friendRequestActionVisibility(view, true);
                sendNewConnectionRequest(view, false);
                friendRequestToActionVisibility(view, false);
                sendMessageActionVisibility(view, false);
                break;
            default:
                sendMessageActionVisibility(view, true);
                friendRequestActionVisibility(view, false);
                sendNewConnectionRequest(view, false);
                friendRequestToActionVisibility(view, false);
                break;
        }
    }

    private void friendRequestActionVisibility(View view, Boolean isShow) {
        int show = isShow ? View.VISIBLE : View.INVISIBLE;
        view.findViewById(R.id.BlockUser).setVisibility(show);
        view.findViewById(R.id.AcceptFriendRequest).setVisibility(show);
        view.findViewById(R.id.DenyFriendRequest).setVisibility(show);
    }

    private void sendMessageActionVisibility(View view, Boolean isShow) {
        int show = isShow ? View.VISIBLE : View.INVISIBLE;
        view.findViewById(R.id.sendMessageTo).setVisibility(show);
    }

    private void sendNewConnectionRequest(View view, Boolean isShow) {
        int show = isShow ? View.VISIBLE : View.INVISIBLE;
        view.findViewById(R.id.friend_request).setVisibility(show);
        view.findViewById(R.id.email_nickname_target).setVisibility(show);
    }

    private void friendRequestToActionVisibility(View view, Boolean isShow) {
        int show = isShow ? View.VISIBLE : View.INVISIBLE;
        view.findViewById(R.id.remove_request).setVisibility(show);
        view.findViewById(R.id.re_send_friend_request).setVisibility(show);
    }

    private class PostWebServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //get pushy token
            String deviceToken;

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
                ob = jsonObj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            Log.w("GET_CONTACT_ERROR", "Failed to get contact");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject ob = new JSONObject(result);
                if (ob.getBoolean("success") && ob.getBoolean("result")) {
                    TextView textResult = (TextView)getActivity().findViewById(R.id.actionResult);
                    textResult.setText(ob.getString("message"));
                    sendMessageActionVisibility(getView(), true);
                    friendRequestActionVisibility(getView(), false);
                    sendNewConnectionRequest(getView(), false);
                    friendRequestToActionVisibility(getView(), false);
//                    addFriendSuccess();
                } else {
                    TextView textResult = (TextView)getActivity().findViewById(R.id.actionResult);
                    textResult.setText(ob.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

//    private void addFriendSuccess() {
//        String newValue = "I like sheep.";
//        int updateIndex = 3;
//        //(ConnectionsRecyclerViewAdapter.ViewHolder)getActivity().f(R.layout.fragment_contact);
//    }

    private void doAction() {
        task = new PostWebServiceTask();
        task.execute(getString(R.string.base_url),
                getString(R.string.friendConnect),
                msg.toString());
    }
}