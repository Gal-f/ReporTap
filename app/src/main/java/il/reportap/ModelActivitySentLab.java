package il.reportap;

public class ModelActivitySentLab {
    private Integer id, isUrgent, isValueBool;
    private String patientId,sentTime,deptName,testResult,testName, confirmTime, text, measurementUnit, component, fullName,fullNameP;

    public ModelActivitySentLab(Integer id, String sent_time, String patient_id,String fullNameP, String test_name, Integer isUrgent , String confirm_time, String dept_name,
                                String text, String measurementUnit, String component, Integer isValueBool, String testResult,
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
        this.fullNameP=fullNameP;

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

    public String getTestResult() {
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

    public String getFullNameP(){ return fullNameP; }
}
