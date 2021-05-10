package il.reportap;

import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.example.loginregister.R;

public class ButtonsOptions extends OptionsMenu {

    String dept;
    Button btnI, btnS, btnD;

    public void colorButton(String deptname) {
        btnI = (Button) findViewById(R.id.toDoB);
        btnS = (Button) findViewById(R.id.sentB);
        btnD = (Button) findViewById(R.id.doneB);
        Button temp;
        this.dept = deptname;
        if (dept.equals("InboxLab") || dept.equals("SentLab") || dept.equals("DoneLab")) {
            btnI.setText("תגובות לטיפולי");
            btnS.setText("מסרים שנשלחו");
            btnD.setText("תגובות שטופלו");
        }
        else {
            btnI.setText("מסרים לטיפולי");
            btnS.setText("תגובות שנשלחו");
            btnD.setText("מסרים שטופלו");
        }

        if (dept.equals("InboxLab") || dept.equals("InboxDoctor")) {
                temp = btnI;
            }
        else if (dept.equals("SentLab") || dept.equals("SentDoctor")) {
                temp = btnS;
            } else {
                temp = btnD;
            }

        temp.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.stroke));
        temp.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
}
}
