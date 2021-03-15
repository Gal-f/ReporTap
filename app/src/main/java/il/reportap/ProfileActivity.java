package il.reportap;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.R;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewId, textViewUsername, textViewEmail, textViewEmployeeNumber,
            textViewFullName, textViewJobTitle, textViewPhoneNumber;
    //TextView textViewGender;

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


        textViewId = (TextView) findViewById(R.id.textViewId);
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewEmployeeNumber =  (TextView) findViewById(R.id.textViewEmployeeNumber);
        textViewFullName = (TextView) findViewById(R.id.textViewFullName);
        textViewJobTitle = (TextView) findViewById(R.id.textViewJobTitle);
        textViewPhoneNumber =  (TextView) findViewById(R.id.textViewPhoneNumber);

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //setting the values to the textviews
        textViewId.setText(String.valueOf(user.getId()));
        textViewUsername.setText(user.getUsername());
        textViewEmail.setText(user.getEmail());
        textViewEmployeeNumber.setText(user.getEmployeeNumber());
        textViewFullName.setText(user.getFullName());
        textViewJobTitle.setText(user.getJobTitle());
        textViewPhoneNumber.setText(user.getPhoneNumber());

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