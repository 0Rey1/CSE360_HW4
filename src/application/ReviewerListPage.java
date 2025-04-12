package application;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.beans.property.SimpleStringProperty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewerListPage extends VBox {
    private DatabaseHelper databaseHelper;
    private TableView<User> userTable;
    private User currentAdmin;
    private final String[] AVAILABLE_ROLES = {"1", "2", "3", "4", "5"}; //we change/edit more roles here.

    public ReviewerListPage(User admin, DatabaseHelper databaseHelper) {
        this.currentAdmin = admin;
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	VBox layout = new VBox(20);
    	layout.setAlignment(Pos.CENTER);
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    	
        //header
        Label headerLabel = new Label("Reviewers");
        headerLabel.getStyleClass().add("page-header");

        //creating table
        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       
        //creating columns
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
//        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        TableColumn<User, String> rolesCol = new TableColumn<>("Current Rating");
               
        usernameCol.setCellValueFactory( data -> new SimpleStringProperty(data.getValue().getUserName()));
        nameCol.setCellValueFactory( data -> new SimpleStringProperty(data.getValue().getName()));
//        emailCol.setCellValueFactory( data -> new SimpleStringProperty(data.getValue().getEmail()));
        rolesCol.setCellValueFactory(data -> {
            ArrayList<String> rawRoles = data.getValue().getRoles();
            String fullRoleString = rawRoles.isEmpty() ? "" : rawRoles.get(0);

            // Split manually inside this page
            String[] splitRoles = fullRoleString.split(",");
            String rating = "None";

            for (String role : splitRoles) {
                role = role.trim();
                if (role.equals("1") || role.equals("2") || role.equals("3") || role.equals("4") || role.equals("5")) {
                    rating = role;
                    break;
                }
            }

            return new SimpleStringProperty(rating);
        });

        userTable.getColumns().add(usernameCol);
        userTable.getColumns().add(nameCol);
//        userTable.getColumns().add(emailCol);
        userTable.getColumns().add(rolesCol);
        
        // Get userinfo from database
        ArrayList<User> users = getRequestedReviewers();
        System.out.println(users.toString());
        
        for (User user: users) {
        	userTable.getItems().add(user);
        }
        
        //role management column here
        TableColumn<User, Void> actionCol = new TableColumn<>("Manage Rating");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button manageButton = new Button("Modify Rating");

            {
                manageButton.getStyleClass().add("manage-button");
                manageButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showRoleManagementDialog(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
//                    manageButton.setDisable(user.getUserName().equals(currentAdmin.getUserName()));
                    setGraphic(manageButton);
                }
            }
        });

        userTable.getColumns().add(actionCol);

        //buttons (control)

        refreshUserList();
        //refresh
        HBox buttonBar = new HBox(10);
        Button refreshButton = new Button("Refresh List");
        refreshButton.setOnAction(e -> refreshUserList());
        
        //back
        Button backButton = new Button("Back to Home");
        backButton.setOnAction(e -> new StudentHomePage(databaseHelper, currentAdmin).show(primaryStage));
        
//        Button acceptButton = new Button("Accept");
//        acceptButton.setOnAction(a -> {
//        	User user = userTable.getSelectionModel().getSelectedItem();
//        	handleRemoveRole(user, "requestedreviewer");
//			handleAddRole(user, "reviewer");
//        });
        
//        Button DenyButton = new Button("Deny");
//        DenyButton.setOnAction(a -> {
//        	User user = userTable.getSelectionModel().getSelectedItem();
//			handleRemoveRole(user, "requestedreviewer");
//        });

        buttonBar.getChildren().addAll(refreshButton, backButton);

        //adding all the components to the page.
        layout.getChildren().addAll(headerLabel, userTable, buttonBar);
        Scene userRoleManagementScene = new Scene(layout, 800, 400);
        
        //loading initial data..
