package MobileProject.WorkingTitle.UI.Conversations;


import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

import MobileProject.WorkingTitle.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {


    public ConversationFragment() {
        // Required empty public constructor
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
            Serializable convo = getArguments().getSerializable("conversation");
            Conversation conversation = (Conversation) convo;

            //sets the actionbar title to the contact name
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setTitle(conversation.getContact());
            //actionBar.setDisplayHomeAsUpEnabled(false);

            //disables the bottom nav bar while in conversation
            BottomNavigationView navView = activity.findViewById(R.id.nav_view);
            navView.setVisibility(view.GONE);


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

}
