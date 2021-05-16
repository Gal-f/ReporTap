package il.reportap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class SentDoctor extends ButtonsOptions {

    private RecyclerView recyclerView;
    private AdapterActivitySentDr adapter;
    private List<ModelActivitySentDr> modelActivitySentDrList;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        TextView desc = (TextView)findViewById(R.id.layoutDescription);
        ImageView urgIc= (ImageView)findViewById(R.id.urgentIcon);
        CheckBox chb = (CheckBox)findViewById(R.id.checkBox) ;
        desc.setText(R.string.sentDesc);
        urgIc.setVisibility(View.GONE);
        chb.setVisibility(View.GONE);
        colorButton(SharedPrefManager.getInstance(this).getUser().getDeptType(), getClass().getSimpleName());
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewInbox);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getDrawable(R.drawable.dividerbig));
        recyclerView.addItemDecoration(divider);
        modelActivitySentDrList = new ArrayList<>();
        myStringRequest();

        SwipeRefreshLayout mySwipeToRefresh= (SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);
        mySwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                modelActivitySentDrList.clear();
                myStringRequest();
                mySwipeToRefresh.setRefreshing(false);
            }
        });

        Button btnD= (Button)findViewById(R.id.doneB);
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), DoneDoctor.class));
            }
        });
        Button btnI= (Button)findViewById(R.id.toDoB);
        btnI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), InboxDoctor.class));
            }
        });


    }

    public void myStringRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLs.URL_SENTDR,
                //lambda expression
                response -> {


                    try {
                        JSONObject repObj = new JSONObject(response);
                        JSONArray repArray = repObj.getJSONArray("report");

                        for(int i=0; i<repArray.length(); i++){

                            JSONObject jObg = new JSONObject();
                            jObg= repArray.getJSONObject(i);
                            ModelActivitySentDr modelActivitySentDr = new ModelActivitySentDr(jObg.getInt("id"),
                                    jObg.getString("sent_time"),
                                    jObg.getString("full_name_p"),
                                    jObg.getString("text"),
                                    jObg.getString("sender_name"),
                                    jObg.getString("patient_id"),
                                    jObg.getString("name"),
                                    jObg.getString("confirm_time"));
                            modelActivitySentDrList.add(modelActivitySentDr);
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new AdapterActivitySentDr(modelActivitySentDrList,getApplicationContext());
                    recyclerView.setAdapter(adapter);

                },
                //lambda expression
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
        {
            @Nullable
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("works_in_dept", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUser().getDepartment()));
                System.out.println(params.get("works_in_dept"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
