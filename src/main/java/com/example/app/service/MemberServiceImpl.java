package com.example.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.domain.Member;
import com.example.app.mapper.MemberMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberMapper memberMapper;

	// =getAllData
	@Override
	public List<Member> getMemberList() throws Exception {
		return memberMapper.selectAll();
	}

	@Override
	public void addMember(Member member) throws Exception {
		memberMapper.insert(member);
	}

	@Override
	public Member getMemberById(String nameId) {
		return memberMapper.selectById(nameId);
	}

	@Override
	public void delete(Integer id) throws Exception {
		System.out.println("Deleting member with name: " + id);
		memberMapper.delete(id);
	}
}
