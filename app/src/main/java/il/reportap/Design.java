package il.reportap;

public class Design { // Used to maintain a uniform design throughout the project, using constants to keep fixed design parameters.
    public final String font_Heading1 = "android:textSize=\"18dp\"\n" +
            "                android:textColor=\"@color/black\"\n" +
            "                android:textStyle=\"bold\"";

    public final String font_Heading2 = "android:textSize=\"16dp\"\n" +
            "                android:textColor=\"@color/black\"\n" +
            "                android:textStyle=\"bold\"";

    public final String font_normalText = "android:textSize=\"16dp\"\n" +
            "                android:textColor=\"@color/black\"";

    public final String horizontalLine_h1 = "<View\n" +
            "                android:layout_width=\"match_parent\"\n" +
            "                android:layout_height=\"3dp\"\n" +
            "                android:background=\"#0F2D6C\"/>";

    public final String horizontalLine_h2 = "<View\n" +
            "                android:layout_width=\"match_parent\"\n" +
            "                android:layout_height=\"2dp\"\n" +
            "                android:background=\"#74C043\"/>";
}
