package MobileProject.WorkingTitle.UI.Conversations.ConversationList;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import MobileProject.WorkingTitle.R;
import MobileProject.WorkingTitle.UI.Conversations.Conversation;
import MobileProject.WorkingTitle.UI.Conversations.ConversationBuilder;
import MobileProject.WorkingTitle.UI.Weather.Locations;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class ConversationListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private List<Conversation> mConversations;

    private RecyclerView ConvolistRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConversationListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ConversationListFragment newInstance(int columnCount) {
        ConversationListFragment fragment = new ConversationListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConversations = ConversationBuilder.getConversations();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);

        FloatingActionButton fab = view.findViewById(R.id.conversations_frag_fab);
        fab.setOnClickListener(this::createNewConversation);

        // Set the adapter
        if (view.findViewById(R.id.conversation_list) != null) {
            Context context = view.getContext();
            ConvolistRecyclerView = view.findViewById(R.id.conversation_list);

            if (mColumnCount <= 1) {
                ConvolistRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                ConvolistRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            ConvolistRecyclerView.setAdapter(new ConversationsListRecyclerViewAdapter(mConversations, this::onClick));


            ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                    new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            if(direction == 8) {
                                int index = viewHolder.getLayoutPosition();
                                mConversations.remove(index);
                                ConvolistRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        }
                    };

            // attaching the touch helper to recycler view
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(ConvolistRecyclerView);



        }
        return view;
    }

    private void onClick(final Conversation convo) {
        final Bundle args = new Bundle();
        args.putSerializable("conversation", convo);

        NavController nc = Navigation.findNavController(getView());
        if (nc.getCurrentDestination().getId() != R.id.nav_home) {
            nc.navigateUp();
        }
        //Log.d("current location" , String.valueOf(nc.getCurrentDestination().getLabel()));
        if(convo.getContact() != null) {
            nc.navigate(R.id.action_nav_home_to_nav_conversation, args);
        }
    }

    public void clearData() {
        mConversations.clear(); // clear list
    }


    private void createNewConversation(View view) {
        Context c = view.getContext();
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c, R.style.AlertDialog)
                .setTitle("Contact Name")
                .setView(taskEditText)
                .setPositiveButton("Start Conversation", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(taskEditText.getText());

                        // TODO CHECK IF THERE IS A CONTACT
                        //  WITH THE NAME THAT MATCHES THE INPUT

                        //make the new conversation
                        name = upperCaseFirst(name); //make the contact name uppercase if it is not.
                        Conversation newConvo = ConversationBuilder.createConversation(name, null);
                        ConversationBuilder.addItem(newConvo);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public String upperCaseFirst(String line) {
        return line.substring(0, 1).toUpperCase() + line.substring(1);
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
        void onListFragmentInteraction(Conversation post);
    }


}
