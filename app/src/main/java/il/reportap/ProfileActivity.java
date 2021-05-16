package il.reportap;


import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginregister.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends OptionsMenu {

    private TextView textViewEmployeeNumber,
            textViewFullName, textViewEmail, textViewJobTitle, textViewPhoneNumber, textViewDepartment, notActiveUser;
    private boolean isActive = false;
    private LinearLayout notActive;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = SharedPrefManager.getInstance(this).getUser();
        textViewEmployeeNumber =  (TextView) findViewById(R.id.textViewEmployeeNumber);
        textViewFullName = (TextView) findViewById(R.id.textViewFullName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewJobTitle = (TextView) findViewById(R.id.textViewJobTitle);
        textViewPhoneNumber =  (TextView) findViewById(R.id.textViewPhoneNumber);
        textViewDepartment=  (TextView) findViewById(R.id.textViewDepartment);
        //isActive = user.isActive;
        notActive = findViewById(R.id.notActive);
        checkActive();
        //setting the values to the textviews
        textViewEmployeeNumber.setText("מספר עובד: " + user.getEmployeeNumber());
        textViewFullName.setText(user.getFullName());
        textViewEmail.setText("אימייל: " + user.getEmail());
        textViewJobTitle.setText(user.getJobTitle());
        textViewPhoneNumber.setText("מספר טלפון: " + user.getPhoneNumber());
        textViewDepartment.setText("מחלקה: " + user.getDeptName());


    }

    private void checkActive() {
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
                        if(!isActive){
                            notActive.setVisibility(View.VISIBLE);
                            notActiveUser = findViewById(R.id.textViewNotActive);
                            notActiveUser.setText(Html.fromHtml(getString(R.string.notApprovedYet)));
                        }
                        else{
                            user.setActive(true);
                            notActive.setVisibility(View.GONE);
                        }
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