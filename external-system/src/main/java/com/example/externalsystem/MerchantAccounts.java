package com.example.externalsystem;

import org.springframework.data.jpa.repository.JpaRepository;

interface MerchantAccounts extends JpaRepository<MerchantAccount, String>{}
