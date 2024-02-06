package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.domain.Member;
import com.example.app.service.MemberService;

import jakarta.validation.Valid;

@RequestMapping("/member")
@Controller
public class MemberController {

	@Autowired
	private MemberService service;

	@GetMapping("/members")
	public String members(Model model) throws Exception {
		List<Member> members = service.getMemberList();
		model.addAttribute("members", members);
		return "member/members";
	}

	@GetMapping("/add")
	public String addGet(Model model) throws Exception {
		model.addAttribute("title", "Add Member");
		model.addAttribute("member", new Member());
		return "member/addMember";
	}

	@PostMapping("/add")
	public String addPost(@Valid Member member, Errors errors, RedirectAttributes rd, Model model) throws Exception {
		if (errors.hasErrors()) {
			return "member/addMember";
		}
		service.addMember(member);
		rd.addFlashAttribute("MemberFlash", "memberFlash");
		rd.addFlashAttribute("akStatusMessage", "Added Member Compleated");
		rd.addFlashAttribute("newName", member.getName());
		return "redirect:/member/addMemberDone";
	}

	@GetMapping("/addMemberDone")
	public String addMemberDone(Member member, Model model) {
		System.out.println("DEBUG: addMemberDone method called");

		model.addAttribute("member", member);

		return "member/addMemberDone";
	}

}
