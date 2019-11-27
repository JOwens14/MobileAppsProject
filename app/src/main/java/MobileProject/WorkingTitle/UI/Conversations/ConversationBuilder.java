package MobileProject.WorkingTitle.UI.Conversations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationBuilder {

    private static final List<Conversation> CONVERSATIONS = new ArrayList<Conversation>();
    private static final Map<String, Conversation> CONVERSATIONS_MAP = new HashMap<String, Conversation>();


    public static void addItem(Conversation item) {
        CONVERSATIONS.add(item);
        CONVERSATIONS_MAP.put(item.getContact(), item);
    }

    public static Conversation createConversation(String Contact, String lastMessage) {
        ArrayList<String> testMessages = new ArrayList<String>();

        return new Conversation.Builder(Contact, lastMessage, testMessages).build();
    }

    public static List getConversations() {
        // code to create sample convos for testing
        
        Boolean sampleCreated = false;

        //sample convo
        Conversation sample = createConversation("sample convo", "This is a test message");

        for (int i = 0; i < CONVERSATIONS.size(); i++) {
            if (CONVERSATIONS.get(i).getContact() == sample.getContact()) {
                sampleCreated = true;
            }
        }
        if (!sampleCreated) {
            addItem(sample);
        }

        return CONVERSATIONS;
    }

}
