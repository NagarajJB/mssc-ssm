package com.njb.msscssm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.njb.msscssm.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
