package il.reportap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginregister.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewMessage extends OptionsMenu {

    private Integer messageID; //TODO get this from the previous screen somehow
    private TextView sentTime, senderName, patientId, patientName, testName, componentName, measuredAmountValue, measurementUnit, boolValue, comments;
    private ImageView isUrgent;
    private boolean isTestValueBool;
    private ImageView wasRead, reply, forward; //TODO change these to ImageButtons

    private HashMap<String, Integer> deptMap;                     //Translates department name to it's corresponding ID
    private HashMap<String, Pair<Integer, String>> testTypeMap;   //Translates test type ID to it's corresponding name + result type (in this form: [name, [ID, resultType]] )
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        populateHashmaps();
        this.sentTime = findViewById(R.id.textViewDateTime);
        this.patientName = findViewById(R.id.textViewPatientName);
        this.patientId = findViewById(R.id.textViewPatientID);
        this.testName = findViewById(R.id.textViewTestName);
        this.componentName = findViewById(R.id.textViewComponentName);
        this.measuredAmountValue = findViewById(R.id.textViewMeasuredAmount);
        this.measurementUnit = findViewById(R.id.textViewMeasurementUnit);
        this.boolValue = findViewById(R.id.textViewBoolResult);
        this.comments = findViewById(R.id.textViewComments);
        this.isUrgent = findViewById(R.id.imageViewUrgent);

        // FOR TESTING ONLY. REMOVE THIS WHEN DONE.
/*        Button button =findViewById(R.id.ButtonTestMessageIDBox);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageID = ((EditText)findViewById(R.id.testMessageIDBox)).getText().toString();
                getMessage(messageID);
            }
        });
*/
        //END TESTING ONLY

        //  APPLY THESE 2 after adding and intent-extra in the linking page (the redirecting message in the inbox to be clicked on)
        if (getIntent().getExtras() != null) {
            messageID = getIntent().getIntExtra("MESSAGE_ID", 0);
            getMessage(this.messageID);
        }

        //TODO add other test results for the same patient (nice to have for version #1)

        // this.isTestValueBool = ((Pair)testTypeMap.get(this.testName.getText())).second.equals("boolean"); //TODO skip the hashmaps? isTestValueBool gets value in getMessage() already
        this.progressDialog = new ProgressDialog(this);
    }

    public void populateHashmaps(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_DEPTS_N_TESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                deptMap = new HashMap<String, Integer>();
                testTypeMap = new HashMap<String, Pair<Integer, String>>();

                try{
                    JSONObject entireResponse = new JSONObject(response);
                    JSONArray deptsArray = entireResponse.getJSONArray("departments");
                    JSONArray testTypesArray = entireResponse.getJSONArray("testTypes");
                    for (int i=0; i<deptsArray.length(); i++) {
                        JSONObject dept = deptsArray.getJSONObject(i);
                        deptMap.put(dept.getString("deptName"), dept.getInt("deptID"));
                    }
                    for (int i=0; i<testTypesArray.length(); i++){
                        JSONObject testType = testTypesArray.getJSONObject(i);
                        testTypeMap.put(testType.getString("testName"), new Pair<>(testType.getInt("testID"), testType.getString("resultType")));
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO Handle error response
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getMessage(Integer messageID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_GETMESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("error")) { // If there was any error along the way
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        JSONObject requestedMessage = jsonObject.getJSONObject("requestedMessage");
                        //patientName.setText(requestedMessage.getString("patientName"));
                        sentTime.setText(requestedMessage.getString("sentTime"));
                        patientId.setText(requestedMessage.getString("patientId"));
                        testName.setText(requestedMessage.getString("testName"));
                        componentName.setText(requestedMessage.getString("componentName"));
                        isTestValueBool = requestedMessage.getString("isValueBool").equals("1");
                        if (isTestValueBool){
                            boolValue.setText((requestedMessage.getString("testResultValue")).equals("1") ? "חיובית" : "שלילית");
                        } else {
                            measuredAmountValue.setText(requestedMessage.getString("testResultValue"));
                            measurementUnit.setText(requestedMessage.getString("measurementUnit"));
                        }
                        comments.setText(requestedMessage.getString("comments")); //TODO resize the textview to fit all the text
                        if (requestedMessage.getString("isUrgent").equals("1")){
                            isUrgent.setImageResource(R.drawable.redexclamation_trans);
                            ((TextView)findViewById(R.id.textViewUrgent)).setText("דחוף");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                isUrgent.setTooltipText("דחוף");
                            }
                        } else {
                            isUrgent.setImageResource(R.drawable.greyexclamation_trans);    //TODO resize grey and red triangles to same width
                            ((TextView)findViewById(R.id.textViewUrgent)).setText("לא דחוף");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                isUrgent.setTooltipText("לא דחוף");
                            }
                        }
                        updateFields();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO Handle error response
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("messageID", messageID.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void updateFields(){
        if (isTestValueBool){
            findViewById(R.id.linearLayoutBoolResult).setVisibility(View.VISIBLE);
            findViewById(R.id.linearLayoutMeasuredAmount).setVisibility(View.GONE);
        } else {
            findViewById(R.id.linearLayoutBoolResult).setVisibility(View.GONE);
            findViewById(R.id.linearLayoutMeasuredAmount).setVisibility(View.VISIBLE);
        }

        wasRead =  findViewById(R.id.imageButtonRead);
        reply = findViewById(R.id.imageButtonReply);
        forward = findViewById(R.id.imageButtonForward);

        wasRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsRead(messageID, SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmployeeNumber());
            }
        });
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply(messageID);

            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward(messageID);
            }
        });
    }

    public void markAsRead(Integer messageID, String userID){
        // show confirmation box
        // on approval show progressDialog
        // add approval time and user in DB table


    }

    public void reply(Integer messageID){

    }

    public void forward(Integer messageID){

    }
}