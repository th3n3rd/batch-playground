package com.example.externalsystem.payment;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface MerchantAccounts extends JpaRepository<MerchantAccount, UUID>{}
