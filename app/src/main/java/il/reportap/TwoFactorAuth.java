package il.reportap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginregister.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;

public class TwoFactorAuth extends AppCompatActivity {

    private String otp;
    private User user;
    EditText editTextOTP;
    LinearLayout dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_auth);

        editTextOTP = findViewById(R.id.editTextOTP);
        Intent intent = getIntent();
        //generates random 6 digits code
        otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        //get the user data from Register Activity/Login activity
        user = (User) getIntent().getSerializableExtra("user");
        dialog = (LinearLayout) findViewById(R.id.dialogPopUp);

    }

    public void sendOtp(View v) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLs.URL_SENDOTP,
                //lambda expression
                response -> {
                    String errorMessage = "";
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        errorMessage = jsonObject.getString("message");
                        if (jsonObject.getBoolean("error")) { // If there was any error along the way
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally{
                        dialog.setVisibility(View.VISIBLE);
                    }
                },
                //lambda expression
                error -> Toast.makeText(TwoFactorAuth.this, error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Nullable
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
        };  RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
    }


        public void validateOTP(View view) {
            String userOTP = editTextOTP.getText().toString();
            if(!userOTP.equals(otp)){
                Toast.makeText(this,
                        "קוד שגוי, נסה שוב", Toast.LENGTH_LONG)
                        .show();
            }
            else{
                Executors.newSingleThreadExecutor().submit(() -> {
                    RequestHandler secondRequestHandler = new RequestHandler();
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("employee_ID", user.getEmployeeNumber());
                    try{
                        //converting response to json object
                        JSONObject obj = new JSONObject(secondRequestHandler.sendPostRequest(URLs.URL_VREIFIEDUSER, params));
                        String responseMessage = obj.getString("message");
                        //if the user is still waiting for the manager confirmation
                         if (responseMessage.equals("הקוד אומת בהצלחה, כעת יש להמתין לאישור מנהל")) {
                             runOnUiThread(new Runnable()
                             {
                                 public void run()
                                 {
                                     Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
                                     finish();
                                     startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                 }
                             });
                         } else {
                            //storing the user in shared preferences
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            finish();
                            //TODO - navigate to lab inbox if this is a lab worker
                            startActivity(new Intent(getApplicationContext(), InboxDoctor.class));
                        }
                    }catch (Exception e) {
                    e.printStackTrace();
                    }
                });
            }
        }
}