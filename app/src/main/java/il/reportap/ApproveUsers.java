package il.reportap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.il.reportap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ApproveUsers extends OptionsMenu  {

    private RecyclerView usersRecyclerView;
    private List<User> usersList;
    private LinearLayout chooseOperation;
    private TextView noMoreUsers;
    private HashMap<Integer, String> deptMap;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_users);
        usersRecyclerView = findViewById(R.id.users_recycleview);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setHasFixedSize(true);
        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getDrawable(R.drawable.dividerbig));
        usersRecyclerView.addItemDecoration(divider);
        usersList = new ArrayList<>();
        chooseOperation =  (LinearLayout) findViewById(R.id.chooseOperation);
        TextView greeting = findViewById(R.id.helloUser);
        greeting.setText("שלום "+SharedPrefManager.getInstance(this).getUser().getFullName()+", כיף שחזרת!");
        noMoreUsers = findViewById(R.id.noMoreUsers);
        deptMap = new HashMap<Integer, String>();
        populateDeptMap();

    }

    @Override
    public void onBackPressed()
    {
        usersRecyclerView.setVisibility(View.GONE);
        final int visibility = noMoreUsers.getVisibility();
        if(visibility == 0){ //this is visible
            noMoreUsers.setVisibility(View.GONE);
        }
        //so the admin would see the Linear layout with the options (approve users/suspend users)
        chooseOperation.setVisibility(View.VISIBLE);
        usersList.clear();
    }


    //in order to get the departments of the not active users (every user has his department id as a foreign key, we want the name)
    private void populateDeptMap() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_DEPTS_N_TESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject entireResponse = new JSONObject(response);
                    JSONArray deptsArray = entireResponse.getJSONArray("departments");
                    for (int i = 0; i < deptsArray.length(); i++) {
                        JSONObject dept = deptsArray.getJSONObject(i);
                        deptMap.put(dept.getInt("deptID"), dept.getString("deptName"));
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "קרתה שגיאה, נא נסו מאוחר יותר או פנו למנהל המערכת", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getUsersList () {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
            URLs.URL_SYSTEMMANAGER,
            //lambda expression
            response -> {
                String errorMessage;

                try {
                    JSONObject usersObj = new JSONObject(response);
                    errorMessage = usersObj.getString("message");
                    if(!usersObj.getBoolean("error")) {
                        JSONArray usersArray = usersObj.getJSONArray("users");
                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject jObg = new JSONObject();
                            jObg = usersArray.getJSONObject(i);
                            User user = new User(
                                    jObg.getString("full_name"),
                                    jObg.getString("employee_ID"),
                                    jObg.getString("role"),
                                    jObg.getInt("works_in_dept")
                            );
                            usersList.add(user);
                        }
                    }
                    else{ //all the users are active
                        noMoreUsers.setText(errorMessage);
                        noMoreUsers.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                usersRecyclerView.setAdapter(new UsersAdapter(usersList));
            },
            //handling volley error
            error -> Toast.makeText(getApplicationContext(), "עקב תקלה לא ניתן לצפות ברשימת המשתמשים הממתינים לאישור.", Toast.LENGTH_LONG).show()) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    public void goApproveUsers(View view) {
        chooseOperation.setVisibility(View.GONE);
        usersRecyclerView.setVisibility(View.VISIBLE);
        getUsersList();
    }

    public void suspendUsers(View view) {
        startActivity(new Intent(getApplicationContext(), SuspendUsers.class));
    }


    class UsersAdapter extends RecyclerView.Adapter<UserViewHolder>{
        private List<User> users;

        public UsersAdapter(List<User>users){
            super();
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            holder.bind(this.users.get(position));
        }

        @Override
        public int getItemCount() {
            return this.users.size();
        }

    }


    class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView fullName, employeeID, jobTitle, department;
        public UserViewHolder(ViewGroup container){
            super(LayoutInflater.from(ApproveUsers.this).inflate(R.layout.user_list_item, container, false));
            fullName = (TextView) itemView.findViewById(R.id.fullName);
            employeeID =  itemView.findViewById(R.id.employeeID);
            jobTitle = (TextView) itemView.findViewById(R.id.jobTitle);
            department = (TextView) itemView.findViewById(R.id.department);
        }


        private void approveUser(String employeeID) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    URLs.URL_APPROVEUSER,
                    //lambda expression
                    response -> {
                        String errorMessage;
                        try {
                            JSONObject Obj = new JSONObject(response);
                            errorMessage = Obj.getString("message");
                            if(Obj.getBoolean("error")){
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "הפעולה בוצעה בהצלחה", Toast.LENGTH_LONG).show();
                                usersList.remove(getAdapterPosition());
                                usersRecyclerView.removeViewAt(getAdapterPosition());
                                Objects.requireNonNull(usersRecyclerView.getAdapter()).notifyItemRemoved(getAdapterPosition());
                                usersRecyclerView.getAdapter().notifyItemRangeChanged(getAdapterPosition(), usersList.size());
                                if(usersList.size() == 0){ //the admin approved all the users
                                    noMoreUsers.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    //handling volley error
                    error -> Toast.makeText(getApplicationContext(), "שגיאה בביצוע הפעולה. עימך הסליחה.", Toast.LENGTH_LONG).show())
            {

                @Override
                protected HashMap<String, String> getParams() throws AuthFailureError {
                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("employee_ID", employeeID);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(ApproveUsers.this);
            requestQueue.add(stringRequest);
        }

        public void bind(User user){
            fullName.setText("שם: " + user.getFullName());
            employeeID.setText("מספר עובד: " +user.getEmployeeNumber());
            jobTitle.setText("תפקיד: " +user.getJobTitle());
            department.setText("מחלקה: " +deptMap.get(user.getDeptID()));
            itemView.findViewById(R.id.buttonApprove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    approveUser(user.getEmployeeNumber());
                }
            });
        }

    }
}

