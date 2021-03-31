package il.reportap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewMessage extends AppCompatActivity {

    private AutoCompleteTextView recipient, testName, componentName;
    private EditText patientId, patientName, measuredAmount, comments;
    private CheckBox isUrgent;
    private ProgressDialog progressDialog;
    private boolean success;

    private HashMap<Integer, String> deptMap;                     //Translates department ID to department name
    private HashMap<Integer, Pair<String, String>> testTypeMap;   //Translates test type ID to it's corresponding name + result type (in this form: [ID, [name, resultType]] ).

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        this.recipient = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        this.patientId = (EditText) findViewById(R.id.editTextPatientIdNumber);
        this.patientName = (EditText) findViewById(R.id.editTextPatientName);
        this.testName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTestName);
        this.componentName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewComponentName);
        this.measuredAmount = (EditText) findViewById(R.id.editTextMeasuredAmount);
        this.isUrgent = (CheckBox) findViewById(R.id.checkBoxUrgent);
        this.comments = (EditText) findViewById(R.id.editTextTextMultiLineComments);
        this.progressDialog = new ProgressDialog(this);

        //TODO Create auto-complete for patient ID? (optional)

        populateHashmaps(); // This populates the departments and test types from the DB and then adds them as options to the form.

        findViewById(R.id.buttonSendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send();
                //TODO show a message if Send() succeeded or not
            }
        });

        findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipient.setText("");
                patientId.setText("");
                patientName.setText("");
                testName.setText("");
                componentName.setText("");
                measuredAmount.setText("");
                isUrgent.setChecked(false);
                comments.setText("");
                //startActivity(new Intent(NewMessage.this, NewMessage.class)); //Another option, simply refreshing the page
            }
        });
    }

    public Boolean Send(){
        final String recipient = this.recipient.getText().toString().trim();
        final String patientId = this.patientId.getText().toString().trim();
        final String patientName = this.patientName.getText().toString().trim();
        final String testName = this.testName.getText().toString().trim();
        final String componentName = this.componentName.getText().toString().trim();
        final String measuredAmount = this.measuredAmount.getText().toString().trim();
        //final int isUrgent = (this.isUrgent.isChecked()?1:0);
        final String isUrgent = Boolean.toString(this.isUrgent.isChecked()); //TODO Will sending bool as string be saved in the DB as 0/1 ?
        final String comments = this.comments.getText().toString().trim();

        /* //TODO check if it's OK to use those as strings when sending them to the query using the hashmap
        final int recipient = Integer.parseInt(this.recipient.toString().trim());
        final String patientId = this.patientId.toString().trim();
        final String patientName = this.patientName.toString().trim();
        final String testName = this.testName.toString().trim();
        final String componentName = this.componentName.toString().trim();
        final float measuredAmount = Float.parseFloat(this.measuredAmount.toString().trim());
        final Boolean isUrgent = Boolean.parseBoolean(this.isUrgent.toString().trim());
        final String comments = this.comments.toString().trim();
        */

        success = false;
        progressDialog.setMessage("ההודעה שלך נשלחת.\nנא להמתין לאישור...");
        progressDialog.show();
        //Creating a Volley request to communicate with PHP pages
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_NEWMESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("error")){ // If there was any error along the way
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        success = true;
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), jsonObject.getJSONArray("sent_message").toString(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("sender", SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmployeeNumber());
                params.put("department", recipient);
                params.put("patientId",patientId);
                params.put("patientName",patientName);
                params.put("testType",testName);
                params.put("componentName",componentName);
                params.put("boolValue", "false");   //TODO implement option to make a boolean test result value
                params.put("measuredAmount",measuredAmount);
                params.put("isUrgent",isUrgent);
                params.put("comments",comments);
                return params;
            }
        };
        //Queueing the request since the operation will take some time
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return success;
    }

    public void populateHashmaps(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_DEPTS_N_TESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                deptMap = new HashMap<Integer, String>();
                testTypeMap = new HashMap<Integer, Pair<String, String>>();

                try{
                    JSONObject entireResponse = new JSONObject(response);
                    JSONArray deptsArray = entireResponse.getJSONArray("departments");
                    JSONArray testTypesArray = entireResponse.getJSONArray("testTypes");
                    for (int i=0; i<deptsArray.length(); i++) {
                        JSONObject dept = deptsArray.getJSONObject(i);
                        deptMap.put(dept.getInt("deptID"), dept.getString("deptName"));
                    }
                    for (int i=0; i<testTypesArray.length(); i++){
                        JSONObject testType = testTypesArray.getJSONObject(i);
                        testTypeMap.put(testType.getInt("testID"), new Pair<>(testType.getString("testName"), testType.getString("resultType")));
                    }
                    inflateAutocompleteOptions(); //Use the freshly populated hashmaps as options to select from in the form fields.
                    // The inflate call is done here in order to make sure the response for PopulateHashmaps() returned before calling inflateAutocompleteOptions().
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

    public void inflateAutocompleteOptions(){
        //Define autocomplete fields options

        //String[] departments = (String[]) this.deptMap.values().toArray(); //casting doesn't work
        ArrayList<String> departments = new ArrayList<>();
        for (String dept : this.deptMap.values())
            departments.add(dept);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, departments);
        recipient.setAdapter(adapter);
        recipient.setThreshold(1); //Start autocompletion from the 1st character

        ArrayList<String> tests = new ArrayList<>();
        for (int i=0; i<this.testTypeMap.size(); i++){
            tests.add((String)((Pair)this.testTypeMap.values().toArray()[i]).first);
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, tests);
        testName.setAdapter(adapter2);
        testName.setThreshold(1);

        String[] components = new String[]{"חיידק","בקטריה", "שניבוריג"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, components);
        componentName.setAdapter(adapter3);
        componentName.setThreshold(1);
    }
}