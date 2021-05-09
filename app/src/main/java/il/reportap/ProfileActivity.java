package il.reportap;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.loginregister.R;

public class ProfileActivity extends OptionsMenu {

    TextView textViewEmployeeNumber,
            textViewFullName, textViewEmail, textViewJobTitle, textViewPhoneNumber, textViewDepartment;
    boolean isActive;
    LinearLayout notActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User user = SharedPrefManager.getInstance(this).getUser();
        //TODO pull the user details from the server if isActive==false, in order to check if he had been approved by an admin

        textViewEmployeeNumber =  (TextView) findViewById(R.id.textViewEmployeeNumber);
        textViewFullName = (TextView) findViewById(R.id.textViewFullName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewJobTitle = (TextView) findViewById(R.id.textViewJobTitle);
        textViewPhoneNumber =  (TextView) findViewById(R.id.textViewPhoneNumber);
        textViewDepartment=  (TextView) findViewById(R.id.textViewDepartment);
        isActive = user.isActive;
        notActive = findViewById(R.id.notActive);
        if(!isActive){
            notActive.setVisibility(View.VISIBLE);
        }
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
                break;
            case 6:
                textViewDepartment.setText("מחלקה: הנהלה");
                break;
        }

    }
}