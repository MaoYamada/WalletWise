package com.example.app.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
//getter/setterコードの省略
public class Payment {

	private Integer id;
	
	@NotBlank(message = "fill in this blank")
	private String NameId;
	
//	@NotBlank(message = "fill in this blank")
	private String Name;
	
	@NotNull(message = "fill in this blank")
	private Integer payment;
	
	private String memo;
	
	private Integer paymentSum;

}
