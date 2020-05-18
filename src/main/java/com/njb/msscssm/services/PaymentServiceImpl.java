package com.njb.msscssm.services;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import com.njb.msscssm.domain.Payment;
import com.njb.msscssm.domain.PaymentEvent;
import com.njb.msscssm.domain.PaymentState;
import com.njb.msscssm.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	public final static String PAYMENT_ID = "payment_id";

	private final PaymentRepository paymentRepository;
	private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
	private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

	@Override
	public Payment newPayment(Payment payment) {
		payment.setState(PaymentState.NEW);
		return paymentRepository.save(payment);
	}

	@Override
	public StateMachine<PaymentState, PaymentEvent> preAutorize(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = buildStateMachine(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
		return null;
	}

	@Override
	public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = buildStateMachine(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.AUTHORIZE);
		return null;
	}

	@Override
	public StateMachine<PaymentState, PaymentEvent> declineAuthorizePayment(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = buildStateMachine(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
		return null;
	}

	private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
		// we can just send event or send data(payment id as a header) with it using
		// message
		Message<PaymentEvent> message = MessageBuilder.withPayload(event).setHeader(PAYMENT_ID, paymentId).build();
		sm.sendEvent(message);
	}

	private StateMachine<PaymentState, PaymentEvent> buildStateMachine(Long paymentId) {

		Payment payment = paymentRepository.getOne(paymentId);
		StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(paymentId));

		sm.stop();

		sm.getStateMachineAccessor().doWithAllRegions(sma -> {
			sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
			sma.resetStateMachine(
					new DefaultStateMachineContext<PaymentState, PaymentEvent>(payment.getState(), null, null, null));
		});

		sm.start();

		return sm;
	}

}
