package il.reportap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.il.reportap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;

public class  TwoFactorAuth extends NavigateUser {

    private String otp;
    private User user;
    EditText editTextOTP;
    TextView helloUser;
    LinearLayout dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_auth);

        editTextOTP = findViewById(R.id.editTextOTP);
        //generates random 6 digits code
        otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        //get the user data from Register Activity/Login activity
        //get the user's data from Register Activity/Login activity
        user = (User) getIntent().getSerializableExtra("user");
        dialog = (LinearLayout) findViewById(R.id.dialogPopUp);
        helloUser = findViewById(R.id.helloUser);
        helloUser.setText("שלום " + user.getFullName() +",");
    }

    @Override
    public void onBackPressed()
    {
        final int visibility = dialog.getVisibility();
        if(visibility == 0){ //this is visible
            dialog.setVisibility(View.GONE);
        }
        else{
            SharedPrefManager.getInstance(getApplicationContext()).logout();
        }
    }

    public void sendOtp(View v) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SENDOTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String errorMessage;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    errorMessage = jsonObject.getString("message");
                    if (jsonObject.getBoolean("error")){ // If there was any error along the way
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    } else {
                        dialog.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            },
                new Response.ErrorListener() {
                @Override
                //handling volley error
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "שגיאה בשליחת קוד האימות. נא נסו את אמצעי האימות השני.", Toast.LENGTH_LONG).show();
                }
            }) {

            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", user.getEmail());
                params.put("phone_number", user.getPhoneNumber());
                params.put("otp", otp);
                switch (v.getId()) {
                    case (R.id.buttonSendToPhone):
                        //creating request parameters
                        params.put("sendTo", "phone");
                        break;
                    case (R.id.buttonSendToMail):
                        params.put("sendTo", "email");
                        break;
                }
                return params;
            }
        };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
    }

    public void validateOTP(View view) {
        String userOTP = editTextOTP.getText().toString();
        if(!userOTP.equals(otp)){
            Toast.makeText(this,
                    "קוד שגוי, נסה שוב", Toast.LENGTH_LONG)
                    .show();
        }
        else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_VREIFIEDUSER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String errorMessage;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        errorMessage = jsonObject.getString("message");
                        if (jsonObject.getBoolean("error")) { // If there was any error along the way
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            Toast.makeText(getApplicationContext(), "הקוד אומת בהצלחה. כעת יש להמתין לאישור מנהל", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(TwoFactorAuth.this, ProfileActivity.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        //handling with volley error
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "הקוד אומת בהצלחה אך אנו חווים תקלה טכנית. נא נסה שנית מאוחר יותר.", Toast.LENGTH_LONG).show();
                        }
                    }) {

                @Override
                protected HashMap<String, String> getParams() throws AuthFailureError {
                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("employee_ID", user.getEmployeeNumber());
                    return params;
                }
            };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
        }
    }


