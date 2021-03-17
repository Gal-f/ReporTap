package il.reportap;

public class User {

    private int id;
    private String username, employeeNumber, fullName, jobTitle, phoneNumber;
    //private String gender;

    public User(int id, String username, String employeeNumber, String fullName, String jobTitle, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}