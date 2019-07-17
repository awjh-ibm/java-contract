package org.awjh.ledger_api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class Util {
    public static void logStackTrace(Exception err) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        err.printStackTrace(pw);
        String sStackTrace = sw.toString();
        System.out.println(sStackTrace);
    }

    public static void log(String... strings) {
        String message = "";
        for (String str : strings) {
            message += str + " ";
        }
        System.out.println(message);
    }

    public static void labeledLog(String label, String... strings) {
        label = label + " ==> ";
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(label);
        for (String str : strings) {
            arrayList.add(str);
        }
        String[] newList = new String[strings.length + 1];
        Util.log(arrayList.toArray(newList));
    }
}
