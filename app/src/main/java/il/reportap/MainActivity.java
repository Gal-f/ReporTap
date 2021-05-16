package il.reportap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends NavigateUser {

    private EditText editTextEmployeeNumber, editTextPassword;
    private ProgressBar progressBar;
    private HashMap<Integer, String> deptMap;
    private boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmployeeNumber = findViewById(R.id.editTextEmployeeNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);
        deptMap = new HashMap<Integer, String>();
        populateHashmap();


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

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            //check if the user's account has been approved by the system manager
            User user = SharedPrefManager.getInstance(this).getUser();
            checkActive(user);
            if(isActive){
                user.setActive(true);
                SharedPrefManager.getInstance(getApplicationContext()).updateIsActive(true);
                try{
                    goToClass(SharedPrefManager.getInstance(getApplicationContext()).getUser().getDeptType());
                }catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            } else {
                //navigate the user to his profile
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        }
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

                        //creating a new user object - names are identical to the columns in the php code
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("employee_ID"),
                                userJson.getString("full_name"),
                                userJson.getString("email"),
                                userJson.getString("role"),
                                userJson.getString("phone_number"),
                                userJson.getInt("works_in_dept"),
                                userJson.getString("dept_type"),
                                deptMap.get(userJson.getInt("works_in_dept")));

                        //if the user has not completed the 2fa process
                        if (message.equals("משתמש לא מאומת")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            finish();
                            Intent intent1 = new Intent(MainActivity.this, TwoFactorAuth.class);
                            intent1.putExtra("user", user);
                            startActivity(intent1);

                        //check if the system manager has approved the user's account
                        } else if (!obj.getBoolean("isActive")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                         //the user has finished the 2fa process and his account got the manager's approval
                        } else {
                            //subscribe the user to his department's channel in order to get relevant notifications
                            FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(user.getDeptID()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //msg is for our needs only, we don't show it to the user.
                                            String msg ="Done";
                                            if (!task.isSuccessful()) {
                                                msg = "Failed";
                                            }
                                            Log.d("user's subscription", msg);
                                        }
                                    });
                            //storing the user in shared preferences and log him in
                            user.setActive(true);
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            finish();
                            goToClass(user.getDeptType());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
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

    private void populateHashmap(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_DEPTS_N_TESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject entireResponse = new JSONObject(response);
                    JSONArray deptsArray = entireResponse.getJSONArray("departments");
                    for (int i=0; i<deptsArray.length(); i++) {
                        JSONObject dept = deptsArray.getJSONObject(i);
                        deptMap.put(dept.getInt("deptID"), dept.getString("deptName"));
                    }
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void checkActive(User user) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ISACTIVE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String message = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("error")) {
                        message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                    else{
                        isActive = jsonObject.getBoolean("isActive");
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("employee_ID", user.getEmployeeNumber());
                return params;
            }
        };
        //Queueing the request since the operation will take some time
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
