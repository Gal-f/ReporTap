package il.reportap;

public class ModelActivityInboxLab {
    private Integer id,messageId,isValueBool;
    private String sentTime,patientId,testName,text,measurement,component, fullName, dept, resultValue;
    private Boolean wasRead;

    public ModelActivityInboxLab(Integer id,Integer messageId, String sentTime, String patientId, String testName,
                                 String text, String measurement, String component, Integer isValueBool, String resultValue,
                                 String fullName, String dept, Boolean wasRead) {
        this.id = id;
        this.messageId=messageId;
        this.sentTime = sentTime.substring(0,sentTime.length()-3);
        this.patientId = patientId;
        this.testName = testName;
        this.text=text;
        this.measurement=measurement;
        this.component=component;
        this.isValueBool=isValueBool;
        this.resultValue=resultValue;
        this.fullName=fullName;
        this.dept=dept;
        this.wasRead=wasRead;
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

    public String getResultValue() {
        return resultValue;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDept() {
        return dept;
    }

    public Boolean getWasRead() { return wasRead; }

}

