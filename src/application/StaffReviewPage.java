package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * StaffReviewPage displays the interface for all staff members to review and manage questions/answers and perform other changes.
 * This page serves as the main heart for a staff's account/use.
 */
public class StaffReviewPage {
    private TableView<Question> questionTable;
    private TableView<Answer> answerTable;
    private ObservableList<Question> questionData;
    private ObservableList<Answer> answerData;

    @SuppressWarnings("unchecked")
    public void show(Stage primaryStage, DatabaseHelper databaseHelper, User currentUser) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        //question table.
        questionTable = new TableView<>();
        questionTable.setPrefHeight(200);

        TableColumn<Question, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty
        		(data.getValue().getQuestionID()));

        TableColumn<Question, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty
        		(data.getValue().getTitle()));

        TableColumn<Question, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
        		data.getValue().getDescription()));

        TableColumn<Question, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty
        		(data.getValue().getAuthor()));

        TableColumn<Question, Boolean> concerningCol = new TableColumn<>("Concerning");
        concerningCol.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty
        		(data.getValue().isConcerning()));

        questionTable.getColumns().addAll(idCol, titleCol, descCol, authorCol, concerningCol);
        questionData = FXCollections.observableArrayList(databaseHelper.getAllQuestions());
        questionTable.setItems(questionData);

        //answers table.
        answerTable = new TableView<>();
        answerTable.setPrefHeight(200);

        TableColumn<Answer, String> answerTextCol = new TableColumn<>("Answer");
        answerTextCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty
        		(data.getValue().getAnswerText()));

        TableColumn<Answer, String> answerAuthorCol = new TableColumn<>("Author");
        answerAuthorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty
        		(data.getValue().getAuthor()));

        TableColumn<Answer, Boolean> answerConcerningCol = new TableColumn<>("Concerning");
        answerConcerningCol.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty
        		(data.getValue().isConcerning()));

        answerTable.getColumns().addAll(answerTextCol, answerAuthorCol, answerConcerningCol);

        //concerning / not-concerning button.
        Button markQuestionButton = new Button("Concerning / Not-concerning");
        //delete button.
        Button deleteQuestionButton = new Button("Delete Question");
        //notify instruct
        Button notifyInstructorButton = new Button("Notify Instructor");
        
        markQuestionButton.setOnAction(e -> {
            Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                boolean newState = !selectedQuestion.isConcerning();
                if (databaseHelper.markQuestionAsConcerning(selectedQuestion.getQuestionID(), newState)) {
                    selectedQuestion.setConcerning(newState);
                    questionTable.refresh();
                    showAlert("Success", "Question marked as " + (newState ? "concerning" : "not concerning"));
                } else {
                    showAlert("Error", "Failed to mark question");
                }
            } else {
                showAlert("Error", "Please select a question");
            }
        });
        
        //deleting the inappropriate question.
        deleteQuestionButton.setOnAction(e -> {
            Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                if (databaseHelper.deleteQuestion(selectedQuestion.getQuestionID())) {
                    questionData.remove(selectedQuestion);
                    answerData.clear();
                    showAlert("Success", "Question has been deleted");
                } else {
                    showAlert("Error", "Failed to delete question");
                }
            } else {
                showAlert("Error", "Please select a question to delete");
            }
        });
        

		notifyInstructorButton.setOnAction(e -> {
		    Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
		    if (selectedQuestion != null) {
		        boolean newState = !selectedQuestion.isStaffMarked();
		        if (databaseHelper.markQuestionForInstructor(selectedQuestion.getQuestionID(), newState)) {
		            selectedQuestion.setStaffMarked(newState);
		            questionTable.refresh();
		            showAlert("Success", "Question " + (newState ? "marked" : "unmarked") + " for instructor review");
		        } else {
		            showAlert("Error", "Failed to mark question");
		        }
		    } else {
		        showAlert("Error", "Please select a question");
		    }
		});


        Button sendMessageButton = new Button("Send Private Message");

        HBox buttonBox = new HBox(10, sendMessageButton);
        HBox concerningButtonBox = new HBox(10, markQuestionButton);
        	concerningButtonBox.getChildren().add(deleteQuestionButton);
        	concerningButtonBox.getChildren().add(notifyInstructorButton);

        //select question
        questionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                answerData = FXCollections.observableArrayList(databaseHelper.getAnswers(newSelection.getQuestionID()));
                answerTable.setItems(answerData);
            }
        });

        //message
        sendMessageButton.setOnAction(e -> {
            Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlert("Error", "Please select a question to send a message.");
                return;
            }
            showPrivateMessageDialog(primaryStage, databaseHelper, currentUser, selectedQuestion.getAuthor());
        });

        Button backButton = new Button("Back to Staff Menu");
        backButton.setOnAction(e -> {
            new StaffHomePage(databaseHelper, currentUser).show(primaryStage);
        });

        root.getChildren().addAll(
            new Label("Questions:"),
            questionTable,
            new Label("Answers:"),
            answerTable,
            concerningButtonBox,
            buttonBox,
            backButton
        );

        Scene scene = new Scene(root, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Review Page");
        primaryStage.show();
    }

    private void showPrivateMessageDialog(Stage primaryStage, DatabaseHelper databaseHelper, User currentUser, String recipient) {
        Stage dialog = new Stage();
        VBox dialogRoot = new VBox(10);
        dialogRoot.setPadding(new Insets(10));

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here...");
        messageArea.setPrefRowCount(3);
        
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String messageText = messageArea.getText().trim();
            if (!messageText.isEmpty()) {
                PrivateMessage pm = new PrivateMessage(currentUser.getUserName(), recipient, messageText);
                if (databaseHelper.addPrivateMessage(pm)) {
                    showAlert("Success", "Message sent successfully.");
                    dialog.close();
                } else {
                    showAlert("Error", "Failed to send message.");
                }
            }
        });

        dialogRoot.getChildren().addAll(
            new Label("Message to " + recipient + ":"),
            messageArea,
            sendButton
        );

        Scene dialogScene = new Scene(dialogRoot, 400, 250);
        dialog.setScene(dialogScene);
        dialog.setTitle("Send Private Message");
        dialog.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
