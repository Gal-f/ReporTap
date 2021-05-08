package il.reportap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import java.util.Map;

public class NewMessage extends OptionsMenu {

    private AutoCompleteTextView recipient, testName, componentName, patientId;
    private EditText /*patientId,*/ patientName, measuredAmount, comments;
    private CheckBox isUrgent;
    private RadioGroup boolResultSelection;
    private ProgressDialog progressDialog;
    private boolean success, isTestValueBool;

    private HashMap<String, Integer> deptMap;                     //Translates department name to it's corresponding ID
    private HashMap<String, Pair<Integer, String>> testTypeMap;   //Translates test type ID to it's corresponding name + result type (in this form: [name, [ID, resultType]] )
    private HashMap<String, String> patientsMap;
    //TODO add a 'measurement unit' to testTypeMap and show it next to the measuredAmount EditText field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        this.recipient = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        this.patientId = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewPatientIdNumber);
        //this.patientId = (EditText) findViewById(R.id.editTextPatientIdNumber);
        this.patientName = (EditText) findViewById(R.id.editTextPatientName);
        this.testName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTestName);
        this.componentName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewComponentName);
        this.measuredAmount = (EditText) findViewById(R.id.editTextMeasuredAmount);
        this.boolResultSelection = (RadioGroup) findViewById(R.id.radioGroupBoolResult);
        this.isUrgent = (CheckBox) findViewById(R.id.checkBoxUrgent);
        this.comments = (EditText) findViewById(R.id.editTextTextMultiLineComments);
        this.progressDialog = new ProgressDialog(this);

        populateHashmaps(); // This populates the departments and test types from the DB and then adds them as options to the form.

        patientId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  // When selecting patient ID, show the patient's name accordingly
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedID = parent.getItemAtPosition(position).toString();
                patientName.setText(patientsMap.get(selectedID));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                patientId.setError("נא לבחור מספר זהות של מטופל קיים");
                patientId.setText(""); // TODO Is this necessary?
            }
        }
        );

        findViewById(R.id.buttonSendMessage).setOnClickListener(new View.OnClickListener() {    // User clicked send
            @Override
            public void onClick(View v) {
                // Input validations
                if (TextUtils.isEmpty(recipient.getText().toString())) {
                    recipient.setError("יש לבחור מחלקה נמענת");
                    return;
                }
                if (!deptMap.containsKey(recipient.getText().toString())){
                    recipient.setError("נא לבחור אחת מהמחלקות הקיימות");
                    return;
                }
                if (patientId.length() != 9){
                    patientId.setError("מספר זהות צריך לכלול 9 ספרות בדיוק");
                    return;
                }
                if (!patientId.getText().toString().matches("[0-9]+")){
                    patientId.setError("מספר זהות צריך לכלול ספרות בלבד");
                    return;
                }
                if (!patientsMap.containsKey(patientId.getText().toString())){
                    patientId.setError("המטופל לא קיים במערכת");
                    return;
                }
                if (TextUtils.isEmpty(testName.getText().toString())){
                    testName.setError("נא לבחור סוג בדיקה");
                    return;
                }
                if (!testTypeMap.containsKey(testName.getText().toString())){
                    testName.setError("נא לבחור בדיקה קיימת");
                    return;
                }
                if (isTestValueBool)
                    if (boolResultSelection.getCheckedRadioButtonId() == -1){
                        ((RadioButton)findViewById(R.id.radioButtonBoolResult_Positive)).setError("נא לבחור תוצאה לבדיקה");
                        return;

                    }
                //Component, Measured amount and Comments are not validated, to allow the lab crew more flexibility.

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

        //Display the correct interface for boolean and numeric test types
        testName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTest = parent.getItemAtPosition(position).toString();
                // If the test type is of a boolean result, set the result views to Radio selection instead of the Textviews.
                if (((Pair)testTypeMap.get(selectedTest)).second.equals("boolean")){    // Check the test type in the map according to the test names
                    isTestValueBool = true;
                    findViewById(R.id.linearLayoutBoolResult).setVisibility(View.VISIBLE);
                    findViewById(R.id.linearLayoutMeasuredAmount).setVisibility(View.GONE);
                }
                else
                {
                    isTestValueBool = false;
                    findViewById(R.id.linearLayoutBoolResult).setVisibility(View.GONE);
                    findViewById(R.id.linearLayoutMeasuredAmount).setVisibility(View.VISIBLE);
                }
            }
        });

        // THE FOLLOWING BUTTON IS FOR TESTING PURPOSES ONLY
        findViewById(R.id.buttonAutofill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillTestValues();
            }
        });

        findViewById(R.id.imageButtonBarcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "סריקת ברקוד לא זמינה עדיין בגירסה הזו...", Toast.LENGTH_LONG).show();

            }
        });
    }

    public Boolean Send(){
        // All values are sent as Strings and interpreted by the server side logic to the correct types (due to limitation of the parameters-sending process using a Map<String, String>)
        final String recipient = this.deptMap.get(this.recipient.getText().toString().trim()).toString();   // Convert the department name in the form to department ID
        final String patientId = this.patientId.getText().toString().trim();
        final String patientName = this.patientName.getText().toString().trim();
        final String testName = ((Pair)(this.testTypeMap.get(this.testName.getText().toString().trim()))).first.toString(); // Convert the test-type name in the form to the test-type ID
        final String componentName = this.componentName.getText().toString().trim();
        final String isUrgent = (this.isUrgent.isChecked()?"1":"0");
        final String comments = this.comments.getText().toString().trim();

        // Set both numeric and boolean test results (as we can't define constants inside a condition), and use only one of them down the road
        final String measuredAmount = this.measuredAmount.getText().toString().trim();
        int selectedRadio = boolResultSelection.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedRadio);

        final String booleanResult = (rb != null ? (rb.getText().equals("חיובית")?"1":"0") : ""); // TODO Check if rb is actually null when none is selected, otherwise change it to check if selectedRadio == -1

        success = false;
        progressDialog.setMessage("ההודעה שלך נשלחת.\nנא להמתין לאישור...");
        progressDialog.show();
        //Creating a Volley request to communicate with PHP pages
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_NEWMESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                String errorMessage = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    errorMessage = jsonObject.getString("message");
                    if(!jsonObject.getBoolean("error")) // If there was any error along the way
                        success = true;
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    finish();
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(NewMessage.this, InboxDoctor.class));  // Redirect after the message is sent
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
                params.put("boolValue", "false");
                params.put("isValueBool", (isTestValueBool ? "1" : "0"));
                if (isTestValueBool) {     // Use either a numeric or boolean result, depending on the test type
                    params.put("testResultValue", booleanResult);
                } else {
                    params.put("testResultValue", measuredAmount);
                }
                params.put("isUrgent",isUrgent);
                params.put("comments",comments);
                return params;
            }
        };
        //Queueing the request since the operation will take some time
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return success; //Currently unused (error messages being handled within the function), returns bool for future use
    }

    public void populateHashmaps(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_DEPTS_N_TESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                deptMap = new HashMap<String, Integer>();
                testTypeMap = new HashMap<String, Pair<Integer, String>>();
                patientsMap = new HashMap<String, String>();

                try{
                    JSONObject entireResponse = new JSONObject(response);
                    JSONArray deptsArray = entireResponse.getJSONArray("departments");
                    // Getting all departments
                    for (int i=0; i<deptsArray.length(); i++) {
                        JSONObject dept = deptsArray.getJSONObject(i);
                        deptMap.put(dept.getString("deptName"), dept.getInt("deptID"));
                    }
                    // Getting all test types
                    JSONArray testTypesArray = entireResponse.getJSONArray("testTypes");
                    for (int i=0; i<testTypesArray.length(); i++){
                        JSONObject testType = testTypesArray.getJSONObject(i);
                        testTypeMap.put(testType.getString("testName"), new Pair<>(testType.getInt("testID"), testType.getString("resultType")));
                    }
                    // Getting all patients
                    JSONArray patientsArray = entireResponse.getJSONArray("patients");
                    for (int i=0; i<patientsArray.length(); i++){
                        JSONObject patient = patientsArray.getJSONObject(i);
                        patientsMap.put(patient.getString("ID"), patient.getString("name"));
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

        ArrayList<String> departments = new ArrayList<>();
        for (String dept : this.deptMap.keySet())
            departments.add(dept);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, departments);
        recipient.setAdapter(adapter);
        recipient.setThreshold(1); //Start autocompletion from the 1st character

        ArrayList<String> tests = new ArrayList<>();
        for (int i=0; i<this.testTypeMap.size(); i++){
            tests.add((String) (this.testTypeMap.keySet().toArray()[i]));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, tests);
        testName.setAdapter(adapter2);
        testName.setThreshold(1);

        String[] components = new String[]{"חיידק","בקטריה", "שניבוריג"};   //TODO Make this list dynamic with DB table?
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, components);
        componentName.setAdapter(adapter3);
        componentName.setThreshold(1);

        //TODO inflate autocompletion list for patientID
    }

    public void fillTestValues(){
        recipient.setText("פנימית א");
        patientId.setText("123456789");
        patientName.setText("ישראל ישראלי");
        testName.setText("תרבית דם");
        componentName.setText("חיידק");
        measuredAmount.setText("10");
        isUrgent.setChecked(true);
        comments.setText("בדיקה אוטומטית");
    }
}