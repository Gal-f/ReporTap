package il.reportap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "reporTap";
    private static final String KEY_ID = "keyid";
    private static final String KEY_EMPNUM = "keyemployeenumber";
    private static final String KEY_FULLNAME = "keyfullname";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_JOBTITLE = "keyjobtitle";
    private static final String KEY_PHONENUMBER = "keyphonemumber";
    private static final String KEY_DEPT = "keydepartment";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_EMPNUM, user.getEmployeeNumber());
        editor.putString(KEY_FULLNAME, user.getFullName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_JOBTITLE, user.getJobTitle());
        editor.putString(KEY_PHONENUMBER, user.getPhoneNumber());
        editor.putInt(KEY_DEPT, user.getDepartment());
        editor.apply();
    }

    //this method will check whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMPNUM, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_EMPNUM, null),
                sharedPreferences.getString(KEY_FULLNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_JOBTITLE, null),
                sharedPreferences.getString(KEY_PHONENUMBER, null),
                sharedPreferences.getInt(KEY_DEPT, -1)
        );
    }

    //this method will logout the user 
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(mCtx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }
}

 