//        refreshUserList();
        primaryStage.setScene(userRoleManagementScene);
        primaryStage.setTitle("User Role Management");
    }

    private void showRoleManagementDialog(User user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Manage Roles - " + user.getUserName());
//        dialog.setHeaderText("Current roles: " + String.join(", ", user.getRoles()));

        //dialog box
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        //select role
        ComboBox<String> roleComboBox = new ComboBox<>(
            FXCollections.observableArrayList(AVAILABLE_ROLES)
        );
        roleComboBox.setPromptText("Select a rating");

        //buttons (action)

        //add_Role btn
        Button addButton = new Button("Give Rating");
        //remove_Role btn
        Button removeButton = new Button("Remove Rating");

        addButton.setOnAction(e -> {
            String selectedRole = roleComboBox.getValue();
            if (selectedRole != null) {
                handleAddRole(user, selectedRole);
//                dialog.setHeaderText("Current roles: " + String.join(", ", user.getRoles()));
            }
        });

        removeButton.setOnAction(e -> {
            String selectedRole = roleComboBox.getValue();
            if (selectedRole != null) {
                handleRemoveRole(user, selectedRole);
//                dialog.setHeaderText("Current roles: " + String.join(", ", user.getRoles()));
            }
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, removeButton);

        content.getChildren().addAll(
            new Label("Select Role:"),
            roleComboBox,
            buttonBox
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }

    private void handleAddRole(User user, String role) {
        try {
            for (String rating : List.of("1", "2", "3", "4", "5")) {
                databaseHelper.removeRole(user.getUserName(), rating);
            }

            boolean success = databaseHelper.addRole(user.getUserName(), role);

            if (success) {
                showAlert("Success", "Rating updated to " + role, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to set rating", Alert.AlertType.ERROR);
            }

            refreshUserList();

        } catch (SQLException ex) {
            showAlert("Error", "Database error: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleRemoveRole(User user, String role) {
        try {
            if (role.equals("admin") && user.getRoles().size() == 1) {
                showAlert("Error", "Cannot remove the last admin role", Alert.AlertType.ERROR);
                return;
            }

            if (databaseHelper.removeRole(user.getUserName(), role)) {
                refreshUserList();
            } else {
                showAlert("Error", "Role cannot be removed", Alert.AlertType.ERROR);
            }
        } catch (SQLException ex) {
            showAlert("Error", "Database error: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshUserList() {
        ArrayList<User> users = getRequestedReviewers();

        users.sort((u1, u2) -> {
            String rating1 = extractRating(u1);
            String rating2 = extractRating(u2);

            try {
                int r1 = Integer.parseInt(rating1);
                int r2 = Integer.parseInt(rating2);
                return Integer.compare(r2, r1); 
            } catch (NumberFormatException e) {
                if (rating1.equals("None")) return 1;
                if (rating2.equals("None")) return -1;
                return 0;
            }
        });

        userTable.setItems(FXCollections.observableArrayList(users));
    }
    
    private String extractRating(User user) {
        ArrayList<String> rawRoles = user.getRoles();
        String fullRoleString = rawRoles.isEmpty() ? "" : rawRoles.get(0);

        String[] splitRoles = fullRoleString.split(",");
        for (String role : splitRoles) {
            role = role.trim();
            if (role.matches("[1-5]")) {
                return role;
            }
        }
        return "None";
    }

//    private void goBackToAdminHome() {
//        /*creating a new home page for admin and seting it as the current stage
//        (i dont know how to send the admin back to original admin hompage saving the changes, so this is kindof workaround lmk if you have a better solu.)*/
//        AdminHomePage adminHome = new AdminHomePage(currentAdmin);
//        getScene().setRoot(adminHome);
//    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private ArrayList<User> getRequestedReviewers() {
    	ArrayList<User> requestedUsers = new ArrayList<User>();
    	ArrayList<User> users = databaseHelper.getAllUsers();
        System.out.println(users.toString());
        for (User user : users) {
        	System.out.println(user);
        	ArrayList<String> role = databaseHelper.getUserRole(user.getUserName());
        	System.out.println(role);
            if (role.contains("reviewer")) {
            	System.out.println(requestedUsers);
            	requestedUsers.add(user);
            }
        }
        return requestedUsers;
    }
    
}