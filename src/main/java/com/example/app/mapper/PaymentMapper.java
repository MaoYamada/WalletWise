package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.Payment;

@Mapper
public interface PaymentMapper {

	int countAll();

	int totalPayment();

	int addCulEach();

	List<String> advanceNameList();
	
	List<Payment> memberNameList();
	
	List<Payment> paymentSum();

	List<Payment> selectAll() throws Exception;

	void insert(Payment payment) throws Exception;

	void count(Payment payment) throws Exception;
}
