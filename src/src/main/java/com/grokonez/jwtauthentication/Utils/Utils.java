/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author smile
 */
public class Utils {
    
    public static String getCurrentTimeStamp() {
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
    Date now = new Date();
    String strDate = sdfDate.format(now);
    return strDate;
}
    
    public static String getCurrentTimeStampwithtime() {
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//dd/MM/yyyy
    Date now = new Date();
    String strDate = sdfDate.format(now);
    return strDate;
    }

    public static String getYearTimeStampMMYY(int year) {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/YYYY");//dd/MM/yyyy
        date.setTime(new Date());
        date.add(Calendar.YEAR,year);
        String strDate = sdfDate.format(date.getTime());
        return strDate;
    }
}
