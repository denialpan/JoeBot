import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        if (event.getMessage().getContentRaw().equalsIgnoreCase("joe commands")) {

            sendCommands(event);
        }
    }

    public static void sendCommands(MessageReceivedEvent e) {

        e.getChannel().sendMessage(
                "pending..." +
                        "").queue();

    }
}
