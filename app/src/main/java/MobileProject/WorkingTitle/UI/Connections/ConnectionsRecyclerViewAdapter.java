package MobileProject.WorkingTitle.UI.Connections;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import MobileProject.WorkingTitle.R;
import MobileProject.WorkingTitle.UI.Connections.ConnectionsListFragment.OnListFragmentInteractionListener;
import MobileProject.WorkingTitle.model.Contacts.Contact;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Contact} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ConnectionsRecyclerViewAdapter extends RecyclerView.Adapter<ConnectionsRecyclerViewAdapter.ViewHolder> {

    private final List<Contact> mValues;
    private final ConnectionsListFragment.OnListFragmentInteractionListener mListener;

    public ConnectionsRecyclerViewAdapter(List<Contact> items, ConnectionsListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String status = "";

        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);

        switch (mValues.get(position).getStatus()) {
            case FriendRequestFrom:
                status = " is requesting connection";
                break;
            case FriendRequestTo:
                status = "     Accept Pending";
                break;
            case NewConnection:
                status = "";
                break;
                default: break;
        }
        holder.mContentView.setText(mValues.get(position).contact + status);
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
        public final TextView mIdView;
        public final TextView mContentView;
        public Contact mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_location);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
