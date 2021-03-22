package il.reportap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
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
import com.example.loginregister.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewMessage extends AppCompatActivity {

    private AutoCompleteTextView recipient, testName, componentName;
    private EditText patientId, patientName, measuredAmount, comments;
    private CheckBox isUrgent;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        this.recipient = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        this.patientId = (EditText) findViewById(R.id.editTextPatientIdNumber);
        this.patientName = (EditText) findViewById(R.id.editTextPatientName);
        this.testName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTestName);
        this.componentName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewComponentName);
        this.measuredAmount = (EditText) findViewById(R.id.editTextMeasuredAmount);
        this.isUrgent = (CheckBox) findViewById(R.id.checkBoxUrgent);
        this.comments = (EditText) findViewById(R.id.editTextTextMultiLineComments);
        this.progressDialog = new ProgressDialog(this);

        //TODO Create auto-complete for patient ID? (optional)

        //Define autocomplete fields options //TODO Populate recipient, testName, componentName with choices from the DB tables.
        String[] departments = new String[]{"בחר מחלקה","מעבדה מיקרוביולוגית", "פנימית א"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, departments);
        recipient.setAdapter(adapter);

        String[] tests = new String[]{"תרבית דם","תרבית שתן", "PCR קורונה"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, tests);
        testName.setAdapter(adapter2);

        String[] components = new String[]{"חיידק","בקטריה", "שניבוריג"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, components);
        testName.setAdapter(adapter3);

        findViewById(R.id.buttonSendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send();
            }
        });

        findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                recipient.setText("");
                patientId.setText("");
                patientName.setText("");
                testName.setText("");
                componentName.setText("");
                measuredAmount.setText("");
                isUrgent.setChecked(false);
                comments.setText("");
                //startActivity(new Intent(NewMessage.this, NewMessage.class)); //Another option, simply refreshing the page
            }
        });
    }

    public Boolean Send(){
        final String recipient = this.recipient.toString().trim();
        final String patientId = this.patientId.toString().trim();
        final String patientName = this.patientName.toString().trim();
        final String testName = this.testName.toString().trim();
        final String componentName = this.componentName.toString().trim();
        final String measuredAmount = this.measuredAmount.toString().trim();
        final String isUrgent = this.isUrgent.toString().trim();
        final String comments = this.comments.toString().trim();

        /* //TODO check if it's OK to use those as strings when sending them to the query using the hashmap
        final int recipient = Integer.parseInt(this.recipient.toString().trim());
        final String patientId = this.patientId.toString().trim();
        final String patientName = this.patientName.toString().trim();
        final String testName = this.testName.toString().trim();
        final String componentName = this.componentName.toString().trim();
        final float measuredAmount = Float.parseFloat(this.measuredAmount.toString().trim());
        final Boolean isUrgent = Boolean.parseBoolean(this.isUrgent.toString().trim());
        final String comments = this.comments.toString().trim();
        */

        progressDialog.setMessage("ההודעה שלך נשלחת. נא להמתין לאישור...");
        progressDialog.show();
        //Creating a Volley request to communicate with PHP pages
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_NEWMESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("recipient", recipient);
                params.put("patientId",patientId);
                params.put("patientName",patientName);
                params.put("testName",testName);
                params.put("componentName",componentName);
                params.put("measuredAmount",measuredAmount);
                params.put("isUrgent",isUrgent);
                params.put("comments",comments);
                return params;
            }
        };
        //Queueing the request since the operation will take some time
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return true;
    }
}