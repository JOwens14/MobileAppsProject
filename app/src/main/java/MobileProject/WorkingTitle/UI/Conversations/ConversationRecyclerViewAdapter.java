package MobileProject.WorkingTitle.UI.Conversations;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import MobileProject.WorkingTitle.R;
import MobileProject.WorkingTitle.UI.Conversations.Conversation;


public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final ConversationFragment.OnListFragmentInteractionListener mListener;
    private TextView message;


    public ConversationRecyclerViewAdapter(List<String> items, ConversationFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_conversation_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        message = holder.mView.findViewById(R.id.conversation_message);

        //THIS METHOD RELIES ON THE MESSAGE BEING "USERNAME: MESSAGE"
        setBubbleText(holder, mValues.get(position));


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMessage;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMessage = view.findViewById(R.id.conversation_message);

        }

    }

    //THIS METHOD RELIES ON THE MESSAGE BEING "USERNAME: MESSAGE"
    public void setBubbleText(ViewHolder holder, String msg) {
        //gets the username header
        int splitter = msg.indexOf(":");
        String username = msg.substring(0, splitter);

        //Log.d("user get inside", username);
        //Log.d("user getoutside", ConversationFragment.getUser());

        //conversion of the layouts
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //margins set
        params.leftMargin = 15;
        params.rightMargin = 15;
        params.topMargin = 15;


        // if username in message matches our user name then the message appears right and blue, else grey and left
        if (username.equals(ConversationFragment.getUser())) {
            //Log.d("MATCH:", "CONFIRM");
            message.setBackgroundResource(R.drawable.chatbubbleme);
            params.gravity = Gravity.RIGHT;

        } else {
            message.setBackgroundResource(R.drawable.chatbubblethem);
            params.gravity = Gravity.LEFT;
        }

        //set the new layout
        message.setLayoutParams(params);


        holder.mMessage.setText(msg.substring(splitter + 2));


    }





    /**
     * Probably don't need this?
     */
    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
}
