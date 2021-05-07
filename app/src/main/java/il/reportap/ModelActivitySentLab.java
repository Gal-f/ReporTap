package il.reportap;

public class ModelActivitySentLab {
    private Integer id, isUrgent, isValueBool, testResult;
    private String patientId,sentTime,deptName,testName, confirmTime, text, measurementUnit, component, fullName;

    public ModelActivitySentLab(Integer id, String sent_time, String patient_id, String test_name, Integer isUrgent , String confirm_time, String dept_name,
                                String text, String measurementUnit, String component, Integer isValueBool, Integer testResult,
                                String fullName) {
        this.id = id;
        this.patientId = patient_id;
        this.sentTime = sent_time;
        this.isUrgent=isUrgent;
        this.deptName=dept_name;
        this.testName = test_name;
        this.confirmTime = confirm_time;
        this.text=text;
        this.measurementUnit=measurementUnit;
        this.component=component;
        this.isValueBool=isValueBool;
        this.testResult=testResult;
        this.fullName=fullName;

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

    public Integer getIsValueBool() {
        return isValueBool;
    }

    public Integer getTestResult() {
        return testResult;
    }

    public String getText() {
        return text;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public String getComponent() {
        return component;
    }

    public String getFullName() {
        return fullName;
    }
}
