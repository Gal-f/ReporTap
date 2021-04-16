package il.reportap;

public class User {

    private int id;
   // private String username;
    private String employeeNumber, fullName, email, jobTitle, phoneNumber;
    private int department;

    public User(int id, String employeeNumber, String fullName, String email,  String jobTitle, String phoneNumber, int department) {
        this.id = id;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.phoneNumber = phoneNumber;
        this.department = department;
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

}