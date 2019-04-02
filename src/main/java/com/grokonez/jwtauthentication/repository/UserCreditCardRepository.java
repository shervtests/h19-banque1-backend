/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.repository;

import com.grokonez.jwtauthentication.model.Role;
import com.grokonez.jwtauthentication.model.RoleName;
import com.grokonez.jwtauthentication.model.UserCreditCard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author smile
 */

@Repository
public interface UserCreditCardRepository extends JpaRepository<UserCreditCard, Long> , QueryByExampleExecutor<UserCreditCard> {
     Optional<UserCreditCard> findByCreditcardno(String creditcardno);
     Boolean existsByCreditcardno(String creditcardno);
     
     
    @Query(value="select * from user_credit_card u where u.creditcardno =:ccnumber and u.cvv=:cvv and u.expiry_date =:expirydate", nativeQuery=true)
    List<UserCreditCard> findCreditcard(@Param("ccnumber") String ccnumber, @Param("cvv") String cvv, @Param("expirydate") String expirydate  );
}