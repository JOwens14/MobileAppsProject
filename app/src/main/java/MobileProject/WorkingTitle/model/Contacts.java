package MobileProject.WorkingTitle.model;

import android.content.Context;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MobileProject.WorkingTitle.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Contacts implements Serializable {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Contact> FRIENDS = new ArrayList<Contact>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Contact> CONTACT_MAP = new HashMap<String, Contact>();

    private static final int COUNT = 1;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createContact(i));
        }
    }
    public void sortFriend() {
        Collections.sort(FRIENDS);
    }

    public static void addItem(Contact item) {
        FRIENDS.add(item);
        CONTACT_MAP.put(item.id, item);
    }

    private static Contact createContact(int position) {
        return new Contact(String.valueOf(position), "Add new contact ", makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Contact implements Serializable, Comparable<Contact> {
        public final String id;
        public final String contact;
        public final String details;
        private String token;
        private String username;
        private String email;
        private String memberid;
        private EnumsDefine.Status status;  // "friend_request_from" , "friend_request_to", "new_connection"

        public Contact(String id, String contact, String details) {
            this.id = id;
            this.contact = contact;
            this.details = details;
            this.memberid = "";
            this.token = "";
            this.email = "";
            this.username = "";
            this.status = EnumsDefine.Status.NewConnection;
        }

        public void setToken(String token) {
            this.token = token;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public void setStatus(EnumsDefine.Status status) {
            this.status = status;
        }

        public String getEmail() {
            return email;
        }

        public String getToken() {
            return token;
        }

        public String getUsername() {
            return username;
        }

        public String getMemberid() {
            return memberid;
        }

        public void setMemberid(String memberid) {
            this.memberid = memberid;
        }

        public EnumsDefine.Status getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return contact;
        }

        /*
         *Compare a given Contact with current(this) object.
         *If current Contact id is greater than the received object,
         *then current object is greater than the other.
         */
        public int compareTo(Contact otherContact) {
            // return this.id - otherStudent.id ; //result of this operation can overflow
            int a = (otherContact.getStatus().equals(EnumsDefine.Status.Connected))?1:0;
            int b = (this.getStatus().equals(EnumsDefine.Status.Connected))?1:0;

            return b - a;
        }
    }
}
