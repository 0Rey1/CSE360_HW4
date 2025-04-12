package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;

public class ReviewManagementPage {
    private TableView<Question> tableView;
    private ObservableList<Question> questionData;

    public ReviewManagementPage() {
    }

    public void show(Stage primaryStage, DatabaseHelper databaseHelper, User currentUser) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        questionData = FXCollections.observableArrayList(databaseHelper.getAllReviewersReviews(currentUser.getUserName()));

        Button viewAnswersButton = new Button("View Answers");

        tableView = new TableView<>();
        tableView.setPrefHeight(300);

        TableColumn<Question, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuestionID()));
        TableColumn<Question, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        TableColumn<Question, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        TableColumn<Question, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        

        tableView.getColumns().addAll(idCol, titleCol, descCol, authorCol);
        tableView.setItems(questionData);
        
        viewAnswersButton.setOnAction(e -> {
        	Question selected = tableView.getSelectionModel().getSelectedItem();
        	if (selected == null) {
        		showAlert("Error", "No question selected.");
        		return;
        	}
        	new AnswerManagementPage().show(primaryStage, databaseHelper, currentUser, selected);
        });
        
        Button backButton = new Button("Back to Student Menu");
        backButton.setOnAction(e -> {
            new StudentHomePage(databaseHelper, currentUser).show(primaryStage);
        });
        
        root.getChildren().addAll(tableView, backButton);

        Scene scene = new Scene(root, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviews Management");
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
