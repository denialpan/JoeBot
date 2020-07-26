import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class Main extends Application {
    
	//initialize bot api
	JDA api;
	
	//initialize button toggle functionality
	boolean startStop = true;
    
	//initialize buttons
    Button toggleButton;
    Button exitButton;
    
    //initialize window
    Stage primaryStage;

    //something here that i need but i dont know why
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    
    //where all the important stuff is
    public void start(Stage primaryStage) {
        //set title window
    	primaryStage.setTitle("JoeBot");

        //create toggle button
        toggleButton = new Button();
        toggleButton.setText("Run");
        toggleButton.setOnAction(e -> toggleBot());
        toggleButton.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");
        
        //create toggle button
        exitButton = new Button();
        exitButton.setText("Exit");
        exitButton.setOnAction(e -> {primaryStage.close();});
        exitButton.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");

        //GUI text so it doesn't look boring
        Label mainDescription = new Label("The JoeBot GUI");
        mainDescription.setStyle("-fx-font: 20 Consolas; -fx-base: #202020;");
        mainDescription.setTextFill(Color.web("#FFFFFF"));
        
        //GridPane with 10px padding around edge
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        //organize buttons and text in the gridpane
        GridPane.setConstraints(mainDescription, 0, 0);
        GridPane.setConstraints(toggleButton, 0, 1);
        GridPane.setConstraints(exitButton, 1, 1);
        
        //create a background fill 
        BackgroundFill background_fill = new BackgroundFill(Color.gray(0.15), CornerRadii.EMPTY, Insets.EMPTY); 

        //create Background 
        Background background = new Background(background_fill); 

        //set background 
        grid.setBackground(background); 
        
        //put it all together into a window and display it
        grid.getChildren().addAll(mainDescription, toggleButton, exitButton);        
        Scene scene = new Scene(grid, 256, 87, Color.GRAY);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        //this is here because somehow you can't close the program right as you open it, which is stupid
        primaryStage.setOnCloseRequest(event -> {
        try {
        	shutDownBotAPI();
        } catch (Exception e) {
        	event.consume();
        };});
    }
    
    //toggle bot method
    public void toggleBot() {
    	
    	//detects if startStop is true or false. Pretty sure its obvious what happens when its true or false
    	if (startStop) {
    		
    		//start the bot api and connect the bot to servers
    		System.out.println("Bot has been turned on");
            String token = "bot token";
            toggleButton.setText("Stop");
            
            try {
            	
                api = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
                api.addEventListener(new Commands());

            } catch (Exception e) {
            	
                e.printStackTrace();

            }
            
            startStop = false;
            exitButton.setVisible(false);
            
    	} else {
    		
    		//shut down all bot stuff
    		shutDownBotAPI();
    		exitButton.setVisible(true);
    		
    	}
    		
    }    
    
    //safer method to shut down bot
    public void shutDownBotAPI() {
    	api.shutdown();
		toggleButton.setText("Run");
        System.out.println("Bot has been terminated");
		startStop = true;
    }
    
}

