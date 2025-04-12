package application;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

/**
 * The StudentHomePage class represents the student home interface.
 * It allows students to navigate to various functionalities such as question management and private messaging,
 * and includes a back button to return to the welcome page.
 */
public class StudentHomePage {

    private DatabaseHelper databaseHelper;
    private User currentUser;

    // Constructor takes the database helper and current user for proper navigation.
    public StudentHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Button to navigate to the Question Management Page
        Button viewQuestionsButton = new Button("View Questions");
        viewQuestionsButton.setOnAction(e -> {
            new QuestionManagementPage().show(primaryStage, databaseHelper, currentUser);
        });
        
        // Button to navigate to the Private Messaging Page
        Button privateMessagingButton = new Button("Private Messaging");
        privateMessagingButton.setOnAction(e -> {
            new PrivateMessagingPage().show(primaryStage, databaseHelper, currentUser);
        });
        
        // Back button to return to the Welcome Page
        Button backButton = new Button("Back to Welcome");
        backButton.setOnAction(e -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, currentUser);
        });
        
        Button requestButton = new Button("Request to be a reviewer");
        requestButton.setOnAction(e ->{
        	try {
				if (databaseHelper.addRole(currentUser.getUserName(), "requestedreviewer")) {
				    System.out.print("Success");
				} else {
					System.out.print("Failure");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        });
        
        Button reviewerListButton = new Button("List of Reviewers");
        reviewerListButton.setOnAction(e -> {
        	new ReviewerListPage(currentUser, databaseHelper).show(primaryStage);
        });
        
        // Add all buttons to the layout
        root.getChildren().addAll(viewQuestionsButton, privateMessagingButton, backButton, requestButton, reviewerListButton);
        
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Homepage");
        primaryStage.show();
    }
}
