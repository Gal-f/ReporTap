package il.reportap;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.loginregister.R;

public class ProfileActivity extends OptionsMenu {

    TextView textViewEmployeeNumber,
            textViewFullName, textViewEmail, textViewJobTitle, textViewPhoneNumber, textViewDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }


        textViewEmployeeNumber =  (TextView) findViewById(R.id.textViewEmployeeNumber);
        textViewFullName = (TextView) findViewById(R.id.textViewFullName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewJobTitle = (TextView) findViewById(R.id.textViewJobTitle);
        textViewPhoneNumber =  (TextView) findViewById(R.id.textViewPhoneNumber);
        textViewDepartment=  (TextView) findViewById(R.id.textViewDepartment);

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //setting the values to the textviews
        textViewEmployeeNumber.setText("מספר עובד: " + user.getEmployeeNumber());
        textViewFullName.setText(user.getFullName());
        textViewEmail.setText("אימייל: " + user.getEmail());
        textViewJobTitle.setText(user.getJobTitle());
        textViewPhoneNumber.setText("מספר טלפון: " + user.getPhoneNumber());
        switch(user.getDepartment()){
            case 1:
                textViewDepartment.setText(" מחלקה: מעבדה מיקרוביולוגית");
                break;
            case 2:
                textViewDepartment.setText("מחלקה: פנימית א");
        }

    }
}