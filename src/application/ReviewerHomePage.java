package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class ReviewerHomePage {

	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	private User user;
	private DatabaseHelper databaseHelper;
	
	public ReviewerHomePage(User user, DatabaseHelper databaseHelper) {
		this.user = user;
		this.databaseHelper = databaseHelper;
    }
	
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(20);
    	
    	layout.setAlignment(Pos.CENTER);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the reviewer
	    Label reviewerLabel = new Label("Hello, Reviewer!"); 
	    reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    // HBox for Private Message User and View Questions buttons
	    HBox buttonBox = new HBox(20); // Add spacing
	    buttonBox.setAlignment(Pos.CENTER);
	 
	 // Button to navigate to the user's private message contact list page
	    Button contactListButton = new Button("Contact List");
	    contactListButton.setOnAction(a -> {
	    		// TODO: Link Ty's private messaging contact page here
	    		new PrivateMessagingPage().show(primaryStage, databaseHelper, user);
//	    		new UserRoleManagementPage(user, databaseHelper).show(primaryStage);
	    });
	    
	 // Button to navigate to the user's respective page based on their role
	    Button questionManagementButton = new Button("View Questions");
	    questionManagementButton.setOnAction(a -> {
	    		new QuestionManagementPage().show(primaryStage, databaseHelper, user);
	    });
	    
	 // Button to navigate to the user's respective page based on their role	    
	    Button viewAllReviewsButton = new Button("Your Reviews");
	    viewAllReviewsButton.setOnAction(a -> {
	    		new ReviewManagementPage().show(primaryStage, databaseHelper, user);
	    });
	    
	    buttonBox.getChildren().addAll(contactListButton, questionManagementButton);
	    
	    // Back button to return to WelcomeLoginPage
        Button backButton = new Button("Back to Welcome");
        backButton.setOnAction(e -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

	    layout.getChildren().addAll(reviewerLabel, buttonBox, backButton);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}