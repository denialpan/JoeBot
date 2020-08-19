import java.util.List;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Delete extends ListenerAdapter{
	
	public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getMessage().getContentRaw().substring(0, 7).equalsIgnoreCase("~delete")) {
        	
        	//this integer exists to determine how many images to delete
        	int x;
        	
        	//if the keyword is JUST 7 letters long, automatically assign x to be 1, to delete only ONE previous message
        	if (event.getMessage().getContentRaw().length() == 7) {
        		
        		x = 1;
        	
        	} else {
        		
        		//otherwise, get the amount the user wants to delete
        		x = Integer.parseInt(event.getMessage().getContentRaw().substring(8));
        	}
        	
        	//get message history
        	MessageHistory history = new MessageHistory(event.getChannel());
        	
        	//determine which channel the event was called (must be casted to TextChannel for method availability below)
        	TextChannel textChannel = (TextChannel) event.getChannel();
        	
        	//create array of messages the size of how much the user wanted to check
        	List<Message> msgs = history.retrievePast(x + 1).complete(); //x + 1 because when ~delete is issued, it also deletes itself
        	
        	//loop through array of messages
        	for (Message m : msgs) {
        		
        		//the user is only allowed to delete bot messages, or messages that have only come from them
        		if (m.getAuthor().getAsMention().equals("<@736722374858113046>") || m.getAuthor().getAsMention().equals(event.getAuthor().getAsMention())) {
        			
        			//delete message that meet aforementioned requirements
        			m.delete().queue();
        		}
        		//repeat process
        	}
        }	
	
	}
	
}
