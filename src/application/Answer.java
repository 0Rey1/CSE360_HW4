package application;


public class Answer {
	private static int idCounter = 1;
	private int answerID;
	private int questionID;
	private String author;
	private String answerText;
	private boolean isAccepted;
	private boolean isReview;
	private boolean concerning;
	
	public Answer(int answerID, int questionID, String answerTheText, String author, boolean isReview) {
		this.answerID= answerID;
		this.answerText= answerTheText;
		this.questionID= questionID;
		this.author= author;
		this.isReview = isReview;
		this.isAccepted = false;
	}
	
	public Answer(int questionID, String answerTheText, String author, boolean isReview) {
		this.answerID= idCounter++;
		this.answerText= answerTheText;
		this.questionID= questionID;
		this.author= author;
		this.isAccepted = false;
		this.isReview = isReview;
	}
	
	public boolean isValid() {
		return answerText != null &&
				!answerText.trim().isEmpty();
	}
	
	public int getAnswerID() { return answerID; }
	public void setAnswerID(int answerID) {
		this.answerID = answerID;
	}
	
	public int getQuestionID() { return questionID; }
	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}
	
	public String getAnswerText() { return answerText; }
	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
	
	public String getAuthor() { return author; }
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public boolean isAccepted() { return isAccepted; }
	public void setAccepted(boolean accepted) { 
		isAccepted = accepted;
	}
	
	public boolean isReview() { return isReview; }
	
	@Override
	public String toString() {
		return "Answer{" +
				"ID=" + answerID +
				", QuestionID='" + questionID + '\'' +
				", AnswerText='" + answerText + '\'' + 
				", Author='" + author + '\'' +
				", Accepted=" + isAccepted +
				'}';
	}
	public boolean isConcerning() {
		return concerning;
	}
	
	public void setConcerning(boolean concerning) {
		this.concerning = concerning;
	}
}
