package com.fin.payment.domain.member.entity;


import java.util.List;

import com.fin.payment.domain.account.entity.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;


	@OneToMany(mappedBy = "member")
	private List<Account> accounts;

	public Member (long memberId){
		this.memberId = memberId;
	}
}
