package MobileProject.WorkingTitle.UI.Conversations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConversationBuilder {

    private static final List<Conversation> CONVERSATIONS = new ArrayList<Conversation>();
    private static final Map<String, Conversation> CONVERSATIONS_MAP = new HashMap<String, Conversation>();


    private static void addItem(Conversation item) {
        CONVERSATIONS.add(item);
        CONVERSATIONS_MAP.put(item.getContact(), item);
    }

    public static Conversation createConversation(String Contact, String lastMessage) {
        Random rand = new Random();
        int id = rand.nextInt(10000);

        ArrayList messageList = new ArrayList();
        messageList.add(lastMessage);
        return new Conversation.Builder(Contact, lastMessage, messageList, id).build();
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
        //Log.d("what", "why here");
        return CONVERSATIONS;
    }

}
