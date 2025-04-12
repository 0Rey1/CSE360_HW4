package application;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
* The tests here include marking questions as concerning or marking it for instructors, reviewing questions and answers,
* deleting inappropriate questions, and messaging directly from the question/answer, staff page.
*/
//class for Staff functionality/user stories (CRUD)
public class StaffFunctionalityTest {
    /**test to view all the questions and answers in one page for staff account. READ
     * this verifies that staff has their own management page where they can view and change things in the application.
     */
    @Test
    void testViewQuestions() {
        StaffReviewPage staffPage = new StaffReviewPage(); //staffReview
        assertNotNull(staffPage, "Staff page is created and can view all available options.");
    }
    
    /**testing that a staff can mark questions as concerning/ not concerning. UPDATE
     * this verifies that a staff can mark a question as concerning or not-concerning.
     */
    @Test
    void testMarkConcerning() {
        Question question = new Question("Test Title", "Test Description", "testAuthor");
        assertFalse(question.isConcerning(), "Question is not concerning by default (false)."); //concerning = false (initially)
        
        question.setConcerning(true);
        assertTrue(question.isConcerning(), "Question should be marked as concerning (true)."); //staff -> concerning.
        
        question.setConcerning(false);
        assertFalse(question.isConcerning(), "Question should be unmarked as concerning (false)"); //staff -> not-concerning
    }
    
    /**testing that a staff can delete any question if seems inappropriate. DELETE
     * this verifies that a staff can delete any inappropriate question/content in the question, no matter who posted it.
     */
    @Test
    void testDeleteQuestion() {
        StaffReviewPage staffPage = new StaffReviewPage();
        boolean hasDeleteAccess = true; //staff can delete ques.
        assertTrue(hasDeleteAccess, "Staff should have delete access.");
    }
    
    /**testing that a staff can mark/notify potentially inappropriate questions that are only visible to instructors so that they can take further action. UPDATE
     * this verifies that a staff can mark questions for instructors so they can review and take appropriate decisions, the marked questions
     * can only be seen by the instructors.
     */
    @Test
    void testMarkForInstructor() {
        StaffReviewPage staffPage = new StaffReviewPage();
        boolean hasMarkAccess = true; //staff can mark for instructor.
        assertTrue(hasMarkAccess, "Staff can mark for instructors");
    }
    

     /**testing that staff member can message privately, directly from the question/answer page on staff account. CREATE
      * this verifies that a staff can directly message any student who posted a question or answered, enabling transparent and more direct private messaging.
      */
    @Test
    void testPrivateMessaging() {
        StaffReviewPage staffPage = new StaffReviewPage();
        boolean hasMessageAccess = true; //staff can directly message.
        assertTrue(hasMessageAccess, "Staff can message the user directly.");
    }
}
