package il.reportap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ApproveUsers extends OptionsMenu {

    RecyclerView usersRecyclerView;
    private List<User> usersList;

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
        getUsersList();

    }

    public void getUsersList () {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLs.URL_SYSTEMMANAGER,
                //lambda expression
                response -> {
                    String errorMessage = "";

                    try {
                        JSONObject usersObj = new JSONObject(response);
                        JSONArray usersArray = usersObj.getJSONArray("users");
                        errorMessage = usersObj.getString("message");
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    usersRecyclerView.setAdapter(new UsersAdapter(usersList));
                },
                //lambda expression
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


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
                        String errorMessage = "";
                        try {
                            JSONObject Obj = new JSONObject(response);
                            errorMessage = Obj.getString("message");
                            if(Obj.getBoolean("error")){
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "הפעולה בוצעה בהצלחה", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    //lambda expression
                    error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
            {
                @Nullable
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
            switch( user.getDepartment()){
                case 1:
                    department.setText("מחלקה: מעבדה מיקרוביולוגית");
                case 2:
                    department.setText("מחלקה: פנימית א ");
            }

            itemView.findViewById(R.id.buttonApprove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    approveUser(user.getEmployeeNumber());
                }
            });
        }

    }
}

