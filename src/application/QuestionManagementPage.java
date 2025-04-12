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

public class QuestionManagementPage {
    private TableView<Question> tableView;
    private ObservableList<Question> questionData;
    private int nextQuestionId = 1;

    public QuestionManagementPage() {
    }

    @SuppressWarnings("unchecked")
	public void show(Stage primaryStage, DatabaseHelper databaseHelper, User currentUser) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        questionData = FXCollections.observableArrayList(databaseHelper.getAllQuestions());

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");

        HBox formBox = new HBox(10, new Label("Title:"), titleField,
                               new Label("Description:"), descriptionArea);
        formBox.setSpacing(10);

        // Buttons
        Button addButton = new Button("Add Question");
        Button updateButton = new Button("Update Selected");
        Button deleteButton = new Button("Delete Selected");
        Button searchButton = new Button("Search");
        TextField searchField = new TextField();
        searchField.setPromptText("Keyword");
        Button viewAnswersButton = new Button("View Answers");

        // Create button box
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, searchField, searchButton, viewAnswersButton);

        //concerning button for staff
        if (currentUser.getRoles().contains("staff")) {
            Button markConcerningButton = new Button("Concerning");
            buttonBox.getChildren().add(markConcerningButton);

            markConcerningButton.setOnAction(e -> {
                Question selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    boolean newState = !selected.isConcerning();
                    if (databaseHelper.markQuestionAsConcerning(selected.getQuestionID(), newState)) {
                        selected.setConcerning(newState);
                        tableView.refresh();
                        showAlert("Success", "Question marked as " + (newState ? "concerning" : "not concerning"));
                    } else {
                        showAlert("Error", "Failed to mark question");
                    }
                } else {
                    showAlert("Error", "Please select a question");
                }
            });
        }

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

        //concerning
        TableColumn<Question, Boolean> concerningCol = new TableColumn<>("Concerning");
        concerningCol.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isConcerning()));

        //column only for instructors
        if (currentUser.getRoles().contains("instructor")) {
            TableColumn<Question, String> staffMarkedCol = new TableColumn<>("Staff Marked");
            staffMarkedCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(data.getValue().isStaffMarked() ? "Yes" : ""));
            tableView.getColumns().add(staffMarkedCol);
        }
        
        tableView.getColumns().addAll(idCol, titleCol, descCol, authorCol, concerningCol);
        tableView.setItems(questionData);

        addButton.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String author = currentUser.getUserName();
            Question newQuestion = new Question(title, description, author);
            
            if (!newQuestion.isValid()) {
                showAlert("Error", "Title and Description cannot be empty.");
                return;
            }

            if (databaseHelper.addQuestion(newQuestion)) {
                questionData.add(newQuestion);
                clearFields(titleField, descriptionArea);
            } else {
                showAlert("Error", "Failed to add question.");
            }
        });

        updateButton.setOnAction(e -> {
            Question selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "No question selected.");
                return;
            }

            if (!selected.getAuthor().equals(currentUser.getUserName())) {
                showAlert("Error", "Not your question to edit.");
                return;
            }

            String newTitle = titleField.getText();
            String newDescription = descriptionArea.getText();
            if (newTitle.trim().isEmpty() || newDescription.trim().isEmpty()) {
                showAlert("Error", "Title and Description cannot be empty.");
                return;
            }

            if (databaseHelper.updateQuestion(selected.getQuestionID(), newTitle, newDescription)) {
                selected.setTitle(newTitle);
                selected.setDescription(newDescription);
                tableView.refresh();
                clearFields(titleField, descriptionArea);
            } else {
                showAlert("Error", "Failed to update question.");
            }
        });

        deleteButton.setOnAction(e -> {
            Question selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "No question selected.");
                return;
            }

            if (!selected.getAuthor().equals(currentUser.getUserName())) {
                showAlert("Error", "Not your question to delete");
                return;
            }

            if (databaseHelper.deleteQuestion(selected.getQuestionID())) {
                questionData.remove(selected);
            } else {
                showAlert("Error", "Failed to delete question.");
            }
        });

        searchButton.setOnAction(e -> {
            String keyword = searchField.getText();
            if (keyword == null || keyword.trim().isEmpty()) {
                tableView.setItems(questionData);
                return;
            }
            tableView.setItems(FXCollections.observableArrayList(databaseHelper.searchQuestions(keyword)));
        });

        viewAnswersButton.setOnAction(e -> {
            Question selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "No question selected.");
                return;
            }
            new AnswerManagementPage().show(primaryStage, databaseHelper, currentUser, selected);
        });

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            if (currentUser.getRoles().contains("staff")) {
                new StaffHomePage(databaseHelper, currentUser).show(primaryStage);
            } else {
                new StudentHomePage(databaseHelper, currentUser).show(primaryStage);
            }
        });

        root.getChildren().addAll(formBox, buttonBox, tableView, backButton);
        Scene scene = new Scene(root, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Question Management");
        primaryStage.show();
    }

    private void clearFields(TextField titleField, TextArea descriptionArea) {
        titleField.clear();
        descriptionArea.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
