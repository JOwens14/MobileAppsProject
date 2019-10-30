package MobileProject.WorkingTitle.ui.Conversations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationBuilder {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Conversation> CONVERSATIONS = new ArrayList<Conversation>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Conversation> CONVERSATIONS_MAP = new HashMap<String, Conversation>();

    private static final int COUNT = 3;


    private static void addItem(Conversation item) {
        CONVERSATIONS.add(item);
        CONVERSATIONS_MAP.put(item.getContact(), item);
    }

    public static Conversation createConversation(String Contact, String lastMessage) {
        return new Conversation.Builder(Contact, lastMessage, null).build();
    }

    public static List getConversations() {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createConversation("Jolene", "This is a test message"));
        }

        return CONVERSATIONS;
    }

}
