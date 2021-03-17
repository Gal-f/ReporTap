package il.reportap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword, editTextEmployeeNumber,
            editTextFullName, editTextPhoneNumber;
    Spinner spinnerDepartment, spinnerJobTitle;
   // RadioGroup radioGroupGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmployeeNumber = (EditText) findViewById(R.id.editTextEmployeeNumber);
        editTextFullName = (EditText) findViewById(R.id.editTextFullName);
        spinnerJobTitle = findViewById(R.id.spinnerJobTitle);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);

        //define spinners options
        String[] departments = new String[]{"בחר מחלקה","מעבדה מיקרוביולוגית", "פנימית א"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        spinnerDepartment.setAdapter(adapter);

        String[] roles = new String[]{"בחר תפקיד","מנהל.ת מחלקה", "עובד.ת מעבדה","רופא.ה","עובד.ת אדמיניסטרציה","אח.ות"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
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
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

    }

    private void registerUser() {
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String employeeNumber = editTextEmployeeNumber.getText().toString().trim();
        final String fullName = editTextFullName.getText().toString().trim();
        final String jobTitle = spinnerJobTitle.getSelectedItem().toString().trim();
        final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        final String department = spinnerDepartment.getSelectedItem().toString().trim();

        //validations

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("יש להזין שם משתמש");
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("יש להזין סיסמה");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(employeeNumber)) {
            editTextEmployeeNumber.setError("יש להזין מספר עובד");
            editTextEmployeeNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError("יש להזין שם מלא");
            editTextFullName.requestFocus();
            return;
        }


        if (jobTitle.equals("בחר תפקיד")) {
            Toast.makeText(this,
                    "יש לבחור תפקיד", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (department.equals("בחר מחלקה")) {
            Toast.makeText(this,
                    "יש לבחור מחלקה", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            editTextPhoneNumber.setError("יש להזין מספר טלפון");
            editTextPhoneNumber.requestFocus();
            return;
        }
        //if it passes all the validations

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("employeeNumber", employeeNumber);
                params.put("fullName", fullName);
                params.put("jobTitle", jobTitle);
                params.put("phoneNumber", phoneNumber);


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

                        //creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("employeeNumber"),
                                userJson.getString("fullName"),
                                userJson.getString("role"),
                                userJson.getString("phoneNumber")

                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
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
