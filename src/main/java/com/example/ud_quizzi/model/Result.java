package com.example.ud_quizzi.model;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable {

    private static final long serialVersionUID = 4475231609088284863L;

    private int resultID;
    private int examID;
    private int studentID;
    private double score;
    private Date submittedDate;
    private String description; // Tên đề thi

    public Result() {}

    public Result(int examID, int studentID, double score, Date submittedDate) {
        this.examID = examID;
        this.studentID = studentID;
        this.score = score;
        this.submittedDate = submittedDate;
    }


    public Result(int resultID, int examID, int studentID, double score, Date submittedDate) {
        this.resultID = resultID;
        this.examID = examID;
        this.studentID = studentID;
        this.score = score;
        this.submittedDate = submittedDate;
    }

    public int getResultID() { return resultID; }
    public void setResultID(int resultID) { this.resultID = resultID; }

    public int getExamID() { return examID; }
    public void setExamID(int examID) { this.examID = examID; }

    public int getStudentID() { return studentID; }
    public void setStudentID(int studentID) { this.studentID = studentID; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public Date getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(Date submittedDate) { this.submittedDate = submittedDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Result [resultID=" + resultID + ", examID=" + examID + ", studentID=" + studentID +
                ", score=" + score + ", submittedDate=" + submittedDate + ", description=" + description + "]";
    }
}
