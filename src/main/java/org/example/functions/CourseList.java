package org.example.functions;

import java.util.ArrayList;
import java.util.List;

public class CourseList {
    List<Course> courses = new ArrayList<>();

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
