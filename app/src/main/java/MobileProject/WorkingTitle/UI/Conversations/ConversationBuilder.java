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
        return new Conversation.Builder(Contact, lastMessage, null).build();
    }

    public static List getConversations() {
        //fake convos
        String[] names = {"Luke", "Leia", "Han", "Chewy", "Bobba Fett", "R2-D2", "C3-P0"};
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
        //Log.d("what", "why here");
        return CONVERSATIONS;
    }

}
