package il.reportap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.R;

public class HeaderActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);

    }

    public void goProfile(View view) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }
