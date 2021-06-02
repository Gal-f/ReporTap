package il.reportap;

public class ModelActivityInboxDr {
    private Integer id,isUrgent;
    private String sentTime,patientId,testName;

    public ModelActivityInboxDr(Integer id, Integer isUrgent, String sentTime, String patientId, String testName) {
        this.id = id;
        this.isUrgent = isUrgent;
        this.sentTime = sentTime.substring(0,sentTime.length()-3);
        this.patientId = patientId;
        this.testName = testName;
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

}

