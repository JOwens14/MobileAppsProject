package MobileProject.WorkingTitle.ui.Conversations;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Class to encapsulate a Phish.net Blog Post. Building an Object requires a publish date and title.
 *
 * Optional fields include URL, teaser, and Author.
 *
 *
 * @author Charles Bryan
 * @version 14 September 2018
 */
public class Conversation implements Serializable, Parcelable {

    private final String mContact;
    private final String mlastMessage;
    private final Array mMessages;

    protected Conversation(Parcel in) {
        mContact = in.readString();
        mlastMessage = in.readString();
        mMessages = in.readString();

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
        dest.writeString(mPubDate);
        dest.writeString(mTitle);
        dest.writeString(mUrl);
        dest.writeString(mTeaser);
        dest.writeString(mAuthor);
    }

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mPubDate;
        private final String mTitle;
        private  String mUrl = "";
        private  String mTeaser = "";
        private  String mAuthor = "";


        /**
         * Constructs a new Builder.
         */
        public Builder(String mContact, String lastMessage, Array messages) {
            this.mContact = mContact;
            this.mlastMessage = lastMessage;
            this.mMessages = messages
        }




        /**
         * Add an optional author of the blog post.
         * @param val an optional author of the blog post.
         * @return the Builder of this Conversation
         */
        public Builder addAuthor(final String val) {
            mAuthor = val;
            return this;
        }

        public Conversation build() {
            return new Conversation(this);
        }

    }

    private Conversation(final Builder builder) {
        this.mContact = builder.mContact;
        this.mlastMessage = builder.mTitle;
        this.mMessages = builder.mUrl;

    }

    public String getContact() {
        return mContact;
    }

    public String getLastMessage() {
        return mlastMessage;
    }

    public Array getMessages() {
        return mMessages;
    }



}
