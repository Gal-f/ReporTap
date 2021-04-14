package il.reportap;

import com.android.volley.toolbox.StringRequest;

public class URLs {

    private static final String ROOT_URL = "http://edenpe-is.mtacloud.co.il/Api.php?apicall=";    //Actual online server
   // private static final String ROOT_URL = "http://192.168.1.188/reporTap/Api.php?apicall=";  //Local virtual server
    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_NEWMESSAGE = ROOT_URL + "newMessage";
    public static final String URL_GETMESSAGE = ROOT_URL + "getMessage";
    public static final String URL_GET_DEPTS_N_TESTS = ROOT_URL + "getDeptsAndTests";
    public static final String URL_INBOXDR = ROOT_URL + "inboxdr";
    public static final String URL_MARK_AS_READ = ROOT_URL + "markAsRead";
}