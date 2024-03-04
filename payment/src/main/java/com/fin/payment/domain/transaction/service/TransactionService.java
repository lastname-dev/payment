package com.fin.payment.domain.transaction.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fin.payment.domain.account.entity.Account;
import com.fin.payment.domain.account.repository.AccountRepository;
import com.fin.payment.domain.member.entity.Member;
import com.fin.payment.domain.member.repository.MemberRepository;
import com.fin.payment.domain.transaction.dto.TransferDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private final MemberRepository memberRepository;
	private final AccountRepository accountRepository;
	private final RedissonClient redissionClient;
	@Transactional
	public void transfer(TransferDto transferDto) throws Exception {
		RLock lock = redissionClient.getLock("{key}");
		boolean isLocked = false;
		try {
			//락 획득 요청
			isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
			if (!isLocked) {
				//락 획득 실패 시 예외 처리
				throw new Exception("z");
			}
			Member receiver = memberRepository.findById(transferDto.getReceiver()).get();
			Member sender = memberRepository.findById(transferDto.getSender()).get();

			Account recevierAccount = accountRepository.findByMember(receiver);
			Account senderAccount = accountRepository.findByMember(sender);

			recevierAccount.updateMoney(recevierAccount.getAmount()+ transferDto.getAmount());
			senderAccount.updateMoney(senderAccount.getAmount()- transferDto.getAmount());
		} catch (InterruptedException e) {
			//쓰레드가 인터럽트 될 경우 예외 처리
			Thread.currentThread().interrupt(); // 인터럽트 상태 재설정
			throw new Exception("Thread interrupted", e);
		} finally {
			//락 해제
			if (isLocked) {
				lock.unlock(); // 락 획득에 성공한 경우에만 해제
			}
		}

	}
}
