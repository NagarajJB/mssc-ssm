package com.njb.msscssm.config;

import java.util.EnumSet;
import java.util.Random;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.njb.msscssm.domain.PaymentEvent;
import com.njb.msscssm.domain.PaymentState;
import com.njb.msscssm.services.PaymentServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

	@Override
	public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {

		states.withStates().initial(PaymentState.NEW).states(EnumSet.allOf(PaymentState.class)).end(PaymentState.AUTH)
				.end(PaymentState.PRE_AUTH_ERROR).end(PaymentState.AUTH_ERROR);

	}

	@Override
	public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {

		transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE).action(preAuthorizeAction())
		.and()
		.withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
		.and()
		.withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
		.and()
		.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE).action(authorizeAction())
		.and()
		.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)
		.and()
		.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED);

	}

	@Override
	public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {

		StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<PaymentState, PaymentEvent>() {
			@Override
			public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
				log.info(String.format("State changed from: %s to %s", from, to));
			}
		};

		config.withConfiguration().listener(adapter);

	}
	
	//the actions can be complex like jms, http etc
	
	private Action<PaymentState, PaymentEvent> authorizeAction() {
		return stateContext -> {

			System.out.println("Authorize was called!");

			if (new Random().nextInt(10) < 8) {
				System.out.println("Authorize Approved!");
				stateContext.getStateMachine()
						.sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
								.setHeader(PaymentServiceImpl.PAYMENT_ID,
										stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID))
								.build());
			} else {
				System.out.println("Authorize Declined..Credit Limit!!!");
				stateContext.getStateMachine()
						.sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
								.setHeader(PaymentServiceImpl.PAYMENT_ID,
										stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID))
								.build());
			}

		};
	}
	

	private Action<PaymentState, PaymentEvent> preAuthorizeAction() {

		return stateContext -> {

			System.out.println("Pre-auth was called!");

			if (new Random().nextInt(10) < 8) {
				System.out.println("Approved!");
				stateContext.getStateMachine()
						.sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
								.setHeader(PaymentServiceImpl.PAYMENT_ID,
										stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID))
								.build());
			} else {
				System.out.println("Declined..Credit Limit!!!");
				stateContext.getStateMachine()
						.sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
								.setHeader(PaymentServiceImpl.PAYMENT_ID,
										stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID))
								.build());
			}

		};

	}
}
