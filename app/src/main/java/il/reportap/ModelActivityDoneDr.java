package il.reportap;

public class ModelActivityDoneDr {
    private Integer id;
    private String sentTime,patientId,testName,text,confirmUser;

    public ModelActivityDoneDr(Integer id, String sentTime, String patientId, String testName, String text,String confirmUser) {
        this.id = id;
        this.text=text;
        this.confirmUser=confirmUser;
        this.sentTime = sentTime;
        this.patientId = patientId;
        this.testName = testName;
    }

    public String getText() {
        return text;
    }

    public String getConfirmUser() {
        return confirmUser;
    }

    public Integer getId(){return id;}

    public String getSentTime() {
        return sentTime;
    }

    public String getPatientId() {
        return patientId;
    }
    public String getTestName() {
        return testName;
    }

}

