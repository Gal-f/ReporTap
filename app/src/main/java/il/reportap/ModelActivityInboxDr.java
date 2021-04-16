package il.reportap;

public class ModelActivityInboxDr {
    private Integer id,isUrgent;
    private String sentTime,patientId,testName;

    public ModelActivityInboxDr(Integer id, Integer isUrgent, String sentTime, String patientId, String testName) {
        this.id = id;
        this.isUrgent = isUrgent;
        this.sentTime = sentTime;
        this.patientId = patientId;
        this.testName = testName;
    }

    public ModelActivityInboxDr() {
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

    public Integer getIsUrgent() {
        return isUrgent;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setUrgent(Integer urgent) {
        isUrgent = urgent;
    }
}

