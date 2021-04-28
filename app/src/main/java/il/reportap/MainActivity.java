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

import com.example.loginregister.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText editTextEmployeeNumber, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmployeeNumber = (EditText) findViewById(R.id.editTextEmployeeNumber);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);


        //TODO change to inbox doctor/inbox lab based on the job title.
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            if(SharedPrefManager.getInstance(this).getUser().isActive){
                finish();
                startActivity(new Intent(this, InboxDoctor.class));
                return;
            }
            else{
                finish();
                startActivity(new Intent(this, ProfileActivity.class));
                return;
            }

        }


        //if user presses on login
        //calling the method login
        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    userLogin();
            }
        });

        //if user presses on not registered
        findViewById(R.id.textViewRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                finish();
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });


    }

    private void userLogin() {
        //first getting the values
        final String EmployeeNumber = editTextEmployeeNumber.getText().toString();
        final String password = editTextPassword.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(EmployeeNumber)) {
            editTextEmployeeNumber.setError("נא הזן מספר עובד");
            editTextEmployeeNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("נא הזן סיסמה");
            editTextPassword.requestFocus();
            return;
        }

        //if everything is fine

        class UserLogin extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object - names are identical to the columns in the db
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("employee_ID"),
                                userJson.getString("full_name"),
                                userJson.getString("email"),
                                userJson.getString("role"),
                                userJson.getString("phone_number"),
                                userJson.getInt("works_in_dept"));

                        if(obj.getString("message").equals("משתמש לא מאומת")){
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            finish();
                            Intent intent1 = new Intent(MainActivity.this, TwoFactorAuth.class);
                            intent1.putExtra("user", user);
                            startActivity(intent1);
                        }
                        else if(obj.getString("message").equals("משתמש ממתין לאישור מנהל")){
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            finish();
                            Intent intent2 = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intent2);
                        }
                        else{
                            //storing the user in shared preferences and log him in
                            user.setActive(true);
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            finish();
                            //TODO - navigate to lab inbox if this is a lab worker
                            startActivity(new Intent(getApplicationContext(), InboxDoctor.class));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, Object> params = new HashMap<>();
                params.put("employee_ID", EmployeeNumber);
                params.put("password", password);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }


        UserLogin ul = new UserLogin();
        ul.execute();
    }
}
