package il.reportap;

public class User {

    private int id;
   // private String username;
    private String employeeNumber;
    private String fullName;
    private String jobTitle;
    private String phoneNumber;
    private int department;

    public User(int id, String employeeNumber, String fullName, String jobTitle, String phoneNumber, int department) {
        this.id = id;
       // this.username = username;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.phoneNumber = phoneNumber;
        this.department = department;
    }

    public int getId() {
        return id;
    }

 /*   public String getUsername() {
        return username;
    }*/

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

    public int getDepartment() {
        return department;
    }

}