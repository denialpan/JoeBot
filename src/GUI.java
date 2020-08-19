import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class GUI extends Application {
	
	//initialize bot api	
	JDA api;
	
	//initalize file increment suffix integer
	int increment;
	
	//initialize button toggle functionality
	boolean startStop = true;
	
	//initialize strings
	String token;
	String imageKeyword = "null";
	String selectedDirectoryString;
	
	
	//initialize labels
	Label botStatus;
    
	//initialize buttons
    Button toggleButton;
    Button exitButton;
    Button fileButton;
    Button restartButton;
    Button imageKeyWordButton;
    Button chooseDirectoryButton;
    
    //initialize folder directory
    File selectedDirectory;
    
    //initialize folder image names array
    ArrayList<String> fileNames = new ArrayList<String>();
    ArrayList<String> keywordFileNames;
    
    //initialize window
    Stage primaryStage;   

    //something here that i need but i dont know why /shrug
    public static void main(String[] args) {
        launch(args);
        
        
    }

    @Override
    //create the actual GUI, but not the bot listeners
    public void start(Stage primaryStage) {
    	
    	//set title window
    	primaryStage.setTitle("JoeBot");     
    	
        //create toggle button
        toggleButton = new Button("Run");
        toggleButton.setOnAction(e -> toggleBot());
        toggleButton.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");
        
        //create keyword button
        TextField imageKeywordTextfield = new TextField();
        imageKeywordTextfield.setPromptText("Keyword");
        imageKeywordTextfield.setStyle("-fx-text-inner-color: #FFFFFF; -fx-font: 15 Consolas; -fx-background-color: #202020;");
        imageKeywordTextfield.setOnAction(e -> {
        	
        	imageKeyword = imageKeywordTextfield.getText();
        	
        });
        
        //create exit button
        exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {primaryStage.close();});
        exitButton.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");
        
        //create token box
        TextField botToken = new PasswordField();
        botToken.setPromptText("Bot token");
        botToken.setStyle("-fx-text-inner-color: #FFFFFF; -fx-font: 15 Consolas; -fx-background-color: #202020;");
        botToken.setOnAction(e -> {
        	
        	try {
        		token = botToken.getText();
				updatePreferences("preferences.txt");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        });
        
        //create restart button
        restartButton = new Button("Restart GUI");
        restartButton.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");
        restartButton.setOnAction(e -> {
        	
        	try {
        		shutDownBotAPI();
        	} catch (Exception ee) {
        		
        	};
        	System.out.println( "Restarting app!" );
            primaryStage.close();
            Platform.runLater(() -> new GUI().start(new Stage()));
            
        	
        });
        
        //create directory selector button
        DirectoryChooser directoryChooser = new DirectoryChooser();
        
        //determine the default location that opens up first
        directoryChooser.setInitialDirectory(new File("C:\\"));

        chooseDirectoryButton = new Button("Select Directory");
        chooseDirectoryButton.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");
        chooseDirectoryButton.setOnAction(e -> {
            try{
            	//pick a folder
            	selectedDirectory = directoryChooser.showDialog(primaryStage);
            	selectedDirectoryString = selectedDirectory.getAbsolutePath();
            	updatePreferences("preferences.txt");
            } catch (Exception eeee) {
            	//because java sucks and you need try catch everywhere when something is null
            	System.out.println("no directory selected!");
            }

            
        });
        
        //GUI text so it doesn't look boring
        Label mainDescription = new Label("The JoeBot GUI");
        mainDescription.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");
        mainDescription.setTextFill(Color.web("#FFFFFF"));
        
        //GUI text so it doesn't look boring
        Label fileStatus = new Label("You must enter a keyword!");
        fileStatus.setStyle("-fx-font: 15 Consolas; -fx-base: #202020;");
        fileStatus.setTextFill(Color.web("#FFFFFF"));
        fileStatus.setVisible(false);
        
        //GUI text to indicate status
        botStatus = new Label();
        botStatus.setStyle("-fx-font: 15 Consolas; -fx-base: #202020;");
        botStatus.setTextFill(Color.web("#FFFFFF"));
        botStatus.setVisible(false);
        
        //file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        //create file button
        fileButton = new Button("Add image"); 
        fileButton.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");
        fileButton.setOnAction(e -> {
            
        	try {
				readPreferences("preferences.txt");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
        	//checks if keyword is null; if it is, then don't open resource window (causes errors when cancelled)
        	if(!imageKeyword.equals("null")) {
        		
        		scanImagesFolder();
                
        		//open resource window after scanning folder
                List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);
    	        try {	
    	        	
    	        	//this loop exists if multiple images are selected, which is amazing if i say so myself
                	for(File file : list) {
                		
                		//scan again after each image is copied to ensure no replacements after each repetition
    	        		scanImagesFolder();
    	        		
    	        		//get source and declare destination file
    	        		Path from = Paths.get(file.toURI());
    	        		
    	        		//rename by increments
    	                Path to = Paths.get(selectedDirectoryString + "\\" + imageKeyword + " " + increment + ".png");
    	                
    	                //copy the file to destination
    	                CopyOption[] options = new CopyOption[]{
    	                        StandardCopyOption.REPLACE_EXISTING,
    	                        StandardCopyOption.COPY_ATTRIBUTES
    	                };
    	                try {
    						Files.copy(from, to, options);
    						fileStatus.setText(imageKeyword + " " + increment + " has been copied to: \n" + selectedDirectoryString);
    						fileStatus.setVisible(true);
    					} catch (IOException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}
                	}
    	        } catch (Exception eee) {
    	        	
    	        	//declare to file selected
    	        	System.out.println("no files selected!");
    	        } 	
        	} else {
        		//you can't have null (default placeholder) as a keyword dummy
        		System.out.println("No keyword specified!");
        		fileStatus.setVisible(true);
        	}
            
        }); 
        
        //GridPane with 10px padding around edge
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        //organize buttons and text in the gridpane
        GridPane.setConstraints(mainDescription, 0, 0);
        GridPane.setConstraints(toggleButton, 0, 1);
        GridPane.setConstraints(exitButton, 1, 1);
        GridPane.setConstraints(botStatus, 0, 2);
        GridPane.setConstraints(botToken, 0, 3);
        GridPane.setConstraints(fileButton, 0, 4);
        GridPane.setConstraints(imageKeywordTextfield, 1, 4);
        GridPane.setConstraints(fileStatus, 0, 5);
        GridPane.setConstraints(chooseDirectoryButton, 0, 6);        
        GridPane.setConstraints(restartButton, 0, 7);
        
        //create a background fill 
        BackgroundFill background_fill = new BackgroundFill(Color.gray(0.15), CornerRadii.EMPTY, Insets.EMPTY); 

        //create Background 
        Background background = new Background(background_fill); 

        //set background 
        grid.setBackground(background); 
        
        //put it all together into a window and display it
        grid.getChildren().addAll(mainDescription, toggleButton, exitButton, botStatus, fileButton, chooseDirectoryButton, restartButton, fileStatus, botToken, imageKeywordTextfield);        
        Scene scene = new Scene(grid, 400, 360, Color.GRAY);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        //hijack the close corner button to remove errors
        primaryStage.setOnCloseRequest(event -> {
        try {
        	shutDownBotAPI();
        } catch (Exception e) {
        	primaryStage.close();
        };});
           
    }
    
    //toggle bot on and off
    public void toggleBot() {
    	
    	//detects if startStop is true or false. Pretty sure its obvious what happens when its true or false
    	if (startStop) {
    		
    		try {
    			readPreferences("preferences.txt");
    		} catch (IOException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}
    		//start the bot api and connect the bot to websocket
    		System.out.println("Bot has been turned on");
            
            botStatus.setVisible(true);
            
            try {
            	
                api = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
                api.addEventListener(new Commands(), new Images(), new Delete());
                botStatus.setText("JoeBot is currently running...");
                
                
                //hide GUI stuff accordingly
                exitButton.setVisible(false);

                //imageKeywordTextfield.setVisible(false);
                
                //change toggle button text
                toggleButton.setText("Stop");
                
                //show bot status
                botStatus.setVisible(true);
                
                startStop = false;
                

            } catch (Exception e) {
            	
            	//change GUI buttons accordingly
            	botStatus.setText("Invalid token! \nRefresh your token below:");
                
            }
           
    	} else {
    		
    		//shut down all bot stuff
    		shutDownBotAPI();
    		
    		//show buttons again
    		fileButton.setVisible(true);
    		exitButton.setVisible(true);
    		
    		//hide bot status
    		botStatus.setVisible(false);
    		
    	}
    		
    }    
    
    //safer method to shut down bot
    public void shutDownBotAPI() {
    	
    	api.shutdown();
		toggleButton.setText("Run");
        System.out.println("Bot has been terminated");
		startStop = true;
		
    }
    
    //scans all images in folder first to ensure no replacements are made
    public void scanImagesFolder() {
    	
    	//clear arraylist to restart position back to 0
    	fileNames.clear();
    	
    	//planning to allow user to change image destination depending on .jar location
    	File folder = new File(selectedDirectoryString);
    	
    	//scan through selected folder
        File[] listOfFiles = folder.listFiles();
        
        //this increment here sorts through the array for images that contain ONLY the keyword
        int j = 0;
        
        //loop through filenames
        for (int i = 0; i < listOfFiles.length; i++) {

        	//if filename = keyword, sort it to another array
            if (listOfFiles[i].isFile() && !imageKeyword.equals("null") && listOfFiles[i].getName().contains(imageKeyword)) {
            		
                    fileNames.add(listOfFiles[i].getName());
                    System.out.println(fileNames.get(j));
                    j++;
                
            }
        }        
        
        //increment exists to change file suffix to the correct number so there are no repeats
        increment = fileNames.size();
        System.out.println(increment);
    }
    
    ////UPDATE WHICH LINE IS READ AND THE APPROPRIATE VARIABLES OF DIRECTORY AND TOKEN FROM THE PREFERENCES FILE
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
    		token = prefs[1];
    		
    	} catch(IOException e) {  
    		e.printStackTrace();  
    	} 
    }
    
    //method to update preferences file
    public void updatePreferences(String fileName) throws IOException {
    	
    	//open file, replace values, and close file
    	PrintWriter writer = new PrintWriter(fileName);
    	writer.print(selectedDirectoryString + "\n" + token);
    	writer.close();
            
    }
    
}

