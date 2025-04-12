package application;

public class questionReview {
	private static int idCounter = 1;
	private int questionReviewID;
	private int questionID;
	private String reviewer;
	private String reviewText;
	
	public questionReview(int questionReviewID, int questionID, String reviewer, String reviewText) {
		this.questionReviewID = questionReviewID;
		this.questionID = questionID;
		this.reviewer = reviewer;
		this.reviewText = reviewText;
	}
	
	public questionReview(int questionID, String reviewer, String reviewText) {
		this.questionReviewID = idCounter++;
		this.questionID = questionID;
		this.reviewer = reviewer;
		this.reviewText = reviewText;
	}
	
	public int getQuestionReviewID() { return questionReviewID; }
	
	public int getQuestionID() { return questionID; }
	
	public String getReviewer() { return reviewer; }
	
	public String getReviewText() { return reviewText; }
	public void setReviewText(String newReviewText) {
		this.reviewText = newReviewText;
	}
}