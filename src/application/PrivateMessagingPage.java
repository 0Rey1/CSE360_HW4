package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class PrivateMessagingPage {
    private TableView<PrivateMessage> inboxTable;
    private TableView<PrivateMessage> sentTable;
    private ObservableList<PrivateMessage> inboxData;
    private ObservableList<PrivateMessage> sentData;
    
    public PrivateMessagingPage() {
    }
    
    public void show(Stage primaryStage, DatabaseHelper databaseHelper, User currentUser) {
        
        TabPane tabPane = new TabPane();
        
        // Inbox Tab
        Tab inboxTab = new Tab("Inbox");
        inboxTable = new TableView<>();
        inboxTable.setPrefHeight(300);
        
        TableColumn<PrivateMessage, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        
        TableColumn<PrivateMessage, String> senderCol = new TableColumn<>("Sender");
        senderCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSender()));
        
        TableColumn<PrivateMessage, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMessageText()));
        
        TableColumn<PrivateMessage, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimestamp().toString()));
        
        inboxTable.getColumns().addAll(idCol, senderCol, messageCol, timestampCol);
        inboxData = FXCollections.observableArrayList(databaseHelper.getPrivateMessages(currentUser.getUserName()));
        
        inboxTable.setItems(inboxData);
        VBox inboxBox = new VBox(10, inboxTable);
        inboxBox.setPadding(new Insets(10));
        inboxTab.setContent(inboxBox);
        
        // Sent Tab
        Tab sentTab = new Tab("Sent");
        sentTable = new TableView<>();
        sentTable.setPrefHeight(300);
        
        TableColumn<PrivateMessage, Number> sIdCol = new TableColumn<>("ID");
        sIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        
        TableColumn<PrivateMessage, String> recipientCol = new TableColumn<>("Recipient");
        recipientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRecipient()));
        
        TableColumn<PrivateMessage, String> sMessageCol = new TableColumn<>("Message");
        sMessageCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMessageText()));
        
        TableColumn<PrivateMessage, String> sTimestampCol = new TableColumn<>("Timestamp");
        sTimestampCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimestamp().toString()));
        
        sentTable.getColumns().addAll(sIdCol, recipientCol, sMessageCol, sTimestampCol);
        sentData = FXCollections.observableArrayList(databaseHelper.getSentMessages(currentUser.getUserName()));
        
        sentTable.setItems(sentData);
        VBox sentBox = new VBox(10, sentTable);
        sentBox.setPadding(new Insets(10));
        sentTab.setContent(sentBox);
        
        tabPane.getTabs().addAll(inboxTab, sentTab);
        
        // Message form to send a new message
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        
        TextField recipientField = new TextField();
        recipientField.setPromptText("Recipient Username");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here...");
        
        Button sendButton = new Button("Send Message");
        sendButton.setOnAction(e -> {
            String recipient = recipientField.getText();
            String messageText = messageArea.getText();
            if (recipient == null || recipient.trim().isEmpty() || messageText.trim().isEmpty()) {
                showAlert("Error", "Recipient and message cannot be empty.");
                return;
            }
            PrivateMessage pm = new PrivateMessage(currentUser.getUserName(), recipient, messageText);
            if (databaseHelper.addPrivateMessage(pm)) {
                showAlert("Success", "Message sent successfully.");
                // Refresh the sent messages table
                sentData = FXCollections.observableArrayList(databaseHelper.getSentMessages(currentUser.getUserName()));
                sentTable.setItems(sentData);
                recipientField.clear();
                messageArea.clear();
            } else {
                showAlert("Error", "Failed to send message.");
            }
        });
        formBox.getChildren().addAll(new Label("Send a new message:"), recipientField, messageArea, sendButton);
        
        // Create a root pane with a Back button (assumes a StudentHomePage exists)
        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setBottom(formBox);
        Button backButton = new Button("Back to Home");
        backButton.setOnAction(e -> {
            new StudentHomePage(databaseHelper, currentUser).show(primaryStage);
        });
        root.setTop(backButton);
        BorderPane.setMargin(backButton, new Insets(10));
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Private Messaging");
        primaryStage.show();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
