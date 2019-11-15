package MobileProject.WorkingTitle.UI.Conversations;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;


public class Conversation implements Serializable, Parcelable {

    private final String mContact;
    private final String mlastMessage;
    private final ArrayList mMessages;
    private final int mChatID;


    protected Conversation(Parcel in) {
        mContact = in.readString();
        mlastMessage = in.readString();
        mMessages = in.createBinderArrayList();
        mChatID = in.readInt();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mContact);
        dest.writeString(mlastMessage);
        dest.writeBinderList(mMessages);
        dest.writeInt(mChatID);
    }

    /**
     * Helper class for building Credentials.
     *
     */
    public static class Builder {
        private final String mContact;
        private  String mlastMessage;
        private ArrayList mMessages;
        private int mChatId;

        /**
         * Constructs a new Builder.
         */
        public Builder(String mContact, String lastMessage, ArrayList messages, int id) {
            this.mContact = mContact;
            this.mlastMessage = lastMessage;
            this.mMessages = messages;
            this.mChatId = id;
        }

        public Conversation build() {
            return new Conversation(this);
        }

    }

    private Conversation(final Builder builder) {
        this.mContact = builder.mContact;
        this.mlastMessage = builder.mlastMessage;
        this.mMessages = builder.mMessages;
        this.mChatID = builder.mChatId;
    }



    public String getContact() {
        return mContact;
    }

    public String getLastMessage() {
        return mlastMessage;
    }

    public ArrayList getMessages() {
        return mMessages;
    }

    public void addMessage(String message) {
        this.mMessages.add(message);
    }



}
