package com.njb.msscssm.services.actions;

import java.util.Random;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import com.njb.msscssm.domain.PaymentEvent;
import com.njb.msscssm.domain.PaymentState;
import com.njb.msscssm.services.PaymentServiceImpl;

@Component
public class AuthAction implements Action<PaymentState, PaymentEvent> {

	@Override
	public void execute(StateContext<PaymentState, PaymentEvent> context) {
		System.out.println("Auth was called!!!");

		if (new Random().nextInt(10) < 8) {
			System.out.println("Auth Approved");
			context.getStateMachine()
					.sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
							.setHeader(PaymentServiceImpl.PAYMENT_ID,
									context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID))
							.build());

		} else {
			System.out.println("Auth Declined! No Credit!!!!!!");
			context.getStateMachine()
					.sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
							.setHeader(PaymentServiceImpl.PAYMENT_ID,
									context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID))
							.build());
		}
	}
}
