package com.njb.msscssm.services;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import com.njb.msscssm.domain.Payment;
import com.njb.msscssm.domain.PaymentEvent;
import com.njb.msscssm.domain.PaymentState;
import com.njb.msscssm.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

	private final PaymentRepository paymentRepository;

	@Override
	public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
			Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine,
			StateMachine<PaymentState, PaymentEvent> rootStateMachine) {

		Optional.ofNullable(message).ifPresent(msg -> {

			Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID, -1l)))
					.ifPresent(paymentId -> {

						Payment payment = paymentRepository.getOne(paymentId);
						payment.setState(state.getId());
						paymentRepository.save(payment);

					});

		});

	}

}
