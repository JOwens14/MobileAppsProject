package MobileProject.WorkingTitle.UI.Conversations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationBuilder {

    private static final List<Conversation> CONVERSATIONS = new ArrayList<Conversation>();
    private static final Map<String, Conversation> CONVERSATIONS_MAP = new HashMap<String, Conversation>();


    private static void addItem(Conversation item) {
        CONVERSATIONS.add(item);
        CONVERSATIONS_MAP.put(item.getContact(), item);
    }

    public static Conversation createConversation(String Contact, String lastMessage) {
        ArrayList<String> testMessages = new ArrayList<String>();
        testMessages.add("Test Message 1 now the next");
        testMessages.add("Test Message2     2");
        testMessages.add("Test Message3    3");
        testMessages.add("Test Message4     4");
        testMessages.add("Test Message5    5");
        return new Conversation.Builder(Contact, lastMessage, testMessages).build();
    }

    public static List getConversations() {
        //fake convos
        String[] names = {"Luke", "Leia", "Han", "Chewy"};
        // Add some sample items.

        //makes sure the list is clear
        if (CONVERSATIONS != null) {
            CONVERSATIONS.clear();
        }
        for (int i = 0; i < names.length; i++) {
            //checks for duplicates
            if (!CONVERSATIONS.contains(names[i])) {
                addItem(createConversation(names[i], "This is a test message"));
            }

        }
        return CONVERSATIONS;
    }

}
