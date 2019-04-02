/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.cronjob;



import com.grokonez.jwtauthentication.Utils.TransactionsHelper;
import com.grokonez.jwtauthentication.Utils.Utils;
import com.grokonez.jwtauthentication.model.Transactions;
import com.grokonez.jwtauthentication.model.UserCreditCard;
import com.grokonez.jwtauthentication.repository.TransactionsRepository;
import com.grokonez.jwtauthentication.repository.UserCreditCardRepository;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author smile
 */
@Configuration
@EnableScheduling
public class CronJob {
      
       private static int TIMEDIFF =  1*60; // 20 Mins
       
        @Autowired
        TransactionsRepository transRepository;
        
        @Autowired
         UserCreditCardRepository creditcardRepository;
        
       TransactionsHelper transhelper = new TransactionsHelper();
    
       
       @Scheduled(cron = "*/1 * * * * *") // Every 1 Second
       public void  Changetransactionstatus  (){
           
           
            List<Transactions> results = transRepository.findTransactionsbyStatus(Transactions.TransStatus.CREATED.toString());
            System.out.println(
                  "Change Status Cron Job - " + Utils.getCurrentTimeStampwithtime()+ "Rows:" + results.size());
           try {
               
                Date d = new Date(System.currentTimeMillis());
                  for (Transactions t : results)
                  {
                        if (Utils.getDateDiff(t.getTransdate(),d, TimeUnit.SECONDS) >= TIMEDIFF) {

                            UserCreditCard creditcard = t.getUserCreditcard();
           
                            transhelper.deposit(creditcard,t.getDebit());
                            creditcardRepository.save(creditcard);
                            
                            t.setTransstatus(Transactions.TransStatus.CANCELLED);
                            transRepository.save(t);
                        }
                  }
           }
        catch(Exception e)
        {
          e.printStackTrace();
	}
}
}
