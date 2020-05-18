package com.njb.msscssm.services;

import org.springframework.statemachine.StateMachine;

import com.njb.msscssm.domain.Payment;
import com.njb.msscssm.domain.PaymentEvent;
import com.njb.msscssm.domain.PaymentState;

public interface PaymentService {

	public Payment newPayment(Payment payment);

	public StateMachine<PaymentState, PaymentEvent> preAutorize(Long paymentId);

	public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);

	public StateMachine<PaymentState, PaymentEvent> declineAuthorizePayment(Long paymentId);
}
