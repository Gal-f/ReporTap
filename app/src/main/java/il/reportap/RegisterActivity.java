package il.reportap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.il.reportap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextPassword, editTextEmployeeNumber,
            editTextFullName, editTextPhoneNumber, editTextEmail;
    private Spinner spinnerDepartment, spinnerJobTitle;
    private ProgressBar progressBar;

    private HashMap<String, Integer> deptMap;                     //Translates department name to it's corresponding ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmployeeNumber = findViewById(R.id.editTextEmployeeNumber);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        spinnerJobTitle = findViewById(R.id.spinnerJobTitle);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        populateDeptMap(); // This populates the departments list from the DB and then adds them as options to the form.

        String[] roles = new String[]{"בחר תפקיד", "מנהל.ת מחלקה", "עובד.ת מעבדה", "רופא.ה", "עובד.ת אדמיניסטרציה", "אח.ות"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, roles);
        spinnerJobTitle.setAdapter(adapter2);

        findViewById(R.id.buttonRegister).setOnClickListener(view -> {
            registerUser();
        });

        findViewById(R.id.textViewLogin).setOnClickListener(view -> {
            //if the user pressed on login button we will open the login screen
            finish();
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        });

        // Handling back-button action
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void registerUser() {
        final String password = editTextPassword.getText().toString().trim();
        final String employeeNumber = editTextEmployeeNumber.getText().toString().trim();
        final String fullName = editTextFullName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String jobTitle = spinnerJobTitle.getSelectedItem().toString().trim();
        final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        final String department = spinnerDepartment.getSelectedItem().toString().trim();

        //validations

        if (TextUtils.isEmpty(employeeNumber)) {
            editTextEmployeeNumber.setError("יש להזין מספר עובד");
            editTextEmployeeNumber.requestFocus();
            return;
        }

        if (!employeeNumber.matches("[0-9]+")) {
            editTextEmployeeNumber.setError("מספר עובד צריך להכיל ספרות בלבד");
            editTextEmployeeNumber.requestFocus();
            return;
        }

        if (employeeNumber.length() != 6) {
            editTextEmployeeNumber.setError("מספר עובד צריך להכיל 6 ספרות בדיוק");
            editTextEmployeeNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("יש להזין סיסמה");
            editTextPassword.requestFocus();
            return;
        }

        //at least 6 digits, one english letter and one number
        if(!password.matches("^(?=.*?[a-z])(?=.*?[0-9]).{6,}$")){
            editTextPassword.setError("סיסמה צריכה להכיל לפחות 6 תווים, אות באנגלית וספרה");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError("יש להזין שם מלא");
            editTextFullName.requestFocus();
            return;
        }

        //not allowed: numbers, english letters, signs, less than one space, less than 2 characters in a word
        if (!fullName.matches("^(([\\u0590-\\u05FF\\uFB1D-\\uFB4F]{2,})*)(\\s+[\\u0590-\\u05FF\\uFB1D-\\uFB4F]{2,})(?:\\s+[\\u0590-\\u05FF\\uFB1D-\\uFB4F]{2,}+){0,2}$")) {
            editTextFullName.setError("שם מלא צריך להכיל אותיות בעברית בלבד ורווח בין השם הפרטי לבין שם המשפחה");
            editTextFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("יש להזין אימייל");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("נא להזין אימייל תקני");
            editTextEmail.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(phoneNumber)) {
            editTextPhoneNumber.setError("יש להזין מספר טלפון");
            editTextPhoneNumber.requestFocus();
            return;
        }

        if (phoneNumber.length() != 10) {
            editTextPhoneNumber.setError("מספר טלפון צריך להכיל 10 ספרות בדיוק");
            editTextPhoneNumber.requestFocus();
            return;
        }

        String startPhoneNum = phoneNumber.substring(0, 3);
        if (!(startPhoneNum.equals("050") || startPhoneNum.equals("052") || startPhoneNum.equals("054")
                || startPhoneNum.equals("058"))) {
            editTextPhoneNumber.setError("מספר טלפון צריך להתחיל ב: 050/052/054/058 ");
            editTextPhoneNumber.requestFocus();
            return;
        }

        if (department.equals("בחר מחלקה")) {
            Toast.makeText(this,
                    "יש לבחור מחלקה", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (jobTitle.equals("בחר תפקיד")) {
            Toast.makeText(this,
                    "יש לבחור תפקיד", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        //for saving the id of the chosen department as a foreign key in the user record
        final Integer deptID = deptMap.get(department);


        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);
                    String message = obj.getString("message");
                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object - names are identical to the columns in the server side
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("employee_ID"),
                                userJson.getString("full_name"),
                                userJson.getString("email"),
                                userJson.getString("role"),
                                userJson.getString("phone_number"),
                                userJson.getInt("works_in_dept"),
                                userJson.getString("dept_type"),
                                department //not a value that returns from the server response
                                );
                        //in order to send the user a notification when the manager approves his account
                        FirebaseMessaging.getInstance().subscribeToTopic(user.getEmployeeNumber())
                                .addOnCompleteListener(task -> {
                                    //msg is for our needs only, we don't show it to the user.
                                    String msg = "Done";
                                    if (!task.isSuccessful()) {
                                        msg = "Failed";
                                    }
                                    Log.d("user's subscription", msg);
                                });
                        Intent intent = new Intent(RegisterActivity.this, TwoFactorAuth.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "שגיאה בביצוע הפעולה. עימך הסליחה.", Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("password", password);
                params.put("employee_ID", employeeNumber);
                params.put("full_name", fullName);
                params.put("email", email);
                params.put("role", jobTitle);
                params.put("phone_number", phoneNumber);
                params.put("works_in_dept", deptID.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void populateDeptMap(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_DEPTS_N_TESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                deptMap = new HashMap<String, Integer>();
                try{
                    JSONObject entireResponse = new JSONObject(response);
                    JSONArray deptsArray = entireResponse.getJSONArray("departments");
                    for (int i=0; i<deptsArray.length(); i++) {
                        JSONObject dept = deptsArray.getJSONObject(i);
                        deptMap.put(dept.getString("deptName"), dept.getInt("deptID"));
                    }
                    inflateAutocompleteOptions(); //Use the freshly populated hashmap as options to select from in the form fields.
                    // The inflate call is done here in order to make sure the response for PopulateHashmaps() returned before calling inflateAutocompleteOptions().
                }
                catch (JSONException e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "קרתה שגיאה, נא נסו מאוחר יותר או פנו למנהל המערכת", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void inflateAutocompleteOptions(){
        //Define autocomplete field options

        ArrayList<String> departments = new ArrayList<>();
        departments.add("בחר מחלקה");
        for (String dept : this.deptMap.keySet())
            if(!dept.equals("הנהלה")){ //for now this is a department that represents only the system administrator which is already in the db
                departments.add(dept);
            }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        spinnerDepartment.setAdapter(adapter);
    }
}


