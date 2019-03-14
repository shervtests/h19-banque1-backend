/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.repository;

import com.grokonez.jwtauthentication.model.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {
    
     Optional<UserAccount> findByAccountno(String accountno);
     Boolean existsByAccountno(String AccountNo);
}