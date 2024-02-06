package com.example.app.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

//＝エンティティクラス？
@Data
public class Member {
	
	private Integer id;

	@NotBlank(message = "fill in this blank")
	@Size(max = 10, message = "in 10 characters")
	private String name;

}
