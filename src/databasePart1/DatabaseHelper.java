package databasePart1;
import java.sql.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;

import application.User;
import application.Question;
import application.Answer;
import application.PrivateMessage;
import application.questionReview;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			 //statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "name VARCHAR(255), "
				+ "email VARCHAR(255), "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(255), "
				+ "oneTimePassword VARCHAR(255))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // Create questions table
	    String questionTable = "CREATE TABLE IF NOT EXISTS questions ("
	    	    + "id INT, "
	    	    + "title VARCHAR(255) NOT NULL, "
	    	    + "description VARCHAR(1024) NOT NULL, "
	    	    + "author VARCHAR(255) NOT NULL, "
	    	    + "dateCreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	    	    + "concerning BOOLEAN DEFAULT FALSE,"//concerning column
	    		+ "staffMarked BOOLEAN DEFAULT FALSE)";//marked(instruc)
	    	statement.execute(questionTable);
	    
	    // Create answers table
	    String answerTable = "CREATE TABLE IF NOT EXISTS answers ("
	    	    + "id INT, "
	    	    + "questionId INT NOT NULL, "
	    	    + "answerText VARCHAR(1024) NOT NULL, "
	    	    + "author VARCHAR(255) NOT NULL, "
	    	    + "dateCreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	    	    + "review BOOLEAN DEFAULT FALSE, "
	    	    + "accepted BOOLEAN DEFAULT FALSE, "
	    	    + "concerning BOOLEAN DEFAULT FALSE)";
	    	statement.execute(answerTable);

        // Create questionReview table
	    String questionReviewTable = "CREATE TABLE IF NOT EXISTS questionReviews ("
            + "id INT, "
            + "questionId INT NOT NULL, "
            + "reviewText VARCHAR(1024) NOT NULL, "
            + "reviewer VARCHAR(255) NOT NULL)";
        statement.execute(questionReviewTable);
	    
	 // Create private messages table
	    String privateMessageTable = "CREATE TABLE IF NOT EXISTS private_messages ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "sender VARCHAR(255), "
	            + "recipient VARCHAR(255), "
	            + "messageText VARCHAR(1024), "
	            + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
	    statement.execute(privateMessageTable);
	    
	    
	    // staff
	    String staffReviewTable = "CREATE TABLE IF NOT EXISTS staff_reviews ("
	    	    + "id INT AUTO_INCREMENT PRIMARY KEY, "
	    	    + "questionId INT NOT NULL, "
	    	    + "staffUsername VARCHAR(255) NOT NULL, "
	    	    + "reviewText VARCHAR(1024) NOT NULL, "
	    	    + "reviewDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	    	    + "status VARCHAR(50) DEFAULT 'pending')";
	    	statement.execute(staffReviewTable);

	    	
	}



	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role, name, email) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			// String roles = user.getRoles().toString().replace("[", "").replace("]", "");
			String roles = String.join(",", user.getRoles());
			System.out.println("Roles: " + roles);
			System.out.println("Logging in with roles: " + roles);
			pstmt.setString(3, roles);
			pstmt.setString(4, user.getName());
			pstmt.setString(5, user.getEmail());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String oneTimePasswordQuery = "SELECT * FROM cse360users WHERE userName = ? AND oneTimePassword = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(oneTimePasswordQuery)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return true;
				}
			}
		}
		
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			// String roles = user.getRoles().toString().replace("[", "").replace("]", "");
			//System.out.println(roles);
			String roles = String.join(",", user.getRoles());
			System.out.println("Logging in with roles: " + roles);
			pstmt.setString(3, roles);
			//pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public ArrayList<String> getUserRole(String userName) {
	    ArrayList<String> roles = new ArrayList<>();
		String query = "SELECT role FROM cse360users WHERE userName = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	String rolesStr = rs.getString("role");
	            if (rolesStr != null && !rolesStr.isEmpty()) {
	            	for (String role: rolesStr.split(",")) {
	            		roles.add(role.trim());
	            	}
	            }
	        }
	        else
	        {
	        	return null;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return roles; // If no user exists or an error occurs
	}
	
	public boolean addPrivateMessage(PrivateMessage pm) {
	    String sql = "INSERT INTO private_messages (sender, recipient, messageText, timestamp) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, pm.getSender());
	        pstmt.setString(2, pm.getRecipient());
	        pstmt.setString(3, pm.getMessageText());
	        pstmt.setTimestamp(4, Timestamp.valueOf(pm.getTimestamp()));
	        int affectedRows = pstmt.executeUpdate();
	        if (affectedRows == 0) {
	            return false;
	        }
	        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                pm.setId(generatedKeys.getInt(1));
	            }
	        }
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public ArrayList<PrivateMessage> getPrivateMessages(String username) {
	    // Returns messages where the current user is the recipient
	    ArrayList<PrivateMessage> messages = new ArrayList<>();
	    String query = "SELECT * FROM private_messages WHERE recipient = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String sender = rs.getString("sender");
	            String recipient = rs.getString("recipient");
	            String messageText = rs.getString("messageText");
	            Timestamp ts = rs.getTimestamp("timestamp");
	            messages.add(new PrivateMessage(id, sender, recipient, messageText, ts.toLocalDateTime()));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return messages;
	}

	public ArrayList<PrivateMessage> getSentMessages(String username) {
	    // Returns messages sent by the current user
	    ArrayList<PrivateMessage> messages = new ArrayList<>();
	    String query = "SELECT * FROM private_messages WHERE sender = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String sender = rs.getString("sender");
	            String recipient = rs.getString("recipient");
	            String messageText = rs.getString("messageText");
	            Timestamp ts = rs.getTimestamp("timestamp");
	            messages.add(new PrivateMessage(id, sender, recipient, messageText, ts.toLocalDateTime()));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return messages;
	}

	
	// Get user
	public User getUser(String username) {
		String query = "SELECT * FROM cse360users WHERE userName = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1,  username);
			ResultSet rs = pstmt.executeQuery();
		
			if (rs.next()) {
				String name = rs.getString("name");
				String password = "";
				String email = rs.getString("email");
				ArrayList<String> roles = new ArrayList<>(
						Arrays.asList(rs.getString("role").split(", "))
						);
				return new User(username, name, password, roles, email);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Gets all users in cse360
	public ArrayList<User> getAllUsers() {
		String query = "SELECT * FROM cse360users";
		ArrayList<User> users = new ArrayList<User>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			// Selects all the information about a user
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String username = rs.getString("userName");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String roles = rs.getString("role");
				String password = "";
				
				users.add(new User(username, name, password, roles, email));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return users;	
	}
	
	private boolean isLastAdmin(String username) throws SQLException {
		String query = "SELECT COUNT(*) FROM cse360users WHERE role LIKE '%admin%'";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) <= 1;
			}
			
			return true;
		}
	}
	
	// add user roles
	public boolean addRole(String username, String role) throws SQLException {
		ArrayList<String> currentRoles = getUserRole(username);
		if (currentRoles.contains(role)) {
			return false;
		}
		
		currentRoles.add(role);
		return updateUserRoles(username, currentRoles);
	}
	
	// remove user roles
	public boolean removeRole(String username, String role) throws SQLException {
		if (role.contains("admin") && isLastAdmin(username)) {
			return false;
		}
		
		ArrayList<String> currentRoles = getUserRole(username);
		if (!currentRoles.contains(role)) {
			System.out.println("currentroles doesnt contain role");
			System.out.println(currentRoles.toString());
			return false;
		}
		
		currentRoles.remove(role);
		
		return updateUserRoles(username, currentRoles);
	}
	
	// Updates user roles
	private boolean updateUserRoles(String username, ArrayList<String> roles) throws SQLException {
		String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			String rolesStr = String.join(",", roles);
			pstmt.setString(1, rolesStr);
			pstmt.setString(2, username);
			
			return pstmt.executeUpdate() > 0;
		}
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	public String getPassword(String username) {
		String query = "SELECT password FROM cse360users WHERE username = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("password");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		
		return null;
	}
	
	// Reset Function for One Time Password
	public void setOneTimePassword(String userName, String tempPassword) throws SQLException {
		String query = "UPDATE cse360users SET oneTimePassword = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, tempPassword);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}
		
		query = "UPDATE cse360users SET password = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, "");
			pstmt.setString(2, userName);
		}
	}
	
	// Checks if oneTimePassword is valid
	public boolean isOneTimePasswordValid(String userName, String password) throws SQLException {
	    String query = "SELECT oneTimePassword FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            String storedTempPass = rs.getString("oneTimePassword");
	            return storedTempPass != null && storedTempPass.equals(password);
	        }
	    }
	    return false;
	}
	
	public void clearOneTimePassword(String userName) throws SQLException {
		String query = "UPDATE cse360users SET oneTimePassword = NULL WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.executeUpdate();
	    }
	}
	
	public void updatePassword(String userName, String newPassword) throws SQLException {
	    String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    }
	}
	
	public void removeUser(User user) throws SQLException {
		String query = "DELETE FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.execute();
		}
	}
	
	public boolean addAnswer(Answer a) {
        String sql = "INSERT INTO answers (id, questionId, answerText, author, review, accepted) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, a.getAnswerID());
        	pstmt.setInt(2, a.getQuestionID());
            pstmt.setString(3, a.getAnswerText());
            pstmt.setString(4, a.getAuthor());
            pstmt.setBoolean(5, a.isReview());
            pstmt.setBoolean(6, a.isAccepted());
            return pstmt.execute();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return false;
    }
	
	public ArrayList<Answer> getAnswers(int questionID) {
		String query = "SELECT * FROM answers WHERE questionId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();
			
			ArrayList<Answer> answers = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt("id");
				String answerText = rs.getString("answerText");
				String author = rs.getString("author");
				boolean accepted = rs.getBoolean("accepted");
                boolean isReview = rs.getBoolean("review");
				
				Answer answer = new Answer(id, questionID, answerText, author, isReview);
				answer.setAccepted(accepted);
				
				answers.add(answer);
			}
			return answers;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean updateAnswer(Answer answer) {
		String query = "UPDATE answers SET answerText = ?, accepted = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answer.getAnswerText());
			pstmt.setBoolean(2, answer.isAccepted());
			pstmt.setInt(3, answer.getAnswerID());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteAnswer(int answerID) {
		String query = "DELETE FROM answers WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answerID);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean acceptAnswer(int answerID) {
		String query = "UPDATE answers SET accepted = ? WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setBoolean(1, true);
			pstmt.setInt(2, answerID);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean addQuestion(Question question) {
		String query = "INSERT INTO questions (id, title, description, author) VALUES (?, ?, ?, ?)";
		
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, question.getQuestionID());
			pstmt.setString(2, question.getTitle());
			pstmt.setString(3, question.getDescription());
			pstmt.setString(4, question.getAuthor());
			
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean updateQuestion(int questionID, String title, String description) {
		String query = "UPDATE questions SET title = ?, description = ?  WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, title);
			pstmt.setString(2, description);
			pstmt.setInt(3, questionID);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public ArrayList<Question> getAllQuestions() {
		ArrayList<Question> questions = new ArrayList<>();
		String query = "SELECT * FROM questions";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String description = rs.getString("description");
				String author = rs.getString("author");
				boolean concerning = rs.getBoolean("concerning");
				boolean staffMarked = rs.getBoolean("staffMarked");
				Question question = new Question(id, title, description, author);
				question.setConcerning(concerning);
				question.setStaffMarked(staffMarked);
				questions.add(question);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return questions;
	}
	
	public boolean deleteQuestion(int questionID) {
		String query = "DELETE FROM questions WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public ArrayList<Question> searchQuestions(String keyword) {
		ArrayList<Question> questions = new ArrayList<>();
		String query = "SELECT * FROM questions WHERE lower(title) LIKE ? OR lower(description) LIKE ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			String search = "%" + keyword.toLowerCase() + "%";
			pstmt.setString(1, search);
			pstmt.setString(2, search);
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int questionID = rs.getInt("id");
				String title = rs.getString("title");
				String description = rs.getString("description");
				String author = rs.getString("author");
				questions.add(new Question(questionID, title, description, author));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return questions;
	}

    public ArrayList<questionReview> getQuestionReviews(int questionID) {
		String query = "SELECT * FROM questionReviews WHERE questionId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();
			
			ArrayList<questionReview> questionReviews = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt("id");
				String questionReviewText = rs.getString("reviewText");
				String reviewer = rs.getString("reviewer");
				
				questionReview review = new questionReview(id, questionID, reviewer, questionReviewText);
				
				questionReviews.add(review);
			}
			return questionReviews;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean addReview(questionReview r) {
        String sql = "INSERT INTO questionReviews (id, questionId, reviewText, reviewer) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, r.getQuestionReviewID());
        	pstmt.setInt(2, r.getQuestionID());
            pstmt.setString(3, r.getReviewText());
            pstmt.setString(4, r.getReviewer());
            return pstmt.execute();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return false;
    }
	
	public boolean updateReview(questionReview review) {
		String query = "UPDATE questionReviews SET reviewText = ?, WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, review.getReviewText());
			pstmt.setInt(2, review.getQuestionReviewID());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteReview(int questionReviewID) {
		String query = "DELETE FROM questionReviews WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionReviewID);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public ArrayList<Question> getAllReviewersReviews(String username) {
		ArrayList<Integer> questionIDs = new ArrayList<>();
		
		String query1 = "SELECT questionID FROM questionReviews WHERE reviewer = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query1)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int questionID = rs.getInt("questionId");
				questionIDs.add(questionID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String query2 = "SELECT questionID FROM answers WHERE author = ? and review = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query2)) {
			pstmt.setString(1, username);
			pstmt.setBoolean(2, true);
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int questionID = rs.getInt("questionId");
				if (!questionIDs.contains(questionID)) {
					questionIDs.add(questionID);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ArrayList<Question> questions = new ArrayList<>();
		
		for (int questionId : questionIDs)
		{
			String query3 = "SELECT * FROM questions WHERE questionId = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query3)) {
				pstmt.setInt(1, questionId);
				
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					int id = rs.getInt("id");
					String title = rs.getString("title");
					String description = rs.getString("description");
					String author = rs.getString("author");
					questions.add(new Question(id, title, description, author));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return questions;
	}
	
	//staff functionality to mark the question as concerning.
	public boolean markQuestionAsConcerning(int questionId, boolean concerning) {
	    String query = "UPDATE questions SET concerning = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setBoolean(1, concerning);
	        pstmt.setInt(2, questionId);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	//staff functionality to mark the answer as concerning.
	public boolean markAnswerAsConcerning(int answerId, boolean concerning) {
	    String query = "UPDATE answers SET concerning = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setBoolean(1, concerning);
	        pstmt.setInt(2, answerId);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	//marked(instructor)
	public boolean markQuestionForInstructor(int questionId, boolean marked) {
	    String query = "UPDATE questions SET staffMarked = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setBoolean(1, marked);
	        pstmt.setInt(2, questionId);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

}
