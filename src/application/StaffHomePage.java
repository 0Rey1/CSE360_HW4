package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * This is homepage for all the staff members/account holders. It helps them to navigate to different resources by displaying various buttons.
 * It serves as a landing page basically, after a staff has successfully logged in.
 */
public class StaffHomePage {
    private DatabaseHelper databaseHelper;
    private User currentUser;

    public StaffHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Button reviewQuestionsButton = new Button("Review Questions and Answers");
        reviewQuestionsButton.setOnAction(e -> {
            new StaffReviewPage().show(primaryStage, databaseHelper, currentUser);
        });

        Button messageButton = new Button("Private Messages");
        messageButton.setOnAction(e -> {
            new PrivateMessagingPage().show(primaryStage, databaseHelper, currentUser);
        });

     // Button for logging out
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(a -> {
            UserLoginPage loginPage = new UserLoginPage(databaseHelper);
            loginPage.show(primaryStage);
        });
        root.getChildren().addAll(reviewQuestionsButton, messageButton, logoutButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Home Page");
        primaryStage.show();
    }
}
