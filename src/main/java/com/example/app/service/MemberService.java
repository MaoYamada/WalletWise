package com.example.app.service;

import java.util.List;

import com.example.app.domain.Member;

public interface MemberService {

	List<Member> getMemberList() throws Exception;

	void addMember(Member member) throws Exception;

	void delete(Integer id) throws Exception;

	Member getMemberById(String nameId);

}
