package com.example.app.service;

import java.util.List;

import com.example.app.domain.Payment;

public interface PaymentService {

	List<Payment> getPaymentList() throws Exception;

	void addPayment(Payment payment) throws Exception;

	void countPayer(Payment payment) throws Exception;

}
