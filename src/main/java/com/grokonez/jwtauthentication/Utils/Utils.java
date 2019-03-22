/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.Utils;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author smile
 */
@Component
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
