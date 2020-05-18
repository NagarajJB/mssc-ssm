package com.njb.msscssm;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import com.njb.msscssm.domain.Payment;
import com.njb.msscssm.domain.PaymentEvent;
import com.njb.msscssm.domain.PaymentState;
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
	//@Test
	void testPreAutorize() {

		Payment savedPayment = paymentService.newPayment(payment);
		System.out.println("Should be NEW!");
		System.out.println(savedPayment);

		paymentService.preAutorize(savedPayment.getId());

		Payment preAuthPayement = paymentRepository.getOne(savedPayment.getId());

		System.out.println("Should be PRE_AUTH or PRE_AUTH_DECLINED!");
		System.out.println(preAuthPayement);

	}

	@RepeatedTest(10)
	@Transactional
	void testFullAutorize() {

		Payment savedPayment = paymentService.newPayment(payment);

		StateMachine<PaymentState, PaymentEvent> preAuthSm = paymentService.preAutorize(savedPayment.getId());

		if (preAuthSm.getState().getId() == PaymentState.PRE_AUTH) {
			System.out.println("Payment is pre-authorised!");
			StateMachine<PaymentState, PaymentEvent> authSM = paymentService.authorizePayment(savedPayment.getId());
			System.out.println("result of Authorization: " + authSM.getState().getId());

		} else {
			System.out.println("Pre-auth failed!");
		}

	}

}
