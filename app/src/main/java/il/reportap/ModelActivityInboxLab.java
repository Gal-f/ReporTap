package il.reportap;

public class ModelActivityInboxLab {
    private Integer id,messageId,isValueBool;
    private String sentTime,patientId,testName,text,measurement,component, fullName, dept;
    private Float resultValue;

    public ModelActivityInboxLab(Integer id,Integer messageId, String sentTime, String patientId, String testName,
                                 String text, String measurement, String component, Integer isValueBool, Float resultValue,
                                 String fullName, String dept) {
        this.id = id;
        this.messageId=messageId;
        this.sentTime = sentTime;
        this.patientId = patientId;
        this.testName = testName;
        this.text=text;
        this.measurement=measurement;
        this.component=component;
        this.isValueBool=isValueBool;
        this.resultValue=resultValue;
        this.fullName=fullName;
        this.dept=dept;
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

    public Integer getMessageId() {
        return messageId;
    }

    public Integer getIsValueBool() {
        return isValueBool;
    }

    public String getText() {
        return text;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getComponent() {
        return component;
    }

    public Float getResultValue() {
        return resultValue;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDept() {
        return dept;
    }
}

