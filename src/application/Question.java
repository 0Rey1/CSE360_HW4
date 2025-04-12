package application;
import java.time.LocalDateTime;

public class Question {
	private static int idCounter = 1;
	private int questionID;
	private String title;
	private String author;
	private String description;
	private LocalDateTime dateMade;
	private boolean concerning;
	private boolean staffMarked;
	
	public Question(String title, String description, String author) {
		this.questionID = idCounter++;
		this.title = title;
		this.author = author;
		this.description = description;
		this.dateMade = LocalDateTime.now();
	}
	
	public Question(int questionID, String title, String description, String author) {
		this.questionID = questionID;
		this.title = title;
		this.author = author;
		this.description = description;
		this.dateMade = LocalDateTime.now();
		this.concerning = false;
	}
	
	public boolean isValid() {
		return title != null && !title.trim().isEmpty() 
				&& description != null 
				&& !description.trim().isEmpty();
	}
	
	public int getQuestionID() { return questionID; }
	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}
	
	public String getTitle() { return title; }
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() { return description; }
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAuthor() { return author; }
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public LocalDateTime getDateMade() { return dateMade; }
	public void setDateMade(LocalDateTime dateMade) {
		this.dateMade = dateMade;
	}
	
	@Override
	public String toString() {
		return "Question{" +
				"ID=" + questionID +
				", Title='" + title + '\'' +
				", Author='" + author + '\'' + 
				", Description='" + description + '\'' +
				", DateMade=" + dateMade + 
				'}';
	}
	
	public boolean isConcerning() {
		return concerning;
	}
	public void setConcerning(boolean concerning) {
		this.concerning = concerning;
	}
	public boolean isStaffMarked() {
		return staffMarked;
	}
	
	public void setStaffMarked(boolean staffMarked) {
		this.staffMarked = staffMarked;
	}
}