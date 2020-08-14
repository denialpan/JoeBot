import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.Random;

public class Images extends ListenerAdapter{
    public void onMessageReceived(MessageReceivedEvent event) {

    	//string here so i don't need to type it a million times
    	String imagePrefix = "img ";
    	
    	//search for message that contains ONLY "img" to send availabe commands
        if (event.getMessage().getContentRaw().equalsIgnoreCase("img")) {
            sendImageCommands(event);
        }
        
        if (event.getMessage().getContentRaw().substring(0, 4).equalsIgnoreCase(imagePrefix)) {
        	//figure this shit out later lol set up the add images thing first
        }
    }

    public static void sendImageCommands(MessageReceivedEvent ev) {
        ev.getChannel().sendMessage("```available \"img\" commands```").queue();
    }

    public static void sendFileDiscord(MessageReceivedEvent e, File f){

        e.getChannel().sendFile(f).queue();

    }
}
	