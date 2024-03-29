package MobileProject.WorkingTitle.UI.Conversations;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import MobileProject.WorkingTitle.HomeActivity;
import MobileProject.WorkingTitle.model.Credentials;

import MobileProject.WorkingTitle.UI.Conversations.ConversationList.ConversationsListRecyclerViewAdapter;

import MobileProject.WorkingTitle.utils.PushReceiver;
import MobileProject.WorkingTitle.utils.SendPostAsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import MobileProject.WorkingTitle.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static me.pushy.sdk.config.PushyNotificationChannel.CHANNEL_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {

    private static final String TAG = "CHAT_FRAG";

    private static final String CHAT_ID = "1";
    private int notificationId = 1;


    private PushMessageReceiver mPushMessageReciever;

    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;

    private String mEmail;
    private String mJwToken;
    private String mSendUrl;
    private String mGetAllUrl;
    private static String mUser;

    private RecyclerView recyclerView;
    private Conversation conversation;
    private ArrayList mMessages;

    private int mColumnCount = 1;


    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        //ChatFragmentArgs args = ChatFragmentArgs.fromBundle(getArguments());
//        mEmail = "";//"fakeemail@gmail.com";
//        mJwToken = "";//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImZha2VlbWFpbEBnbWFpbC5jb20iLCJpYXQiOjE1NzM3NTgxMjgsImV4cCI6MTU3Mzg0NDUyOH0.pf3PF6N2o6xiWEfwNMg5q521I1zaM_mMpVryhUuDELo";

            Credentials userCr = (Credentials)getActivity().getIntent().getExtras().get("userCr");
            mEmail = userCr.getEmail();
            mJwToken = userCr.getJwtToken();
            mUser = userCr.getUsername().replaceAll("\\s+","");
        //We will use this url every time the user hits send. Let's only build it once, ya? - yes please
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();
        //Log.d("SEND URL", mSendUrl);

        populateMessages(CHAT_ID);

        if (conversation.getSize() > 0){
            recyclerView.scrollToPosition(conversation.getSize() - 1);
        }

    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle args) {

        if (getArguments() != null) {

            // TODO: get old messages


            mMessageOutputTextView = view.findViewById(R.id.conversation_message);
            mMessageInputEditText = view.findViewById(R.id.edittext_chatbox);

            view.findViewById(R.id.button_chatbox_send).setOnClickListener(this::handleSendClick);

            Serializable convo = getArguments().getSerializable("conversation");
            conversation = (Conversation) convo;

            mMessages = conversation.getMessages();

            if (view.findViewById(R.id.reyclerview_message_list) != null) {
                Context context = view.getContext();
                recyclerView = view.findViewById(R.id.reyclerview_message_list);
                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                recyclerView.setAdapter(new ConversationRecyclerViewAdapter(mMessages, this::messageClicked));
            }

            //sets the actionbar title to the contact name
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setTitle(conversation.getContact());
            //actionBar.setDisplayHomeAsUpEnabled(false);

            //disables the bottom nav bar while in conversation
            BottomNavigationView navView = activity.findViewById(R.id.nav_view);
            navView.setVisibility(view.GONE);


            //adjusts the recycler view when the layout size changes. IE: when the keyboard is opened
            recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom,
                                                    oldLeft, oldTop, oldRight, oldBottom) -> {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(() -> {
                        //if statement is to not crash when the conversation is empty
                        if (recyclerView.getAdapter().getItemCount() > 1) {
                            recyclerView.smoothScrollToPosition(
                                    recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 1);
                }
            });

        }
    }

    private void populateMessages(String chatId) {
        //dont double populate
        if (!mMessages.isEmpty()){
            return;
        }
        //post to get messages
        JSONObject mJson = new JSONObject();
        try {
            mJson.put("chatId", chatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mGetAllUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_getAll))
                .build()
                .toString();

        new SendPostAsyncTask.Builder(mGetAllUrl, mJson)
                .onPostExecute(this::popMessPost)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();

    }

    private void popMessPost(final String result) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);
            //Log.d("HERE COME THE MESSAGES" , result);

            if(res.has("messages")) {
                //response has field "messages"
                //lets populate the messages field for this conversation
                JSONArray messages = res.getJSONArray("messages");

                //Log.d("text:", messages.get(0).toString());
                //ADDS all the messages from the database to the view
                for (int i = messages.length() - 1; i > 0; i--) {
                    JSONObject message = new JSONObject(messages.get(i).toString());
                    //Log.d("text:", message.get("message").toString());
                    String user = message.get("email").toString();
                    String msg = message.get("message").toString();
                    mMessages.add(user + ": " + msg);
                }

                // notifies the list that there has been an update and scrolls to the bottom of the list
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scrollToPosition(conversation.getSize() - 1);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void messageClicked(String message) {
        //todo maybe
    }



    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new PushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mPushMessageReciever, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null){
            getActivity().unregisterReceiver(mPushMessageReciever);
        }
    }


    /**
     * Probably not needed!?
     * @param html html garb text
     * @return no html
     */
    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }



    private void handleSendClick(final View theButton) {
        String msg = mMessageInputEditText.getText().toString();

        //handler for if sending empty message
        if (msg.isEmpty()) {
            //do not pass go, do not collect 200 dollars
            return;
        }

        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("email", mEmail);
            messageJson.put("message", msg);
            messageJson.put("chatId", CHAT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sender = mUser;

        conversation.addMessage(sender + ": " + msg);

        // notifies the list that there has been an update and scrolls to the bottom of the list
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scrollToPosition(conversation.getSize() - 1);

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    private void endOfSendMsgTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);

            if(res.has("success")  && res.getBoolean("success")) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText("");

                //its up to you to decide if you want to send the message to the output here
                //or wait for the message to come back from the web service.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class PushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {
                if (!mEmail.equals(intent.getStringExtra("SENDER"))) {
                    String sender = intent.getStringExtra("SENDER");
                    String messageText = intent.getStringExtra("MESSAGE");
                    conversation.addMessage(sender + ": " + messageText);

                    // notifies the list that there has been an update and scrolls to the bottom of the list
                    recyclerView.getAdapter().notifyDataSetChanged();
                    recyclerView.scrollToPosition(conversation.getSize() - 1);


                }
            }
        }
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
        void onListFragmentInteraction(String message);
    }

    public static String getUser() {
        return mUser;
    }


}
