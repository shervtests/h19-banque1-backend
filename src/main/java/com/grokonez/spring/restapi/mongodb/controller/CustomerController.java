package com.grokonez.spring.restapi.mongodb.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.grokonez.spring.restapi.mongodb.bean.ResponseBean;
import com.grokonez.spring.restapi.mongodb.model.User;
import com.grokonez.spring.restapi.mongodb.repo.UserRepository;
import com.grokonez.spring.restapi.mongodb.util.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grokonez.spring.restapi.mongodb.model.Customer;
import com.grokonez.spring.restapi.mongodb.repo.CustomerRepository;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    CustomerRepository repository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            List<Customer> customers = new ArrayList<>();
            repository.findAll().forEach(customers::add);

            return customers;
        } else {
        }
        return null;

    }
    

    @PostMapping("/customer")
    public Customer postCustomer(@RequestBody Customer customer) {

        Customer _customer = repository.save(new Customer(customer.getName(), customer.getAge()));
        return _customer;
    }

    @PostMapping("/user")
    public User addUser(@RequestBody User user) {
//        Subject subject = SecurityUtils.getSubject();
//        if (subject.isAuthenticated()) {
        if (user.getChequingSaving().equals("chequing")) {
            user.setChequing(user.getAmountAvailOfBankAccount());
        } else {
            user.setSaving(user.getAmountAvailOfBankAccount());
        }
        //every newly created user default will have view permission and user role
        user.setPermission("view");
        user.setRole("user");

        User _user = userRepository.save(user);
        return _user;
//        } else {
//            throw new UnauthorizedException();
//        }
    }


    

    @RequestMapping("/validateCustomer")
    public boolean validateCustomer(@RequestBody User user) {
        User _user = userRepository.findByUsername(user.getUsername());
        if (_user != null && _user.getPassword().equals(user.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    @RequestMapping("/getToken")
    public ResponseBean login(@RequestBody User user) {
        return new ResponseBean(200, "Login success", JWTUtil.sign(user.getUsername(), user.getPassword()));
    }

    @RequestMapping("/getUserInfo")
    public User getUserInfo(@RequestBody User user) {
        return userRepository.findByUsername(user.getUsername());
    }
    
	@GetMapping("/users/{id}")
//	public List<User> getUserInfoById() {
		public Optional<User> idUsers(@PathVariable("id") String id) {
		System.out.println("Get information by Id");

		Optional<User> users = userRepository.findById(id);
		return users;
	}

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") String id) {
        System.out.println("Delete Customer with ID = " + id + "...");

        repository.deleteById(Long.valueOf(id));

        return new ResponseEntity<>("Customer has been deleted!", HttpStatus.OK);
    }

    @GetMapping("customers/age/{age}")
    public List<Customer> findByAge(@PathVariable int age) {

        List<Customer> customers = repository.findByAge(age);
        return customers;
    }
    
    @PutMapping("/customer/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") String id, @RequestBody Customer customer) {
        System.out.println("Update Customer with ID = " + id + "...");

        Optional<Customer> customerData = repository.findById(Long.valueOf(id));

        if (customerData.isPresent()) {
            Customer _customer = customerData.get();
            _customer.setName(customer.getName());
            _customer.setAge(customer.getAge());
            _customer.setActive(customer.isActive());
            return new ResponseEntity<>(repository.save(_customer), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    
	@GetMapping("/users")
	public List<User> getAllUsers() {
		System.out.println("Get all Users...");

		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);

		return users;
	}
	
	
//
//	@PostMapping("/user")
//	public User postUser(@RequestBody User user) {
//
//		User _user = repository.save(new User(user.getFirstname(), user.getLastname(),  user.getUsername(),  user.getPassword(),  user.getQuestion1(),  user.getQuestion2(),  user.getQuestion3(),  user.getAnswer1(),  user.getAnswer2(),  user.getAnswer3(),  user.getFullAddress(),  user.getHomePhone(),  user.getMobile(),  user.getEmail(),  user.getChequing(),  user.getSaving(),  user.getAmountAvailOfBankAccount(),  user.getAmountOfMoneyAvailOfCreditAccount(),  user.getBalance()));
//		return _user;
//	}
}