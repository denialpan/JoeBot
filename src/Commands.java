import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
   	
    	// Create the EmbedBuilder instance
    	EmbedBuilder embeded = new EmbedBuilder();

    	embeded.setTitle("JoeBot Commands");
    	embeded.setColor(new Color(0x8A2BE2));
    	embeded.setDescription("~help\n"
    			+ "~img [keyword]\n"
    			+ "~delete [1 - 100]\n");

        if (event.getAuthor().isBot()) return;

        if (event.getMessage().getContentRaw().equalsIgnoreCase("~help")) {

        	event.getChannel().sendMessage(embeded.build()).queue();
        	
        }
    }
}
