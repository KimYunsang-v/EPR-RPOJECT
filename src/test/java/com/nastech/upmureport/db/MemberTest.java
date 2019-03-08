package com.nastech.upmureport.db;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import com.nastech.upmureport.config.PersistenceJPAConfig;
import com.nastech.upmureport.config.WebConfig;
import com.nastech.upmureport.domain.entity.Dept;
import com.nastech.upmureport.domain.entity.Position;
import com.nastech.upmureport.domain.repository.DeptRepository;
import com.nastech.upmureport.domain.repository.PositionRepository;
import com.nastech.upmureport.jpa.sample.Member;
import com.nastech.upmureport.jpa.sample.MemberRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, PersistenceJPAConfig.class}, loader=AnnotationConfigWebContextLoader.class)
public class MemberTest {

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	DeptRepository deptrepository;
	@Autowired
	PositionRepository positionrepository;
//	
//	@Autowired
//	PlatformTransactionManager platformTransactionManager;
	
	@Test
	public void save1() {
		System.out.println(memberRepository);
		memberRepository.save(Member.builder().name("test").id(1).age(32).build());
//		System.out.println(platformTransactionManager);
		
		assertThat(memberRepository.findAll().get(0).getName(), is("test"));
	}
}
