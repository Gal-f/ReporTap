package il.reportap;


import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
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
            //in order to send the user a notification when the manager approves his account
            FirebaseMessaging.getInstance().subscribeToTopic(user.getEmployeeNumber())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //msg is for our needs only, we don't show it to the user.
                        String msg = "Done";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.d("user's subscription", msg);
                    }
                });
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

