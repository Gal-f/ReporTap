package il.reportap;

public class ModelActivity {
    private String sentTime,messageId,patientId,testName;
    private Boolean isUrgent;

    public ModelActivity(String sentTime, String patientId, String testName, Boolean isUrgent) {
        this.sentTime = sentTime;
        this.patientId = patientId;
        this.testName = testName;
        this.isUrgent = isUrgent;
    }

    public String getSentTime() {
        return sentTime;
    }

    public String getPatientId() {
        return patientId;
    }
    public String getTestName() {
        return testName;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }
}

