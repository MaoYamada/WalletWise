package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.Member;

@Mapper
public interface MemberMapper {

	List<Member> selectAll() throws Exception;

	void insert(Member member) throws Exception;

	void update(Member member) throws Exception;

	void delete(Integer id) throws Exception;

	Member selectById(String nameId);

}
