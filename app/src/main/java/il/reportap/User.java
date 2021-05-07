package il.reportap;

import java.io.Serializable;

public class User implements Serializable {

    public boolean isActive;
    private int id;
    private String employeeNumber, fullName, email, jobTitle, phoneNumber, deptType;
    private int department;


    public User(int id, String employeeNumber, String fullName, String email, String jobTitle, String phoneNumber, int department, String deptType) {
        this.id = id;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.deptType = deptType;
        this.isActive = false;
    }

    public User(String full_name, String employee_id, String role, int works_in_dept) {
        this.fullName = full_name;
        this.employeeNumber = employee_id;
        this.jobTitle = role;
        this.department = works_in_dept;
    }


    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getId() {
        return id;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getDepartment() {
        return department;
    }

    public String getDeptType() {
        return deptType;
    }
}