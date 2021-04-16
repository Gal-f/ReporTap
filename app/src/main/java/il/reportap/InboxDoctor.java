package il.reportap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginregister.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxDoctor extends OptionsMenu {

    private RecyclerView recyclerView;
    private AdapterActivityInboxDr adapter;
    private List<ModelActivityInboxDr> modelActivityInboxDrList;
    private List<ModelActivityInboxDr> urgentList;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_doctor);
        Button btn = (Button)findViewById(R.id.toDoB);
        btn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.stroke));
        btn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        modelActivityInboxDrList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLs.URL_INBOXDR,
                //lambda expression
                response -> {


                    try {
                        JSONObject repObj = new JSONObject(response);
                        JSONArray repArray = repObj.getJSONArray("report");

                        for(int i=0; i<repArray.length(); i++){
                            ModelActivityInboxDr modelActivityInboxDr = new ModelActivityInboxDr();
                            JSONObject jObg = new JSONObject();
                            jObg= repArray.getJSONObject(i);
                            modelActivityInboxDr.setId(jObg.getInt("id"));
                            modelActivityInboxDr.setSentTime(jObg.getString("sent_time"));
                            modelActivityInboxDr.setPatientId(jObg.getString("patient_id"));
                            modelActivityInboxDr.setTestName(jObg.getString("name"));
                            modelActivityInboxDr.setUrgent(jObg.getInt("is_urgent"));
                            modelActivityInboxDrList.add(modelActivityInboxDr);
                            System.out.println(modelActivityInboxDrList.get(i).getId());
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new AdapterActivityInboxDr(modelActivityInboxDrList,getApplicationContext());
                    recyclerView.setAdapter(adapter);

                },
                //lambda expression
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
        {
            @Nullable
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("department", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUser().getDepartment()));
                System.out.println(params.get("department"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        CheckBox chkBx = (CheckBox)findViewById(R.id.checkBox);
        chkBx.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                if(chkBx.isChecked()){
                    urgentList= new ArrayList<>();
                    for (int i = 0; i< modelActivityInboxDrList.size(); i++)
                    {
                        if (modelActivityInboxDrList.get(i).getIsUrgent()==1)
                        {
                            urgentList.add(modelActivityInboxDrList.get(i));
                        }
                    }
                    adapter = new AdapterActivityInboxDr(urgentList,getApplicationContext());
                    ImageView img = (ImageView)findViewById(R.id.imageView3);
                    img.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red),
                            PorterDuff.Mode.MULTIPLY);
                }
                else {
                    adapter = new AdapterActivityInboxDr(modelActivityInboxDrList,getApplicationContext());
                    ImageView img = (ImageView)findViewById(R.id.imageView3);
                    img.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.gray),
                            PorterDuff.Mode.MULTIPLY);

                }
                recyclerView.setAdapter(adapter);

            }

        });
        Button btnS= (Button)findViewById(R.id.sentB);
        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), SentDoctor.class));
            }
        });

    }




}
