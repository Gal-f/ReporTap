package il.reportap;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.R;

public class OptionsMenu extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())){
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.logout:
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                break;
            case R.id.send_message:
                startActivity(new Intent(getApplicationContext(), NewMessage.class));
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + (item.getItemId()));
        }

        return super.onOptionsItemSelected(item);
    }
}
