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
import com.grokonez.jwtauthentication.Utils.CreditCardNumberGenerator;
import com.grokonez.jwtauthentication.Utils.SearchOperation;
import com.grokonez.jwtauthentication.Utils.Utils;
import com.grokonez.jwtauthentication.message.request.CreditCardPaymentRequest;
import com.grokonez.jwtauthentication.message.request.LoginForm;
import com.grokonez.jwtauthentication.message.request.SignUpForm;
import com.grokonez.jwtauthentication.message.request.TransferRequest;
import com.grokonez.jwtauthentication.message.request.VerifyLogin1;
import com.grokonez.jwtauthentication.message.request.VerifyLogin2;
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
import java.util.Optional;
import org.hibernate.Session;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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
        	return new ResponseEntity<String>("Fail -> Need at least a number, a letter and a special character ", HttpStatus.BAD_REQUEST);
        }


        
		// Creating user's account
		User user = new User(signUpRequest.getCompany(),signUpRequest.getFirstname(),signUpRequest.getLastname(), signUpRequest.getUsername(), signUpRequest.getEmail(),
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
                userCreditCard.setAmountavailable(signUpRequest.getCreditbalanceavailable());
                userCreditCard.setAmountowned(signUpRequest.getCreditbalanceowned());
                userCreditCard.setCreditLimit(signUpRequest.getCreditLimit());
                userAccount.setAmount(signUpRequest.getAmount());
                userCreditCard.setCvv(generator.generate("", 3));
				userCreditCard.setExpiryDate(Utils.getYearTimeStampMMYY(3));
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		strRoles.forEach(role -> {
			switch (role) {
			case "admin":
				Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
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
        
        @PreAuthorize("hasRole('ADMIN')")
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

		if (!accountRepository.existsByAccountno(transferRequest.getSenderaccountno())) {
			return new ResponseEntity<String>("Fail -> Sender Account Does Not Exist!", HttpStatus.BAD_REQUEST);
		}

		if (!accountRepository.existsByAccountno(transferRequest.getReceiveraccountno())) {
			return new ResponseEntity<String>("Fail -> Receiver Account Does Not Exist!", HttpStatus.BAD_REQUEST);
		}

	 UserAccount fromaccount = accountRepository.findByAccountno(transferRequest.getSenderaccountno())
                	.orElseThrow(() ->
                        new Exception("Sender Account Does Not Exist!"));


           UserAccount toaccount = accountRepository.findByAccountno(transferRequest.getReceiveraccountno())
                	.orElseThrow(() ->
                        new Exception("Receiver Account Does Not Exist!"));

           if (fromaccount.getAmount() < transferRequest.getAmount()) {
			return new ResponseEntity<String>("Fail -> Funds not available", HttpStatus.BAD_REQUEST);
		}
           transfer(fromaccount, toaccount,transferRequest.getAmount());


           accountRepository.save(toaccount);

           accountRepository.save(fromaccount);

           Transactions toUser = new Transactions();
           toUser.setTranstype(Transactions.TransType.FROMACCOUNT);
           toUser.setDescription(Transactions.TransType.FROMACCOUNT +"("+ fromaccount.getAccountno() +")");
           toUser.setCredit(transferRequest.getAmount());
           //toUser.setDebit(0);
           toUser.setUserAccount(toaccount);
           toUser.setBalance(toaccount.getAmount());
          // transRepository.save(new Transactions(Transactions.TransType.FROMACCOUNT,Transactions.TransType.FROMACCOUNT + fromaccount.getAccountno(), transferRequest.getAmount(),0.00,toaccount.getAmount(), toaccount.getUser()));

          // transRepository.save(new Transactions(Transactions.TransType.TOACCOUNT,0.00,transferRequest.getAmount(),fromaccount.getAmount(), fromaccount.getUser()));

           Transactions fromUser = new Transactions();
           fromUser.setTranstype(Transactions.TransType.TOACCOUNT);
           fromUser.setDescription(Transactions.TransType.TOACCOUNT +"("+ toaccount.getAccountno() +")");
           fromUser.setDebit(transferRequest.getAmount());
           //toUser.setDebit(0);
           fromUser.setUserAccount(fromaccount);
           fromUser.setBalance(fromaccount.getAmount());
           transRepository.save(toUser);
           transRepository.save(fromUser);
            return ResponseEntity.ok().body("Funds successfully transfered");
	}



       @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
       @PostMapping("/CrediCardPayment")
	public ResponseEntity<String> CreditCardPayment(@Valid @RequestBody CreditCardPaymentRequest creditRequest) throws Exception {

		if (!accountRepository.existsByAccountno(creditRequest.getSenderaccountno())) {
			return new ResponseEntity<String>("Fail -> Sender Account Does Not Exist!", HttpStatus.BAD_REQUEST);
		}

		if (!creditcardRepository.existsByCreditcardno(creditRequest.getCreditcardno())) {
			return new ResponseEntity<String>("Fail -> Credit Card Does Not Exist!", HttpStatus.BAD_REQUEST);
		}

	 UserAccount fromaccount = accountRepository.findByAccountno(creditRequest.getSenderaccountno())
                	.orElseThrow(() ->
                        new Exception("Sender Account Does Not Exist!"));


         UserCreditCard creditcard = creditcardRepository.findByCreditcardno(creditRequest.getCreditcardno())
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
           creditcardtrans.setTranstype(Transactions.TransType.FROMACCOUNT);
           creditcardtrans.setDescription(Transactions.TransType.FROMACCOUNT +"("+ fromaccount.getAccountno() +")");
           creditcardtrans.setCredit(creditRequest.getAmount());
           //toUser.setDebit(0);
           creditcardtrans.setUserCreditcard(creditcard);
           creditcardtrans.setBalance(creditcard.getAmountavailable());


           Transactions fromUser = new Transactions();
           fromUser.setTranstype(Transactions.TransType.CREDITCARD);
           fromUser.setDescription(Transactions.TransType.CREDITCARD +"("+ creditcard.getCreditcardno()+")");
           fromUser.setDebit(creditRequest.getAmount()) ;
           //toUser.setDebit(0);
           fromUser.setUserAccount(fromaccount);
           fromUser.setBalance(fromaccount.getAmount());


           transRepository.save(creditcardtrans);
           transRepository.save(fromUser);



            return ResponseEntity.ok().body("$"+ creditRequest.getAmount() + " was successfully paid to CC no. " + "  " + creditcard.getCreditcardno());
	}


  private void deposit( UserAccount acct, double amount )
  {

    double balance = acct.getAmount();   // get the current balance
    balance += amount;                    // adjust the balance
    acct.setAmount( balance );           // set the new balance
  }

  private void deposit(UserCreditCard creditcard, double amount )
  {

    double creditlimit = creditcard.getAmountavailable();   // get the current creditlimit
    double creditowed = creditcard.getAmountowned();   // get the current owed
    creditlimit += amount;                    // adjust the creditlimit
    creditowed  -= amount;                    // adjust the creditlimit
    creditcard.setAmountavailable(creditlimit);// set the new creditlimit
    creditcard.setAmountowned(creditowed);// set the new current owed

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


}