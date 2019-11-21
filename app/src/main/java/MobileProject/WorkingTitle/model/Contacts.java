package MobileProject.WorkingTitle.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static class Contact implements Serializable {
        public final String id;
        public final String contact;
        public final String details;
        private String token;
        private String username;
        private String email;
        private String status;

        public Contact(String id, String contact, String details) {
            this.id = id;
            this.contact = contact;
            this.details = details;
            this.token = "";
            this.email = "";
            this.username = "";
            status = "";
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
        public void setStatus(String status) {
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

        public String getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return contact;
        }
    }
}
