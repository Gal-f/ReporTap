package il.reportap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class InboxLab extends ButtonsOptions {

    private RecyclerView recyclerView;
    private AdapterActivityInboxLab adapter;
    private List<ModelActivityInboxLab> modelActivityInboxLabList;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        colorButton(SharedPrefManager.getInstance(this).getUser().getDeptType(), getClass().getSimpleName());
        TextView desc = (TextView)findViewById(R.id.layoutDescription);
        ImageView urgIc= (ImageView)findViewById(R.id.urgentIcon);
        CheckBox chb = (CheckBox)findViewById(R.id.checkBox) ;
        desc.setText(R.string.inboxDescL);
        urgIc.setVisibility(View.GONE);
        chb.setVisibility(View.GONE);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewInbox);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getDrawable(R.drawable.dividerbig));
        recyclerView.addItemDecoration(divider);

        modelActivityInboxLabList = new ArrayList<>();
        myStringRequest();
//        SwipeRefreshLayout mySwipeToRefresh= (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
//        mySwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                myStringRequest();
//                mySwipeToRefresh.setRefreshing(false);
//            }
//        });

       btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), SentLab.class));
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), DoneLab.class));
            }
        });

    }


    public void myStringRequest (){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLs.URL_INBOXLAB,
                //lambda expression
                response -> {


                    try {
                        JSONObject repObj = new JSONObject(response);
                        JSONArray repArray = repObj.getJSONArray("report");

                        for(int i=0; i<repArray.length(); i++){
                            JSONObject jObg = new JSONObject();
                            jObg= repArray.getJSONObject(i);
                            ModelActivityInboxLab modelActivityInboxLab = new ModelActivityInboxLab(jObg.getInt("id"),
                                    jObg.getInt("messageID"),
                                    jObg.getString("sent_time"),
                                    jObg.getString("patient_id"),
                                    jObg.getString("name"),
                                    jObg.getString("text"),
                                    jObg.getString("measurement"),
                                    jObg.getString("component"),
                                    jObg.getInt("is_value_bool"),
                                    Float.valueOf((float) jObg.getDouble("result_value")),
                                    jObg.getString("full_name"),
                                    jObg.getString("dept_name"));
                            modelActivityInboxLabList.add(modelActivityInboxLab);
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new AdapterActivityInboxLab(modelActivityInboxLabList,getApplicationContext());
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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
