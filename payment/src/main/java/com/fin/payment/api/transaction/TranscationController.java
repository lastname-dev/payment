package com.fin.payment.api.transaction;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fin.payment.domain.account.repository.AccountRepository;
import com.fin.payment.domain.member.repository.MemberRepository;
import com.fin.payment.domain.transaction.dto.TransferDto;
import com.fin.payment.domain.transaction.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TranscationController {

	private final TransactionService transactionService;
	private final MemberRepository memberRepository;
	private final AccountRepository accountRepository;

	@PostMapping("/send")
	public ResponseEntity<?> transfer(@RequestBody TransferDto transferDto) throws Exception {
		transactionService.transfer(transferDto);
		return ResponseEntity.ok().build();
	}

	// @PostMapping("/test")
	// public ResponseEntity<?> test(){
	// 	for (int i = 1; i <=100 ; i++) {
	// 		Member member = new Member(i);
	// 		memberRepository.save(member);
	// 		Account account = new Account((long)i,10000,member);
	// 		accountRepository.save(account);
	// 	}
	// 	return ResponseEntity.ok().build();
	// }
}
