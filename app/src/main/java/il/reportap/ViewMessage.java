package il.reportap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
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

public class ViewMessage extends AppCompatActivity {

    private String messageID; //TODO get this from the previous screen somehow
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

        getMessage(this.messageID);
        //TODO add other test results for the same patient (nice to have for version #1)

        // this.isTestValueBool = ((Pair)testTypeMap.get(this.testName.getText())).second.equals("boolean"); //TODO skip the hashmaps? isTestValueBool gets value in getMessage() already
        if (this.isTestValueBool){
            findViewById(R.id.linearLayoutBoolResult).setVisibility(View.VISIBLE);
            findViewById(R.id.linearLayoutMeasuredAmount).setVisibility(View.GONE);
        } else {
            findViewById(R.id.linearLayoutBoolResult).setVisibility(View.GONE);
            findViewById(R.id.linearLayoutMeasuredAmount).setVisibility(View.VISIBLE);
        }

        this.wasRead =  findViewById(R.id.imageButtonRead);
        this.reply = findViewById(R.id.imageButtonReply);
        this.forward = findViewById(R.id.imageButtonForward);

        this.wasRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsRead(messageID, SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmployeeNumber());
            }
        });
        this.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply(messageID);

            }
        });
        this.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward(messageID);
            }
        });

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

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getMessage(String messageID){
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
                        patientId.setText(requestedMessage.getString("patientID"));
                        testName.setText(requestedMessage.getString("testName"));
                        componentName.setText(requestedMessage.getString("componentName"));
                        isTestValueBool = requestedMessage.getBoolean("isValueBool"); //TODO check if this should be getString() instead
                        if (isTestValueBool){
                            boolValue.setText((requestedMessage.getString("testResultValue")).equals("1") ? "חיובית" : "שלילית");
                        } else {
                            measuredAmountValue.setText(requestedMessage.getString("testResultValue"));
                            measurementUnit.setText(requestedMessage.getString("measurementUnit"));
                        }
                        comments.setText(requestedMessage.getString("comments"));
                        if (requestedMessage.getBoolean("isUrgent")){
                            isUrgent.setImageResource(R.drawable.redexclamation_trans); //TODO find how to change the image src
                            ((TextView)findViewById(R.id.textViewUrgent)).setText("דחוף");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                isUrgent.setTooltipText("דחוף");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("messageID", messageID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void markAsRead(String messageID, String userID){
        // show confirmation box
        // on approval show progressDialog
        // add approval time and user in DB table
    }

    public void reply(String messageID){

    }

    public void forward(String messageID){

    }
}