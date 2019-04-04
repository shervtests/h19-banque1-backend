/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.controller;

import antlr.StringUtils;
import com.grokonez.jwtauthentication.Utils.TransactionsHelper;
import com.grokonez.jwtauthentication.message.request.OtherBankTransferRequest;
import com.grokonez.jwtauthentication.message.request.PaymentGatewayRequest;
import com.grokonez.jwtauthentication.message.request.ProcessRequest;
import com.grokonez.jwtauthentication.message.request.TransferRequest;
import com.grokonez.jwtauthentication.message.request.VerifyLogin1;
import com.grokonez.jwtauthentication.message.response.Response;
import com.grokonez.jwtauthentication.model.Transactions;
import com.grokonez.jwtauthentication.model.Transactions.TransStatus;
import com.grokonez.jwtauthentication.model.UserAccount;
import com.grokonez.jwtauthentication.model.UserCreditCard;
import com.grokonez.jwtauthentication.repository.TransactionsRepository;
import com.grokonez.jwtauthentication.repository.UserAccountRepository;
import com.grokonez.jwtauthentication.repository.UserCreditCardRepository;
import com.grokonez.jwtauthentication.repository.UserRepository;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author smile
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/paymentgateway")
public class PaymentGatewayAPIs {
   
      
    @Autowired
    TransactionsRepository transRepository;

    @Autowired
    UserAccountRepository accountRepository;

    @Autowired
    UserCreditCardRepository creditcardRepository;
    
    @Autowired
    UserRepository userRepository;
    @Autowired
    TransactionsHelper transhelper ;
      @Value("${grokonez.app.apikeygateway}")
    private String apiKey;
    
      
   
      
    @PostMapping("/preauth")
    public ResponseEntity<?> PreAuth(@RequestHeader(value="apikey") String apiKey,  @Valid @RequestBody PaymentGatewayRequest paymentRequest) throws Exception {
        
        if (!this.apiKey.equalsIgnoreCase(apiKey)) {
            return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.UNAUTHORIZED);
        }

        if (paymentRequest.getAmount()<= 0 ) {
            return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);
}

        if (paymentRequest.getCcNumber()== null || paymentRequest.getCcNumber().equalsIgnoreCase("")) {
            return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);
        }


//            UserCreditCard creditcard = new UserCreditCard();                         
//            creditcard.setCreditcardno(paymentRequest.getCcnumber()); 
//            //creditcard.setCvv(paymentRequest.getCvv()); 
//           // creditcard.setExpiryDate(paymentRequest.getMonth()+ "/" + paymentRequest.getYear());  
//            
//            Example<UserCreditCard> example = Example.of(creditcard);
//            List<UserCreditCard> results = creditcardRepository.findAll(example);

             //String expirydate = paymentRequest.getMonth()+ "/" + paymentRequest.getYear();  
