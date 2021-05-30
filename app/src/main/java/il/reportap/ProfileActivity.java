package il.reportap;


import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.il.reportap.R;

public class ProfileActivity extends OptionsMenu {

    private TextView textViewEmployeeNumber,
            textViewFullName, textViewEmail, textViewJobTitle, textViewPhoneNumber, textViewDepartment, notActiveUser;
    private boolean isActive = false;
    private LinearLayout notActive;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = SharedPrefManager.getInstance(this).getUser();
        textViewEmployeeNumber = (TextView) findViewById(R.id.textViewEmployeeNumber);
        textViewFullName = (TextView) findViewById(R.id.textViewFullName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewJobTitle = (TextView) findViewById(R.id.textViewJobTitle);
        textViewPhoneNumber = (TextView) findViewById(R.id.textViewPhoneNumber);
        textViewDepartment = (TextView) findViewById(R.id.textViewDepartment);
        notActive = findViewById(R.id.notActive);
        isActive = user.isActive();
        if (!isActive) {
            notActive.setVisibility(View.VISIBLE);
            notActiveUser = findViewById(R.id.textViewNotActive);
            notActiveUser.setText(Html.fromHtml(getString(R.string.notApprovedYet)));
        } else {
            notActive.setVisibility(View.GONE);
        }
        //setting the values to the textviews
        textViewEmployeeNumber.setText("מספר עובד: " + user.getEmployeeNumber());
        textViewFullName.setText(user.getFullName());
        textViewEmail.setText("אימייל: " + user.getEmail());
        textViewJobTitle.setText(user.getJobTitle());
        textViewPhoneNumber.setText("מספר טלפון: " + user.getPhoneNumber());
        textViewDepartment.setText("מחלקה: " + user.getDeptName());
    }
}

