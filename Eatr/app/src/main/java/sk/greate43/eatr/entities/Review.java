package sk.greate43.eatr.entities;

import java.io.Serializable;

public class Review implements Serializable {
    private String reviewGivenBy;
    private String reviewId;
    private String userId;
    private String orderId;
    private String reviewType;
    private String questionOne;
    private String questionTwo;
    private String questionThree;
    private double questionOneAnswer;
    private double questionTwoAnswer;
    private double questionThreeAnswer;

    public Review() {
    }

    public String getQuestionOne() {
        return questionOne;
    }

    public void setQuestionOne(String questionOne) {
        this.questionOne = questionOne;
    }

    public String getQuestionTwo() {
        return questionTwo;
    }

    public void setQuestionTwo(String questionTwo) {
        this.questionTwo = questionTwo;
    }

    public String getQuestionThree() {
        return questionThree;
    }

    public void setQuestionThree(String questionThree) {
        this.questionThree = questionThree;
    }

    public double getQuestionTwoAnswer() {
        return questionTwoAnswer;
    }

    public void setQuestionTwoAnswer(double questionTwoAnswer) {
        this.questionTwoAnswer = questionTwoAnswer;
    }

    public double getQuestionThreeAnswer() {
        return questionThreeAnswer;
    }

    public void setQuestionThreeAnswer(double questionThreeAnswer) {
        this.questionThreeAnswer = questionThreeAnswer;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public double getQuestionOneAnswer() {
        return questionOneAnswer;
    }

    public void setQuestionOneAnswer(double questionOneAnswer) {
        this.questionOneAnswer = questionOneAnswer;
    }

    public String getReviewGivenBy() {
        return reviewGivenBy;
    }

    public void setReviewGivenBy(String reviewGivenBy) {
        this.reviewGivenBy = reviewGivenBy;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
