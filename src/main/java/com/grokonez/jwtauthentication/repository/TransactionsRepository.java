/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.repository;

import com.grokonez.jwtauthentication.model.Transactions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author smile
 */

@Repository
public interface TransactionsRepository extends CrudRepository<Transactions, Long> {
    
}