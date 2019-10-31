package MobileProject.WorkingTitle.UI.Conversations;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import MobileProject.WorkingTitle.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Conversation} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class ConversationsRecyclerViewAdapter extends RecyclerView.Adapter<ConversationsRecyclerViewAdapter.ViewHolder> {

    private final List<Conversation> mValues;
    private final ConversationListFragment.OnListFragmentInteractionListener mListener;


    public ConversationsRecyclerViewAdapter(List<Conversation> items, ConversationListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_conversation_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdContact.setText(mValues.get(position).getContact());
        holder.mIdLastMessage.setText(mValues.get(position).getLastMessage());

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
        public final TextView mIdContact;
        public final TextView mIdLastMessage;
        public Conversation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdContact = (TextView) view.findViewById(R.id.mIdContact);
            mIdLastMessage = (TextView) view.findViewById(R.id.mIdLastMessage);

        }

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
