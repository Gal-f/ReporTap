package il.reportap;

import android.content.Intent;
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
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    EditText editTextEmployeeNumber, editTextPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmployeeNumber = findViewById(R.id.editTextEmployeeNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            myStringRequestDept();

            //check if the user's account has been approved
            if (SharedPrefManager.getInstance(this).getUser().isActive) {
                switch(SharedPrefManager.getInstance(this).getUser().getDepartment()){
                    case 1:
                        startActivity(new Intent(this, InboxLab.class));
                        break;

                    case 2:
                        startActivity(new Intent(this, InboxDoctor.class));
                        break;

                    case 6:
                        startActivity(new Intent(this, ApproveUsers.class));
                        break;
                }
            } else {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            return;

        }


        //if user presses on login
        //calling the login method
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


        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN, new Response.Listener<String>() {
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

                        //creating a new user object - names are identical to the columns in the db
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("employee_ID"),
                                userJson.getString("full_name"),
                                userJson.getString("email"),
                                userJson.getString("role"),
                                userJson.getString("phone_number"),
                                userJson.getInt("works_in_dept"));

                        if (message.equals("משתמש לא מאומת")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            finish();
                            Intent intent1 = new Intent(MainActivity.this, TwoFactorAuth.class);
                            intent1.putExtra("user", user);
                            startActivity(intent1);
                        } else if (!obj.getBoolean("isActive")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            finish();
                            Intent intent2 = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intent2);
                        } else {
                            //storing the user in shared preferences and log him in
                            user.setActive(true);
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            finish();
                            //TODO - navigate to lab inbox if this is a lab worker

                            myStringRequestDept();
                            //startActivity(new Intent(getApplicationContext(), InboxDoctor.class));

                            if(user.getJobTitle().equals("מנהל מערכת")){
                                startActivity(new Intent(getApplicationContext(), ApproveUsers.class));
                            }
                            else{
                                startActivity(new Intent(getApplicationContext(), InboxDoctor.class));
                            }

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Nullable
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("employee_ID", EmployeeNumber);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
