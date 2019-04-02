/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.Utils;


import com.google.gson.Gson;
import com.grokonez.jwtauthentication.model.UserAccount;
import com.grokonez.jwtauthentication.model.UserCreditCard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 *
 * @author smile
 *
 *
 */
@Component
public class TransactionsHelper {


    @Value("${grokonez.app.otherbankapi}")
    private  String uri;

    @Value("${grokonez.app.otherbankapikey}")
    private String key ;
    
    
    // Helper Functions
    public void deposit( UserAccount acct, double amount )
    {

        double balance = acct.getAmount();   // get the current balance
        balance += amount;                    // adjust the balance
        acct.setAmount(Double.parseDouble(String.format("%.2f", balance )));           // set the new balance
    }

    public void deposit(UserCreditCard creditcard, double amount )
    {

        double creditlimit = creditcard.getAmountavailable();   // get the current creditlimit
        double creditowed = creditcard.getAmountowned();   // get the current owed
        creditlimit += amount;                    // adjust the creditlimit
        creditowed  -= amount;                    // adjust the creditlimit
        creditcard.setAmountavailable(Double.parseDouble(String.format("%.2f", creditlimit)));// set the new creditlimit
        creditcard.setAmountowned(Double.parseDouble(String.format("%.2f",creditowed)));// set the new current owed

    }

    public void transfer( UserAccount fromAcct, UserAccount toAcct, double amount )
    {
        deposit( fromAcct, -amount );   // take money from one account
        deposit( toAcct, amount );      // add the money to the other account
    }

    public void creditcardpayment( UserAccount fromAcct, UserCreditCard creditcard, double amount )
    {
        deposit( fromAcct, -amount );   // take money from one account
        deposit( creditcard, amount );      // add the money to the other account
    }

    
      public  String OtherBankTransfer(Object input) throws IOException  {
     
        try {
            Gson gson = new Gson();
            String json = gson.toJson(input);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-api-key",key);
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            
            if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {

                
                if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                 return "OTHER BANK API Fail -> Receiver Account Does Not Exist. HTTP CODE: " + HttpURLConnection.HTTP_NOT_FOUND ;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN)
                 return "OTHER BANK API Fail -> UNAuthorized Invalid Key. HTTP CODE: " + HttpURLConnection.HTTP_FORBIDDEN;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST)
                 return "OTHER BANK API Fail -> Bad JSON format HTTP CODE: " + HttpURLConnection.HTTP_BAD_REQUEST;
            }

            else {
                return "OTHER BANK API SUCCESS -> Funds successfully transfered. HTTP CODE: " + HttpURLConnection.HTTP_NO_CONTENT ;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

            return "Success";
        } catch (MalformedURLException ex) {
           return ex.getMessage();
        }
        catch (Exception ex) {
           return ex.getMessage();
        }

      }
}
