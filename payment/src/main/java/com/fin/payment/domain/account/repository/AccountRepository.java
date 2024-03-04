package com.fin.payment.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fin.payment.domain.account.entity.Account;
import com.fin.payment.domain.member.entity.Member;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
	Account findByMember(Member member);
}
