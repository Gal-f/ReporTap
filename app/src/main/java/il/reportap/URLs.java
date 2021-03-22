package il.reportap;

public class URLs {

    private static final String ROOT_URL = "https://edenpe.mtacloud.co.il/Api.php?apicall=";    //Actual online server
    //private static final String ROOT_URL = "http://192.168.1.188/reporTap/Api.php?apicall=";  //Local virtual server
    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN= ROOT_URL + "login";
    public static final String URL_NEWMESSAGE= ROOT_URL + "newMessage";

}