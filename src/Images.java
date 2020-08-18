import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Images extends ListenerAdapter {
	
	ArrayList<File> fileNames = new ArrayList<File>();
	String selectedDirectoryString;
	Random random = new Random();
	
    public void onMessageReceived(MessageReceivedEvent event) {

    	//string here so i don't need to type it a million times
    	String keyword = "~img ";
    	/*
    	// Create the EmbedBuilder instance
    	EmbedBuilder embeded = new EmbedBuilder();

    	embeded.setTitle("JoeBot Commands");
    	embeded.setColor(new Color(0x8A2BE2));
    	embeded.setDescription("joe commands\n");
*/
        if (event.getMessage().getContentRaw().substring(0, 5).equalsIgnoreCase(keyword)) {

        	//get default directory for preferences.txt
        	try {
				readPreferences("preferences.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	//get keyword that user has determined
        	String fileKeyword = event.getMessage().getContentRaw().substring(5);
        	
        	//scan and sort through images to find images with the same name as keyword
        	scanImagesFolder(fileKeyword, event);
        	
        	
        }
        
        //custom delete command, later moving this to a separate class
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
    
    //scans all images in folder
    public void scanImagesFolder(String fk, MessageReceivedEvent ev) {
    	
    	//clear arraylist to restart position back to 0
    	fileNames.clear();
    	
    	File folder = new File(selectedDirectoryString);
    	
    	
    	//scan through selected folder
        File[] listOfFiles = folder.listFiles();
        
        //loop through filenames
        for (int i = 0; i < listOfFiles.length; i++) {
        	
        	//if filename = keyword, sort it to another array comprising of ONLY images with the same keyword
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().replaceAll("\\d", "").replaceAll(".png", "").trim().equalsIgnoreCase(fk)) {
                    
            	fileNames.add(listOfFiles[i]);
                
            }
        }        
        
        //determine if images were found under the keyword, if so, send image, if not, display error
        if (fileNames.size() == 0) {
        	
        	ev.getChannel().sendMessage("no images match!").queue();
        	
        } else {
        	
        	//randomly send an image from the arry for variety
        	int randomNumber = random.nextInt(fileNames.size());
        	ev.getChannel().sendFile(fileNames.get(randomNumber)).queue();
        	
        }
    }
    
    ////UPDATE WHICH LINE IS READ AND THE APPROPRIATE VARIABLES OF DIRECTORY AND/OR TOKEN FROM THE PREFERENCES FILE
    public void readPreferences(String fileName) throws IOException {
    	try {
    		//temporary array to hold values of each line
    		String[] prefs = {null, null};
    		
    		//indicate the index of the line being read
    		int i = 0;
    		
    		//the file to be opened for reading  
    		FileInputStream fis = new FileInputStream("preferences.txt");       
    		
    		//file to be scanned  
    		Scanner sc = new Scanner(fis);    
    		
    		//returns true if there is another line to read  
    		while(sc.hasNextLine()) {  
    			
    			prefs[i] = (sc.nextLine());
    			i++;
    		
    		}
    		
    		sc.close();
    		
    		//assign values to global variables
    		selectedDirectoryString = prefs[0];
    		
    	} catch(IOException e) {  
    		e.printStackTrace();  
    	} 
    }
}
