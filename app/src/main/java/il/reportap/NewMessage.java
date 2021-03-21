package il.reportap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginregister.R;

public class NewMessage extends AppCompatActivity {

    private AutoCompleteTextView recipient, testName, componentName;
    private EditText patientId, patientName, measuredAmount, comments;
    private CheckBox isUrgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        this.recipient = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        this.patientId = (EditText) findViewById(R.id.editTextPatientIdNumber);
        this.patientName = (EditText) findViewById(R.id.editTextPatientName);
        this.testName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTestName);
        this.componentName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewComponentName);
        this.measuredAmount = (EditText) findViewById(R.id.editTextMeasuredAmount);
        this.isUrgent = (CheckBox) findViewById(R.id.checkBoxUrgent);
        this.comments = (EditText) findViewById(R.id.editTextTextMultiLineComments);

        //TODO Create auto-complete for patient ID? (optional)

        //Define autocomplete fields options //TODO Populate recipient, testName, componentName with choices from the DB tables.
        String[] departments = new String[]{"בחר מחלקה","מעבדה מיקרוביולוגית", "פנימית א"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, departments);
        recipient.setAdapter(adapter);

        String[] tests = new String[]{"תרבית דם","תרבית שתן", "PCR קורונה"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, tests);
        testName.setAdapter(adapter2);

        String[] components = new String[]{"חיידק","בקטריה", "שניבוריג"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, components);
        testName.setAdapter(adapter3);

        findViewById(R.id.buttonSendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send();
            }
        });

        findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                recipient.setText("");
                patientId.setText("");
                patientName.setText("");
                testName.setText("");
                componentName.setText("");
                measuredAmount.setText("");
                isUrgent.setChecked(false);
                comments.setText("");
                //startActivity(new Intent(NewMessage.this, NewMessage.class)); //Another option, simply refreshing the page
            }
        });
    }

    public Boolean Send(){
        return true;
    }
}