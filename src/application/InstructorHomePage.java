package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * The StudentHomePage class represents the student home interface.
 * It allows students to navigate to various functionalities such as question management and private messaging,
 * and includes a back button to return to the welcome page.
 */
public class InstructorHomePage {

    private DatabaseHelper databaseHelper;
    private User currentUser;

    // Constructor takes the database helper and current user for proper navigation.
    public InstructorHomePage(DatabaseHelper databaseHelper, User currentUser) {
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
        
        // Go to reviewer request page
        Button reviewerRequestButton = new Button("Reviewer Requests");
        reviewerRequestButton.setOnAction(e -> {
        	new reviewerRequestPage(currentUser, databaseHelper).show(primaryStage);
        });
        
        // Add all buttons to the layout
        root.getChildren().addAll(viewQuestionsButton, privateMessagingButton, backButton, reviewerRequestButton);
        
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor Home Page");
        primaryStage.show();
    }
}
