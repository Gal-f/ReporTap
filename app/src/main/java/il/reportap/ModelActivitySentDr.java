package il.reportap;

public class ModelActivitySentDr {
    private Integer id;
    private String patient_id,sent_time,text,sender_name,test_name, confirm_time;

    public ModelActivitySentDr(Integer id, String sent_time, String text,String sender_name, String patient_id,  String test_name, String confirm_time) {
        this.id = id;
        this.patient_id = patient_id;
        this.sent_time = sent_time;
        this.text = text;
        this.sender_name = sender_name;
        this.test_name = test_name;
        this.confirm_time = confirm_time;
    }

    public Integer getId() {
        return id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getSent_time() {
        return sent_time;
    }

    public String getText() {
        return text;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getTest_name() {
        return test_name;
    }

    public String getConfirm_time() {
        return confirm_time;
    }
}
