package com.example.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.domain.Payment;
import com.example.app.mapper.PaymentMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentMapper paymentMapper;

	@Override
	public List<Payment> getPaymentList() throws Exception {
		return paymentMapper.selectAll();
	}

	@Override
	public void addPayment(Payment Payment) throws Exception {
		paymentMapper.insert(Payment);
	}

	@Override
	public void countPayer(Payment payment) throws Exception {
		paymentMapper.count(payment);
		
	}

}
