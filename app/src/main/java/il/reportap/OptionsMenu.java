package il.reportap;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.R;

public class OptionsMenu extends AppCompatActivity {
    Menu optionsMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        if(SharedPrefManager.getInstance(this).getUser().getDeptType().equals("lab")){
            /*
            sendMessage = findViewById(R.id.sendMessage);
            sendMessage.setVisible(true);*/
            MenuItem sendMessage = optionsMenu.findItem(R.id.sendMessage);
            sendMessage.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())){
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
          //  case R.id.settings:
            //    startActivity(new Intent(this, SettingsActivity.class));
              //  break;
            case R.id.sendMessage:
                if(SharedPrefManager.getInstance(this).getUser().isActive){
                    startActivity(new Intent(getApplicationContext(), NewMessage.class));
                }
                else{
                    Toast.makeText(getApplicationContext(), "יש להמתין לאישור מנהל המערכת כדי לשלוח הודעות", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.logout:
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + (item.getItemId()));
        }

        return super.onOptionsItemSelected(item);
    }
}
