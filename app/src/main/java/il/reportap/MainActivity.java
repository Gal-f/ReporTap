package il.reportap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername, editTextEmail, editTextPassword, editTextEmployeeNumber,
            editTextFirstName, editTextLastName, editTextJobTitle, editTextPhoneNumber;
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
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmployeeNumber = (EditText) findViewById(R.id.editTextEmployeeNumber);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextJobTitle = (EditText) findViewById(R.id.editTextJobTitle);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);

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
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String employeeNumber = editTextEmployeeNumber.getText().toString().trim();
        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String jobTitle = editTextJobTitle.getText().toString().trim();
        final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        //final String gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();

        //first we will do the validations

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter username");
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter a password");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(employeeNumber)) {
            editTextEmployeeNumber.setError("Enter your employee number");
            editTextEmployeeNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("Enter your employee number");
            editTextFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Enter your employee number");
            editTextLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(jobTitle)) {
            editTextJobTitle.setError("Enter your employee number");
            editTextJobTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            editTextPhoneNumber.setError("Enter your employee number");
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
                params.put("email", email);
                params.put("password", password);
                params.put("employeeNumber", employeeNumber);
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("jobTitle", jobTitle);
                params.put("phoneNumber", phoneNumber);


                //returing the response
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
                                userJson.getString("email"),
                                userJson.getString("employeeNumber"),
                                userJson.getString("firstName"),
                                userJson.getString("lastName"),
                                userJson.getString("jobTitle"),
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
