package il.reportap;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class NavigateUser extends AppCompatActivity {
    public String goToClass;

    public void goToClass(String deptType) throws ClassNotFoundException {
            if(deptType.equals("lab")){
                goToClass = "il.reportap.InboxLab";
            }
            else if(deptType.equals("medical_dept")){
                goToClass = "il.reportap.InboxDoctor";
            }
            else{
                goToClass = "il.reportap.ApproveUsers";
            }
        startActivity(new Intent(getApplicationContext(), Class.forName(goToClass)));
        }

}
