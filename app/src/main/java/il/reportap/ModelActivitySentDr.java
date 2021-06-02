package il.reportap;

public class ModelActivitySentDr {
    private Integer id;
    private String patientId,patientName,sentTime,text,senderName,testName, confirmTime;

    public ModelActivitySentDr(Integer id, String sent_time,String patient_name, String text,String sender_name, String patient_id,  String test_name, String confirm_time) {
        this.id = id;
        this.patientId = patient_id;
        this.sentTime = sent_time.substring(0,sent_time.length()-3);
        this.text = text;
        this.senderName = sender_name;
        this.testName = test_name;
        this.confirmTime = confirm_time;
        this.patientName=patient_name;
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

    public String getText() {
        return text;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getTestName() {
        return testName;
    }

    public String getConfirmTime() {
        return confirmTime;
    }
    public String getPatientName() {
        return patientName;
    }

}
