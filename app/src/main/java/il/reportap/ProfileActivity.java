package il.reportap;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.R;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewEmployeeNumber,
            textViewFullName, textViewJobTitle, textViewPhoneNumber, textViewDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


      //  textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewEmployeeNumber =  (TextView) findViewById(R.id.textViewEmployeeNumber);
        textViewFullName = (TextView) findViewById(R.id.textViewFullName);
        textViewJobTitle = (TextView) findViewById(R.id.textViewJobTitle);
        textViewPhoneNumber =  (TextView) findViewById(R.id.textViewPhoneNumber);
        textViewDepartment=  (TextView) findViewById(R.id.textViewDepartment);

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //setting the values to the textviews
       // textViewUsername.setText("שם משתמש: " + user.getUsername());
        textViewEmployeeNumber.setText("מספר עובד: " + user.getEmployeeNumber());
        textViewFullName.setText("שם מלא: " + user.getFullName());
        textViewJobTitle.setText("תפקיד: " + user.getJobTitle());
        textViewPhoneNumber.setText("מספר טלפון: " + user.getPhoneNumber());
        switch(user.getDepartment()){
            case 1:
                textViewDepartment.setText(" מחלקה: מעבדה מיקרוביולוגית");
                break;
            case 2:
                textViewDepartment.setText("מחלקה: פנימית א");
        }

        //when the user presses logout button
        //calling the logout method
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });
    }
}