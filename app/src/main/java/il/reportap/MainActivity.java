package il.reportap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginregister.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
            finish();
            myStringRequestDept();
//            startActivity(new Intent(getApplicationContext(), InboxDoctor.class));
            return;
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
                                userJson.getInt("works_in_dept")

                        );

                        if(obj.getString("message").equals("משתמש לא מאומת")){
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            finish();
                            Intent intent = new Intent(MainActivity.this, TwoFactorAuth.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                        else{
                            //storing the user in shared preferences and log him in
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            finish();
                            //TODO - navigate to lab inbox if this is a lab worker
                            myStringRequestDept();
                            //startActivity(new Intent(getApplicationContext(), InboxDoctor.class));
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

    public void myStringRequestDept () {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLs.URL_DEPTTYPE,
                //lambda expression
                response -> {
                    try {
                        JSONObject deptObj = new JSONObject(response);
                        JSONArray jDeptArr = deptObj.getJSONArray("departmentType");
                        JSONObject jDeptObj = jDeptArr.getJSONObject(0);
                        String deptType = jDeptObj.getString("dept_type");
                       // final LayoutInflater factory = getLayoutInflater();
                        if (deptType.equals("lab"))
                        {
                            finish();
                            startActivity(new Intent(getApplicationContext(), InboxLab.class));
                        }
                        else if (deptType.equals("medical_dept"))
                        {
                            finish();
                            startActivity(new Intent(getApplicationContext(), InboxDoctor.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                //lambda expression
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUser().getId()));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
