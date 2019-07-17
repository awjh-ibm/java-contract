package org.awjh.ledger_api;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Util {
    public static void logStackTrace(Exception err) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        err.printStackTrace(pw);
        String sStackTrace = sw.toString();
        System.out.println(sStackTrace);
    }
}
