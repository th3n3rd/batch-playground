package com.example.externalsystem.payment;

import org.springframework.data.jpa.repository.JpaRepository;

interface MerchantAccounts extends JpaRepository<MerchantAccount, String>{}
