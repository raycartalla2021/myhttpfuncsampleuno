package org.example.functions;

public class Course {
    private Long courseID;
    private String courseName;
    private Double rating;

    public Course() {
    }

    public Course(Long courseID, String courseName, Double rating) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.rating = rating;
    }

    public Long getCourseID() {
        return courseID;
    }

    public void setCourseID(Long courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
