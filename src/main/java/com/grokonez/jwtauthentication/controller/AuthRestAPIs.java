package com.grokonez.jwtauthentication.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.grokonez.jwtauthentication.Utils.CreditCardNumberGenerator;
import com.grokonez.jwtauthentication.Utils.SearchOperation;
import com.grokonez.jwtauthentication.Utils.Utils;
import com.grokonez.jwtauthentication.message.request.CreditCardPaymentRequest;
import com.grokonez.jwtauthentication.message.request.ForgetPasswordRequest;
import com.grokonez.jwtauthentication.message.request.LoginForm;
import com.grokonez.jwtauthentication.message.request.OtherBankTransferRequest;
import com.grokonez.jwtauthentication.message.request.SignUpForm;
import com.grokonez.jwtauthentication.message.request.TransferRequest;
import com.grokonez.jwtauthentication.message.request.VerifyLogin1;
import com.grokonez.jwtauthentication.message.request.VerifyLogin2;
import com.grokonez.jwtauthentication.message.response.InfoResponse;
import com.grokonez.jwtauthentication.message.response.JwtResponse;
import com.grokonez.jwtauthentication.model.Role;
import com.grokonez.jwtauthentication.model.RoleName;
import com.grokonez.jwtauthentication.model.Transactions;
import com.grokonez.jwtauthentication.model.User;
import com.grokonez.jwtauthentication.model.UserAccount;
import com.grokonez.jwtauthentication.model.UserCreditCard;
import com.grokonez.jwtauthentication.repository.RoleRepository;
import com.grokonez.jwtauthentication.repository.UserAccountRepository;
import com.grokonez.jwtauthentication.repository.UserRepository;
import com.grokonez.jwtauthentication.repository.TransactionsRepository;
import com.grokonez.jwtauthentication.repository.UserCreditCardRepository;
import com.grokonez.jwtauthentication.security.jwt.JwtProvider;
import com.grokonez.jwtauthentication.specification.UserSpecificationsBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    TransactionsRepository transRepository;

    @Autowired
    UserAccountRepository accountRepository;

    @Autowired
    UserCreditCardRepository creditcardRepository;

    @Value("${grokonez.app.apikey}")
    private String apiKey;
    
    
     @Value("${grokonez.app.otherbankapi}")
    private  String uri;

    @Value("${grokonez.app.otherbankapikey}")
    private String key ;
    //Use to signin

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    //Use to verify second verify
    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/verify2")
    public ResponseEntity<?> authenticateVerify(@Valid @RequestBody VerifyLogin2 verifyLogin) {

        if (userRepository.existsByQuestion2((verifyLogin.getQuestion2()))
                && userRepository.existsByAnswer2((verifyLogin.getAnswer2()))) {
            return ResponseEntity.ok().body("User verify successfully!");
        } else {
            return new ResponseEntity<String>("Fail -> Username is already taken!", HttpStatus.BAD_REQUEST);
        }

    }

    //Use to verify first verify

    @PostMapping("/verify1")
    public ResponseEntity<?> authenticateVerify(@Valid @RequestBody VerifyLogin1 verifyLogin) {

        if (userRepository.existsByQuestion1((verifyLogin.getQuestion1()))
                && userRepository.existsByAnswer1((verifyLogin.getAnswer1()))) {
            return ResponseEntity.ok().body("User verify successfully!");
        } else {
            return new ResponseEntity<String>("Fail -> Username is already taken!", HttpStatus.BAD_REQUEST);
        }

    }
    
     @GetMapping("/getQuestions/{username}")
    public ResponseEntity<?> getQuestionsforgetPassword(@PathVariable("username") String username) {

        
          User user = userRepository.findByUsername(username).orElse(null);
          
          if (user == null) {
            return new ResponseEntity<String>("Username not found", HttpStatus.BAD_REQUEST);
           }
          
          Map<String, String> questions = new HashMap<>();
         
           questions.put("Question1",user.getQuestion1());
           questions.put("Question2",user.getQuestion2());
            return  ResponseEntity.ok(questions);
       
    }
   
    @PostMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
          String response = null ;
        Map<String, String> responses = new HashMap<>();
         responses.put("info",response);
          User user = userRepository.findByUsername(request.getUsername()).orElse(null);
          
          if (user == null) {
            return new ResponseEntity<Object>(new InfoResponse("Username not found"), HttpStatus.BAD_REQUEST);
           }
          if (!(userRepository.existsByQuestion1((request.getQuestion1()))
                && userRepository.existsByAnswer1((request.getAnswer1())))) 
            return new ResponseEntity<Object>(new InfoResponse("Incorrect answer1"), HttpStatus.BAD_REQUEST);
       
         if (!(userRepository.existsByQuestion2((request.getQuestion2()))
                && userRepository.existsByAnswer2((request.getAnswer2())))) 
            return new ResponseEntity<Object>(new InfoResponse("Incorrect answer2"), HttpStatus.BAD_REQUEST);
       
          
         //Use to verify is the password fil all requierement
        int numOfSpecial = 0;
        int numOfLetters = 0;
        int numOfDigits = 0;
        int totalCharacter = 0;

        byte[] bytes = request.getPassword().getBytes();
        for (byte tempByte : bytes) {
            if (tempByte >= 33 && tempByte <= 47) {
                numOfSpecial++;
                totalCharacter++;
            }

            char tempChar = (char) tempByte;
            if (Character.isDigit(tempChar)) {
                numOfDigits++;
                totalCharacter++;
            }

            if (Character.isLetter(tempChar)) {
                numOfLetters++;
                totalCharacter++;
            }

        }

        if (totalCharacter < 8) {
            return new ResponseEntity<Object>(new InfoResponse("Fail -> Not enough character"), HttpStatus.BAD_REQUEST);
        }

        if (numOfSpecial < 1 || numOfLetters < 1 || numOfDigits < 1) {
            System.out.println("numOfSpecial = " + numOfSpecial);
            System.out.println("numOfLetters = " + numOfLetters);
            System.out.println("numOfDigits = " + numOfDigits);
            return new ResponseEntity<Object>(new InfoResponse("Fail -> Need at least 1 number, 1 letter and 1 special character"), HttpStatus.BAD_REQUEST);
        }
            
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
         
        return ResponseEntity.ok().body(new InfoResponse("Password successfully Changed!") );
        
    }
    // use to create user
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Fail -> Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Fail -> Email is already in use!", HttpStatus.BAD_REQUEST);
        }

        //Use to verify is the password fil all requierement
        int numOfSpecial = 0;
        int numOfLetters = 0;
        int numOfDigits = 0;
        int totalCharacter = 0;

        byte[] bytes = signUpRequest.getPassword().getBytes();
        for (byte tempByte : bytes) {
            if (tempByte >= 33 && tempByte <= 47) {
                numOfSpecial++;
                totalCharacter++;
            }

            char tempChar = (char) tempByte;
            if (Character.isDigit(tempChar)) {
                numOfDigits++;
                totalCharacter++;
            }

            if (Character.isLetter(tempChar)) {
                numOfLetters++;
                totalCharacter++;
            }

        }

        if (totalCharacter < 8) {
            return new ResponseEntity<String>("Fail -> Not enough character", HttpStatus.BAD_REQUEST);
        }

        if (numOfSpecial < 1 || numOfLetters < 1 || numOfDigits < 1) {
            System.out.println("numOfSpecial = " + numOfSpecial);
            System.out.println("numOfLetters = " + numOfLetters);
            System.out.println("numOfDigits = " + numOfDigits);
            return new ResponseEntity<String>("Fail -> Need at least 1 number, 1 letter and 1 special character", HttpStatus.BAD_REQUEST);
        }



        // Creating user's account
        User user = new User(signUpRequest.getCompany(), signUpRequest.getFirstname(),signUpRequest.getLastname(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                signUpRequest.getQuestion1(), signUpRequest.getAnswer1(), signUpRequest.getQuestion2(),
                signUpRequest.getAnswer2(), encoder.encode(signUpRequest.getPassword()));
        UserCreditCard userCreditCard = new UserCreditCard();
        UserAccount userAccount = new UserAccount();
        user.setZip(signUpRequest.getZip());
        user.setProvince(signUpRequest.getProvince());
        user.setCity(signUpRequest.getCity());
        user.setCountry(signUpRequest.getCountry());
        user.setMobile(signUpRequest.getMobile());
        user.setLandline(signUpRequest.getLandline());
        user.setAddress(signUpRequest.getAddress());
        CreditCardNumberGenerator generator = new CreditCardNumberGenerator();
        userAccount.setAccountno(generator.generate("111", 8));
        userCreditCard.setCreditcardno(generator.generate("11111", 16));
        userCreditCard.setCreditLimit(signUpRequest.getCreditLimit());
        userCreditCard.setAmountavailable(signUpRequest.getCreditbalanceavailable());
        userCreditCard.setAmountowned(signUpRequest.getCreditbalanceowned());
        userAccount.setAmount(signUpRequest.getAmount());
        userCreditCard.setCvv(generator.generate("", 3));
        userCreditCard.setExpiryDate(Utils.getYearTimeStampMMYY(3));
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found"));
                    roles.add(adminRole);

                    break;
                case "pm":
                    Role pmRole = roleRepository.findByName(RoleName.ROLE_PM)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
                    roles.add(pmRole);

                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
                    roles.add(userRole);
            }
        });

        user.setRoles(roles);
        user.setUserCreditCard(userCreditCard);
        user.setUserAccount(userAccount);

        userCreditCard.setUser(user);
        userAccount.setUser(user);

        userRepository.save(user);

        return ResponseEntity.ok().body("User registered successfully!" );
    }

    @RequestMapping(method = RequestMethod.GET, value = "/searchusers")
    @ResponseBody
    public List<User> search(@RequestParam(value = "search") String search) {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        String operationSetExper = Joiner.on("|")
                .join(SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
        }

        Specification<User> spec = builder.build();
        return userRepository.findAll(spec);

    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/Transfer")
    public ResponseEntity<String> TransferFunds(@Valid @RequestBody TransferRequest transferRequest) throws Exception {

        if (!accountRepository.existsByAccountno(transferRequest.getSenderAccountNo())) {
            return new ResponseEntity<String>("Fail -> Sender Account Does Not Exist!", HttpStatus.BAD_REQUEST);
        }

        if (!accountRepository.existsByAccountno(transferRequest.getReceiverAccountNo())) {
            return new ResponseEntity<String>("Fail -> Receiver Account Does Not Exist!", HttpStatus.BAD_REQUEST);
        }

        UserAccount fromaccount = accountRepository.findByAccountno(transferRequest.getSenderAccountNo())
                .orElseThrow(() ->
                        new Exception("Sender Account Does Not Exist!"));


        UserAccount toaccount = accountRepository.findByAccountno(transferRequest.getReceiverAccountNo())
                .orElseThrow(() ->
                        new Exception("Receiver Account Does Not Exist!"));

        if (fromaccount.getAmount() < transferRequest.getAmount()) {
            return new ResponseEntity<String>("Fail -> Funds not available", HttpStatus.BAD_REQUEST);
        }
        transfer(fromaccount, toaccount,transferRequest.getAmount());


        accountRepository.save(toaccount);

        accountRepository.save(fromaccount);

        Transactions toUser = new Transactions();
        toUser.setTranstype(Transactions.TransType.VIREMENT_DE);
        toUser.setDescription(Transactions.TransType.VIREMENT_DE +"("+ fromaccount.getAccountno() +")");
        toUser.setCredit(transferRequest.getAmount());
        //toUser.setDebit(0);
        toUser.setUserAccount(toaccount);
        toUser.setCurrently_available_funds(toaccount.getAmount());
        // transRepository.save(new Transactions(Transactions.TransType.FROMACCOUNT,Transactions.TransType.FROMACCOUNT + fromaccount.getAccountno(), transferRequest.getAmount(),0.00,toaccount.getAmount(), toaccount.getUser()));

        // transRepository.save(new Transactions(Transactions.TransType.TOACCOUNT,0.00,transferRequest.getAmount(),fromaccount.getAmount(), fromaccount.getUser()));

        Transactions fromUser = new Transactions();
        fromUser.setTranstype(Transactions.TransType.VIREMENT_A);
        fromUser.setDescription(Transactions.TransType.VIREMENT_A +"("+ toaccount.getAccountno() +")");
        fromUser.setDebit(transferRequest.getAmount());
        //toUser.setDebit(0);
        fromUser.setUserAccount(fromaccount);
        fromUser.setCurrently_available_funds(fromaccount.getAmount());
        transRepository.save(toUser);
        transRepository.save(fromUser);
        return ResponseEntity.ok().body("Funds successfully transfered");
    }




    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/CrediCardPayment")
    public ResponseEntity<String> CreditCardPayment(@Valid @RequestBody CreditCardPaymentRequest creditRequest) throws Exception {

        if (!accountRepository.existsByAccountno(creditRequest.getSenderAccountNo())) {
            return new ResponseEntity<String>("Fail -> Sender Account Does Not Exist!", HttpStatus.BAD_REQUEST);
        }

        if (!creditcardRepository.existsByCreditcardno(creditRequest.getCreditCardNo())) {
            return new ResponseEntity<String>("Fail -> Credit Card Does Not Exist!", HttpStatus.BAD_REQUEST);
        }

        UserAccount fromaccount = accountRepository.findByAccountno(creditRequest.getSenderAccountNo())
                .orElseThrow(() ->
                        new Exception("Sender Account Does Not Exist!"));


        UserCreditCard creditcard = creditcardRepository.findByCreditcardno(creditRequest.getCreditCardNo())
                .orElseThrow(() ->
                        new Exception("Credit Card Does Not Exist!"));



        if (fromaccount.getAmount() < creditRequest.getAmount()) {
            return new ResponseEntity<String>("Fail -> Funds not available", HttpStatus.BAD_REQUEST);
        }
        creditcardpayment(fromaccount, creditcard,creditRequest.getAmount());


        creditcardRepository.save(creditcard);

        accountRepository.save(fromaccount);

        // transRepository.save(new Transactions(Transactions.TransType.FROMACCOUNT, creditRequest.getAmount(),0.00,creditcard.getAmountavailable(), creditcard.getUser()));

        //  transRepository.save(new Transactions(Transactions.TransType.CREDITCARD,0.00,creditRequest.getAmount(),fromaccount.getAmount(), fromaccount.getUser()));

        Transactions creditcardtrans = new Transactions();
        creditcardtrans.setTranstype(Transactions.TransType.VIREMENT_DE);
        creditcardtrans.setDescription(Transactions.TransType.VIREMENT_DE +"("+ fromaccount.getAccountno() +")");
        creditcardtrans.setCredit(creditRequest.getAmount());
        //toUser.setDebit(0);
        creditcardtrans.setUserCreditcard(creditcard);
        creditcardtrans.setCurrently_available_funds(creditcard.getAmountavailable());


        Transactions fromUser = new Transactions();
        fromUser.setTranstype(Transactions.TransType.CARTE_DE_CREDIT);
        fromUser.setDescription(Transactions.TransType.CARTE_DE_CREDIT +"("+ creditcard.getCreditcardno()+")");
        fromUser.setDebit(creditRequest.getAmount()) ;
        //toUser.setDebit(0);
        fromUser.setUserAccount(fromaccount);
        fromUser.setCurrently_available_funds(fromaccount.getAmount());


        transRepository.save(creditcardtrans);
        transRepository.save(fromUser);



        return ResponseEntity.ok().body(creditRequest.getAmount() + "$" + " to Credit Card No. " + creditcard.getCreditcardno()+" was successfully paid.");
    }


    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/BankTransfer")
    public ResponseEntity<String> TransferBankFunds(@RequestHeader(value="apikey") String apiKey,  @Valid @RequestBody TransferRequest transferRequest) throws Exception {

        if (!this.apiKey.equalsIgnoreCase(apiKey)) {
            return new ResponseEntity<String>("Fail -> Invalid Access Key!", HttpStatus.UNAUTHORIZED);
        }

        if (transferRequest.getAmount()<= 0 ) {
            return new ResponseEntity<String>("Fail -> Please Provide A Positive Amount", HttpStatus.BAD_REQUEST);
}

        if (transferRequest.getSenderAccountNo() == null || transferRequest.getSenderAccountNo().equalsIgnoreCase("")) {
            return new ResponseEntity<String>("Fail -> Please Provide A Sender Account!", HttpStatus.BAD_REQUEST);
        }


        if (!accountRepository.existsByAccountno(transferRequest.getReceiverAccountNo())) {
            return new ResponseEntity<String>("Fail -> Receiver Account Does Not Exist!", HttpStatus.NOT_FOUND);
        }

        




        UserAccount toaccount = accountRepository.findByAccountno(transferRequest.getReceiverAccountNo())
                .orElseThrow(() ->
                        new Exception("Receiver Account Does Not Exist!"));

        // deposit
        deposit(toaccount,transferRequest.getAmount());


        accountRepository.save(toaccount);



        Transactions toUser = new Transactions();
        toUser.setTranstype(Transactions.TransType.VIREMENT_DE_DESPEPINIERES);
        toUser.setDescription(Transactions.TransType.VIREMENT_DE_DESPEPINIERES +"("+ transferRequest.getSenderAccountNo()+")");
        toUser.setCredit(transferRequest.getAmount());
        //toUser.setDebit(0);
        toUser.setUserAccount(toaccount);
        toUser.setCurrently_available_funds(toaccount.getAmount());


        transRepository.save(toUser);

        return  ResponseEntity.ok().body("Funds successfully transfered");
    }

  // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/OtherBankTransfer")
    public ResponseEntity<String> OtherBankTransferBankFunds( @Valid @RequestBody TransferRequest transferRequest) throws Exception {

       
        if (transferRequest.getAmount()<= 0 ) {
            return new ResponseEntity<String>("Fail -> Please Provide A Positive Amount", HttpStatus.BAD_REQUEST);
}

        if (transferRequest.getReceiverAccountNo()== null || transferRequest.getReceiverAccountNo().equalsIgnoreCase("")) {
            return new ResponseEntity<String>("Fail -> Please Provide A Receiver Account!", HttpStatus.BAD_REQUEST);
        }


        if (!accountRepository.existsByAccountno(transferRequest.getSenderAccountNo())) {
            return new ResponseEntity<String>("Fail -> Sender Account Does Not Exist!", HttpStatus.NOT_FOUND);
        }
        
        UserAccount fromaccount = accountRepository.findByAccountno(transferRequest.getSenderAccountNo())
                .orElseThrow(() ->
                        new Exception("Sender Account Does Not Exist!"));

        
         if (fromaccount.getAmount() < transferRequest.getAmount()) {
            return new ResponseEntity<String>("Fail -> Funds not available", HttpStatus.BAD_REQUEST);
        }

        OtherBankTransferRequest  otherBank = new OtherBankTransferRequest();
        otherBank.setAmount(transferRequest.getAmount());
        otherBank.setSourceAccountNumber(transferRequest.getSenderAccountNo());
        otherBank.setTargetAccountNumber(transferRequest.getReceiverAccountNo());
        
        // api call 
        String Otherbankresponse = PostMethod(otherBank);
        
        if (!Otherbankresponse.contains("OTHER BANK API SUCCESS"))
              return new ResponseEntity<String>(Otherbankresponse, HttpStatus.BAD_REQUEST);

        
        
        // deposit
        deposit(fromaccount,-transferRequest.getAmount());


        accountRepository.save(fromaccount);



        Transactions fromUser = new Transactions();
        fromUser.setTranstype(Transactions.TransType.VIREMENT_A_DESPEPINIERES);
        fromUser.setDescription(Transactions.TransType.VIREMENT_A_DESPEPINIERES +"("+ transferRequest.getReceiverAccountNo()+")");
        fromUser.setDebit(transferRequest.getAmount());
        //toUser.setDebit(0);
        fromUser.setUserAccount(fromaccount);
        fromUser.setCurrently_available_funds(fromaccount.getAmount());


        transRepository.save(fromUser);

        return  ResponseEntity.ok().body(Otherbankresponse);
    }



    // Helper Functions
    private void deposit( UserAccount acct, double amount )
    {

        double balance = acct.getAmount();   // get the current balance
        balance += amount;                    // adjust the balance
        acct.setAmount(Double.parseDouble(String.format("%.2f", balance )));           // set the new balance
    }

    private void deposit(UserCreditCard creditcard, double amount )
    {

        double creditlimit = creditcard.getAmountavailable();   // get the current creditlimit
        double creditowed = creditcard.getAmountowned();   // get the current owed
        creditlimit += amount;                    // adjust the creditlimit
        creditowed  -= amount;                    // adjust the creditlimit
        creditcard.setAmountavailable(Double.parseDouble(String.format("%.2f", creditlimit)));// set the new creditlimit
        creditcard.setAmountowned(Double.parseDouble(String.format("%.2f",creditowed)));// set the new current owed

    }

    private void transfer( UserAccount fromAcct, UserAccount toAcct, double amount )
    {
        deposit( fromAcct, -amount );   // take money from one account
        deposit( toAcct, amount );      // add the money to the other account
    }

    private void creditcardpayment( UserAccount fromAcct, UserCreditCard creditcard, double amount )
    {
        deposit( fromAcct, -amount );   // take money from one account
        deposit( creditcard, amount );      // add the money to the other account
    }


    // Other Bank Post Method
   public  String PostMethod(Object input) throws IOException  {
     
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