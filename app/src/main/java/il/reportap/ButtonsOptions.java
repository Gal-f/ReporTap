package il.reportap;

import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.il.reportap.R;

public class ButtonsOptions extends OptionsMenu {

    String deptType, deptName;
    Button btnI, btnS, btnD;

    public void colorButton(String deptType,String deptName) {
        btnI = (Button) findViewById(R.id.toDoB);
        btnS = (Button) findViewById(R.id.sentB);
        btnD = (Button) findViewById(R.id.doneB);
        Button temp;
        this.deptType = deptType;
        this.deptName=deptName;
        if (deptType.equals("lab")) {
            btnI.setText("תגובות לטיפולי");
            btnS.setText("מסרים שנשלחו");
            btnD.setText("תגובות שטופלו");
        }
        else if (deptType.equals("medical_dept")) {
            btnI.setText("מסרים לטיפולי");
            btnS.setText("תגובות שנשלחו");
            btnD.setText("מסרים שטופלו");
        }

        if (deptName.equals("InboxLab") || deptName.equals("InboxDoctor")) {
                temp = btnI;
            }
        else if (deptName.equals("SentLab") || deptName.equals("SentDoctor")) {
                temp = btnS;
            }
        else if (deptName.equals("DoneLab") || deptName.equals("DoneDoctor")) {
                temp = btnD;
            }
        else
        {
           temp=null;
        }
        if (temp!=null) {
            temp.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.stroke));
            temp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
}
}
