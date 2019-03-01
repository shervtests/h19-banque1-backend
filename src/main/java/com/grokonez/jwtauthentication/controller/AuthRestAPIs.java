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
import com.grokonez.jwtauthentication.message.request.LoginForm;
import com.grokonez.jwtauthentication.message.request.SignUpForm;
import com.grokonez.jwtauthentication.message.request.VerifyLogin1;
import com.grokonez.jwtauthentication.message.request.VerifyLogin2;
import com.grokonez.jwtauthentication.message.response.JwtResponse;
import com.grokonez.jwtauthentication.model.Role;
import com.grokonez.jwtauthentication.model.RoleName;
import com.grokonez.jwtauthentication.model.User;
import com.grokonez.jwtauthentication.repository.RoleRepository;
import com.grokonez.jwtauthentication.repository.UserRepository;
import com.grokonez.jwtauthentication.security.jwt.JwtProvider;
import com.grokonez.jwtauthentication.specification.UserSpecificationsBuilder;

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
        	return new ResponseEntity<String>("Fail -> Need a least a 1 number, 1 letter and 1 special ", HttpStatus.BAD_REQUEST);
        }


        
		// Creating user's account
		User user = new User(signUpRequest.getFirstname(),signUpRequest.getLastname(), signUpRequest.getUsername(), signUpRequest.getEmail(),
				signUpRequest.getQuestion1(), signUpRequest.getAnswer1(), signUpRequest.getQuestion2(),
				signUpRequest.getAnswer2(), encoder.encode(signUpRequest.getPassword()));

				user.setZip(signUpRequest.getZip());
				user.setProvince(signUpRequest.getProvince());
				user.setCity(signUpRequest.getCity());
				user.setCountry(signUpRequest.getCountry());
				user.setMobile(signUpRequest.getMobile());
				user.setLandline(signUpRequest.getLandline());
                user.setAddress(signUpRequest.getAddress());
                CreditCardNumberGenerator generator = new CreditCardNumberGenerator();
                user.setAccountno(generator.generate("111", 8));
                user.setCreditcardno(generator.generate("11111", 16));
                user.setCreditbalanceavailable(signUpRequest.getCreditbalanceavailable());
                user.setCreditbalanceowned(signUpRequest.getCreditbalanceowned());
                user.setAmount(signUpRequest.getAmount());
                user.setCvv(generator.generate("", 3));
				user.setExpirydate(Utils.getYearTimeStampMMYY(3));
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		strRoles.forEach(role -> {
			switch (role) {
			case "admin":
				Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(adminRole);

				break;
			case "pm":
				Role pmRole = roleRepository.findByName(RoleName.ROLE_PM)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(pmRole);

				break;
			default:
				Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(userRole);
			}
		});

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok().body("User registered successfully!");
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
}