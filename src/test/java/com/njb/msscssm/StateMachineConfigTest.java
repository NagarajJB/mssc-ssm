package com.njb.msscssm;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import com.njb.msscssm.domain.PaymentEvent;
import com.njb.msscssm.domain.PaymentState;

@SpringBootTest
class StateMachineConfigTest {

	@Autowired
	StateMachineFactory<PaymentState, PaymentEvent> factory;

	@Test
	void test() {

		StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());

		sm.start();

		System.out.println(sm.getState().toString());

		sm.sendEvent(PaymentEvent.PRE_AUTHORIZE);

		System.out.println(sm.getState().toString());

		sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);

		System.out.println(sm.getState().toString());

	}

}