//            
            List<UserCreditCard> results = creditcardRepository.findCreditcard(paymentRequest.getCcNumber(),paymentRequest.getCvv(),paymentRequest.getExpiryDate());
       
         if (results.isEmpty()) {
             return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);
        }
             
            UserCreditCard creditcard = null;
             
             creditcard = results.get(0);
          
          if (creditcard.getAmountavailable() < paymentRequest.getAmount()) {
              return new ResponseEntity<Object>(new Response("","DECLINED-INSUFFICIANT-FUNDS"), HttpStatus.BAD_REQUEST);
        }

        if (!creditcard.getUser().getFirstname().equalsIgnoreCase(paymentRequest.getFirstName())|| !creditcard.getUser().getLastname().equalsIgnoreCase(paymentRequest.getLastName())) {
             return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);
        }  
          
          if (paymentRequest.getMerchantAccountNo().startsWith("111")|| paymentRequest.getMerchantAccountNo().startsWith("222"))
           {
               if (paymentRequest.getMerchantAccountNo().startsWith("111"))
               {
                    if (!accountRepository.existsByAccountno(paymentRequest.getMerchantAccountNo())) {
                        return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);
                    }
               }
           }
           else 
           {
               return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);
           }  
          
        // withdrawal
        transhelper.deposit(creditcard,-paymentRequest.getAmount());

        creditcardRepository.save(creditcard);



        Transactions transcreditcard = new Transactions();
        transcreditcard.setTranstype(Transactions.TransType.CARTE_DE_CREDIT);
        transcreditcard.setTransstatus(Transactions.TransStatus.CREATED);
        transcreditcard.setDescription(paymentRequest.getTransactionDesc() +"_"+ "PAYÉ_AU_COMPTE_DU_MARCHAND("+ paymentRequest.getMerchantAccountNo()+")");
        transcreditcard.setDebit(paymentRequest.getAmount());
        //toUser.setDebit(0);
        transcreditcard.setUserCreditcard(creditcard);
        transcreditcard.setCurrently_available_funds(creditcard.getAmountavailable());
        transRepository.save(transcreditcard);
         
        return  ResponseEntity.ok(new Response(transcreditcard.getId().toString(),"ACCEPTED"));
    }
    
    
     @PostMapping("/process")
    public ResponseEntity<?> Process(@RequestHeader(value="apikey") String apiKey,  @Valid @RequestBody ProcessRequest processrequest ) throws Exception {

        if (!this.apiKey.equalsIgnoreCase(apiKey)) {
            return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.UNAUTHORIZED);
        }
                
           
            Transactions transaction = transRepository.findTransaction(Long.parseLong(processrequest.getTransactionId()),Transactions.TransStatus.CREATED.toString()).orElse(null);
            if (transaction == null)
                return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.NOT_FOUND);
        
        
       

       // CANCELLED
       if (processrequest.getAction().toUpperCase().equals("CANCEL"))
       {
           UserCreditCard creditcard = transaction.getUserCreditcard();
           
           transhelper.deposit(creditcard,transaction.getDebit());
           creditcardRepository.save(creditcard);
           
           
            transaction.setTransstatus(TransStatus.CANCELLED);
                transRepository.save(transaction);  
           return  ResponseEntity.ok(new Response(transaction.getId().toString(),"CANCELLED"));
           
       }
       
       //COMMITTED
       else if (processrequest.getAction().toUpperCase().equals("COMMIT"))
       {
          String MercantAccountNo = transaction.getDescription() ;        
           MercantAccountNo =  MercantAccountNo.substring(MercantAccountNo.indexOf("(") + 1, MercantAccountNo.indexOf(")"));
           
           //Internal  Bank 
           if (MercantAccountNo.startsWith("111"))
           {
                UserAccount toaccount = accountRepository.findByAccountno(MercantAccountNo)
                .orElseThrow(() ->
                        new Exception("Receiver Account Does Not Exist!"));

                 // deposit
                transhelper.deposit(toaccount,transaction.getDebit());


                accountRepository.save(toaccount);

                
                Transactions toUser = new Transactions();
                toUser.setTranstype(Transactions.TransType.PAIEMENT_DE_CLIENT);
                toUser.setDescription(Transactions.TransType.PAIEMENT_DE_CLIENT +"("+ transaction.getUserCreditcard().getCreditcardno()+")");
                toUser.setCredit(transaction.getDebit());
                //toUser.setDebit(0);
                toUser.setUserAccount(toaccount);
                toUser.setCurrently_available_funds(toaccount.getAmount());
                transRepository.save(toUser);
               
              // Commited Transaction 
               
                transaction.setTransstatus(TransStatus.COMMITTED);
                transRepository.save(transaction);  
                        
           }
           // Other Bank 
           else if (MercantAccountNo.startsWith("222"))
           {
                    OtherBankTransferRequest  otherBank = new OtherBankTransferRequest();
                    otherBank.setAmount(transaction.getDebit());
                    otherBank.setSourceAccountNumber(transaction.getUserCreditcard().getCreditcardno());
                    otherBank.setTargetAccountNumber(MercantAccountNo);

                    // api call 
                    String Otherbankresponse = transhelper.OtherBankTransfer(otherBank);

                    if (!Otherbankresponse.contains("OTHER BANK API SUCCESS")) {


                        UserCreditCard creditcard = transaction.getUserCreditcard();
                        transhelper.deposit(creditcard,transaction.getDebit());
                        creditcardRepository.save(creditcard);
                        transaction.setTransstatus(TransStatus.CANCELLED);
                        transRepository.save(transaction);



                        return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);

                    }
                  // Commited Transaction 
               
                transaction.setTransstatus(TransStatus.COMMITTED);
                transRepository.save(transaction);  
                        

           }
           else 
           {
               return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);
           }    
       }
       else 
       {
           return new ResponseEntity<Object>(new Response("","DECLINED"), HttpStatus.BAD_REQUEST);
       }
       
         
        return  ResponseEntity.ok(new Response(transaction.getId().toString(),"COMMITTED"));

    }


}
