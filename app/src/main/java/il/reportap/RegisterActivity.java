package il.reportap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    EditText editTextPassword, editTextEmployeeNumber,
            editTextFullName, editTextPhoneNumber;
    Spinner spinnerDepartment, spinnerJobTitle;
    HashMap<String, Integer> deptData = new HashMap<String, Integer>(){{
        put("מעבדה מיקרוביולוגית",1);
        put("פנימית א",2);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmployeeNumber = (EditText) findViewById(R.id.editTextEmployeeNumber);
        editTextFullName = (EditText) findViewById(R.id.editTextFullName);
        spinnerJobTitle = findViewById(R.id.spinnerJobTitle);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);

        //define spinners options
        String[] departments = new String[]{"בחר מחלקה","מעבדה מיקרוביולוגית", "פנימית א"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, departments);
        spinnerDepartment.setAdapter(adapter);

        String[] roles = new String[]{"בחר תפקיד","מנהל.ת מחלקה", "עובד.ת מעבדה","רופא.ה","עובד.ת אדמיניסטרציה","אח.ות"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, roles);
        spinnerJobTitle.setAdapter(adapter2);

        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button register
                //here we will register the user to server
                registerUser();
            }
        });

        findViewById(R.id.textViewLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on login
                //we will open the login screen
                finish();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });


    }
    public void sendMessage(View view) {
        // Do something in response to button
    }

    private void registerUser() {
        final String password = editTextPassword.getText().toString().trim();
        final String employeeNumber = editTextEmployeeNumber.getText().toString().trim();
        final String fullName = editTextFullName.getText().toString().trim();
        final String jobTitle = spinnerJobTitle.getSelectedItem().toString().trim();
        final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        final String department = spinnerDepartment.getSelectedItem().toString().trim();

        //validations
        //TODO validation function with switch case

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

        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError("יש להזין שם מלא");
            editTextFullName.requestFocus();
            return;
        }

        //not allowed: numbers, english letters, signs, less than one space, less than 2 characters in a word
        if(!fullName.matches("^(([\\u0590-\\u05FF\\uFB1D-\\uFB4F]{2,})*)\\s+([\\u0590-\\u05FF\\uFB1D-\\uFB4F]*)$")){
            editTextFullName.setError("שם מלא צריך להכיל אותיות בעברית בלבד ורווח בין השם הפרטי לבין שם המשפחה");
            editTextFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            editTextPhoneNumber.setError("יש להזין מספר טלפון");
            editTextPhoneNumber.requestFocus();
            return;
        }

        if(phoneNumber.length()!=10){
            editTextPhoneNumber.setError("מספר טלפון צריך להכיל 10 ספרות בדיוק");
            editTextPhoneNumber.requestFocus();
            return;
        }

        String startPhoneNum = phoneNumber.substring(0, 3);
        if(!(startPhoneNum.equals("050")||startPhoneNum.equals("052")||startPhoneNum.equals("054")
                ||startPhoneNum.equals("058"))){
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
        final Integer deptID = deptData.get(department);


        //if it passes all the validations

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, Object> params = new HashMap<>();
                params.put("password", password);
                params.put("employee_ID", employeeNumber);
                params.put("full_name", fullName);
                params.put("role", jobTitle);
                params.put("phone_number", phoneNumber);
                params.put("works_in_dept", deptID);


                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object - names are identical to the columns in the db
                        User user = new User(
                                userJson.getInt("id"),
                                // userJson.getString("username"),
                                userJson.getString("employee_ID"),
                                userJson.getString("full_name"),
                                userJson.getString("role"),
                                userJson.getString("phone_number"),
                                userJson.getInt("works_in_dept")

                        );
                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }


}