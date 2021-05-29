package il.reportap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.il.reportap.R;

import java.util.Objects;

public class OptionsMenu extends NavigateUser {
    Menu optionsMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem sendMessage = optionsMenu.findItem(R.id.sendMessage);
        //show the "Send message" button only to active lab workers
        if(SharedPrefManager.getInstance(this).getUser().getDeptType().equals("lab") &&
                SharedPrefManager.getInstance(this).getUser().isActive()){
            sendMessage.setVisible(true);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);    // Hide the app name on the action bar
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())){
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.sendMessage:
                    startActivity(new Intent(getApplicationContext(), NewMessage.class));
                break;
            case R.id.logout:
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                break;

            case R.id.logo:
                if(SharedPrefManager.getInstance(this).getUser().isActive()) {
                    try {
                        goToClass(SharedPrefManager.getInstance(this).getUser().getDeptType());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + (item.getItemId()));
        }

        return super.onOptionsItemSelected(item);
    }
}
