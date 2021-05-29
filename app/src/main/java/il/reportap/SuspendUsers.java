package il.reportap;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class SuspendUsers extends OptionsMenu {

    private EditText empNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspend_users);
        TextView explain = findViewById(R.id.explain);
        empNum = findViewById(R.id.userID);
        explain.setText(Html.fromHtml(getString(R.string.managerDelete)));
    }

    public void suspendUser(View view) {
        String userID = empNum.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_DELETEUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String message;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    message = jsonObject.getString("message");
                    //message can be an error message or a success message
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    if(!jsonObject.getBoolean("error")){
                        empNum.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "שגיאה בביצוע הפעולה. עימך הסליחה.", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("employee_ID",userID);
                return params;
            }
        };
        //Queueing the request since the operation will take some time
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
