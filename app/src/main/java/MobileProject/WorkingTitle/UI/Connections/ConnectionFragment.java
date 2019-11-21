package MobileProject.WorkingTitle.UI.Connections;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import MobileProject.WorkingTitle.model.Contacts;
import MobileProject.WorkingTitle.model.Credentials;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import MobileProject.WorkingTitle.R;
import androidx.recyclerview.widget.RecyclerView;

public class ConnectionFragment extends Fragment {

    private String mEmail;
    private String mJwToken;
    private String mSendUrl;
    private RecyclerView recyclerView;

    public ConnectionFragment() {}

    @Override
    public void onStart() {
        super.onStart();

        //ChatFragmentArgs args = ChatFragmentArgs.fromBundle(getArguments());
//        mEmail = "";//"fakeemail@gmail.com";
//        mJwToken = "";//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImZha2VlbWFpbEBnbWFpbC5jb20iLCJpYXQiOjE1NzM3NTgxMjgsImV4cCI6MTU3Mzg0NDUyOH0.pf3PF6N2o6xiWEfwNMg5q521I1zaM_mMpVryhUuDELo";

        Credentials userCr = (Credentials)getActivity().getIntent().getExtras().get("userCr");
        mEmail = userCr.getEmail();
        mJwToken = userCr.getJwtToken();
        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connections, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle args) {
        if (getArguments() != null) {
            Contacts.Contact contact = (Contacts.Contact) getArguments().get("connection");
            TextView detail = (TextView) view.findViewById(R.id.text_share);
            detail.setText(contact.getEmail() + contact.getUsername());
            //sets the actionbar title to the contact name
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setTitle("Connection");
            //actionBar.setDisplayHomeAsUpEnabled(false);

            //disables the bottom nav bar while in conversation
//            BottomNavigationView navView = activity.findViewById(R.id.nav_view);
//            navView.setVisibility(view.GONE);
        }
    }


    public void showContactDetail(Contacts.Contact contact) {

    }

    private void handleSendClick(final View theButton) {

    }
}