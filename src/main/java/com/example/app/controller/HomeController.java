package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/stbHome")
	public String home() {
		return "home";
	}

	@GetMapping("/error")
	public String error() {
		return "error";
	}

	@GetMapping("/logout")
	public String logout() {
		return "logout";
	}
}
