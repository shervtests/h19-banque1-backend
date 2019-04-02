/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.repository;

import com.grokonez.jwtauthentication.model.Transactions;
import com.grokonez.jwtauthentication.model.UserCreditCard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

/**
 *
 * @author smile
 */

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
   
    Optional<Transactions> findById(long transId);
   
     @Query(value="select * from transactions t where t.id =:Id and t.transstatus=:status", nativeQuery=true)
     Optional<Transactions> findTransaction(@Param("Id") long Id, @Param("status") String status );
 
     @Query(value="select * from transactions t where t.transstatus=:status", nativeQuery=true)
     List<Transactions> findTransactionsbyStatus(@Param("status") String status );
}