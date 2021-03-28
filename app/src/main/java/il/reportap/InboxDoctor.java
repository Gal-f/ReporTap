package il.reportap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

import java.util.ArrayList;
import java.util.List;

public class InboxDoctor extends AppCompatActivity {

        private static final String URL = "http://androidcodefinder.com/RecyclerViewJson.json";
        private RecyclerView recyclerView;
        private RecyclerView.Adapter adapter;
        private List<ModelActivity> modelActivityList;
        private RelativeLayout relativeLayout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
            recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            modelActivityList = new ArrayList<>();

            loadData();
        }

        private void loadData() {

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            relativeLayout.setVisibility(View.GONE);

                            try {
                                //JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = new JSONArray(response);

                                for(int i=0; i<jsonArray.length(); i++){

                                    JSONObject o = jsonArray.getJSONObject(i);
                                    ModelActivity modelActivity = new ModelActivity(
                                            o.getString("sentTime"),
                                            o.getString("patientId"),
                                            o.getString("testName"),
                                            o.getBoolean("date")
                                    );
                                    modelActivityList.add(modelActivity);
                                }

                                adapter = new AdapterActivity(modelActivityList, getApplicationContext());
                                recyclerView.setAdapter(adapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

}
