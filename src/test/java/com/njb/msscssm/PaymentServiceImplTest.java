package com.njb.msscssm;

import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.njb.msscssm.domain.Payment;
import com.njb.msscssm.repository.PaymentRepository;
import com.njb.msscssm.services.PaymentService;

@SpringBootTest
class PaymentServiceImplTest {

	@Autowired
	PaymentService paymentService;

	@Autowired
	PaymentRepository paymentRepository;

	private Payment payment;

	@BeforeEach
	void setUp() {
		payment = Payment.builder().amount(new BigDecimal(120)).build();
	}

	@Transactional
	@Test
	void testPreAutorize() {

		Payment savedPayment = paymentService.newPayment(payment);
		System.out.println(savedPayment);

		paymentService.preAutorize(savedPayment.getId());

		Payment preAuthPayement = paymentRepository.getOne(savedPayment.getId());

		System.out.println(preAuthPayement);
	}

}
