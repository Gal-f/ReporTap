package il.reportap;

public class ModelActivitySentLab {
    private Integer id, isUrgent;
    private String patientId,sentTime,deptName,testName, confirmTime;

    public ModelActivitySentLab(Integer id, String sent_time, String patient_id, String test_name, Integer isUrgent , String confirm_time, String dept_name) {
        this.id = id;
        this.patientId = patient_id;
        this.sentTime = sent_time;
        this.isUrgent=isUrgent;
        this.deptName=dept_name;
        this.testName = test_name;
        this.confirmTime = confirm_time;
    }

    public Integer getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getSentTime() {
        return sentTime;
    }

    public Integer getIsUrgent() {
        return isUrgent;
    }

    public String getDeptName() {
        return deptName;
    }

    public String getTestName() {
        return testName;
    }

    public String getConfirmTime() {
        return confirmTime;
    }
}
