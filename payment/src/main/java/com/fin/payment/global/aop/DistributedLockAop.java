package com.fin.payment.global.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
	private static final String REDISSON_LOCK_PREFIX = "LOCK:";

	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(com.fin.payment.global.aop.DistributedLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		String key;
		int cnt = distributedLock.cnt();
		List<RLock> locks = new ArrayList<>(cnt);
		String[] parts = distributedLock.key().split("-");
		for (int i = 0; i < cnt; i++) {
			key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(),parts[i]);
			RLock rLock = redissonClient.getLock(key);
			locks.add(rLock);
		}
		try {
			boolean available = locks.stream()
				.allMatch(lock -> {
					try {
						return lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return false;
					}
				});
			if (!available) {
				return false;
			}

			return aopForTransaction.proceed(joinPoint);  // (3)
		} catch (InterruptedException e) {
			throw new InterruptedException();
		} finally {
			locks.forEach(lock -> {
				try {
					lock.unlock();
				} catch (IllegalMonitorStateException e) {
					log.info("Redisson Lock Already UnLock");
				}
			});
		}
	}
}
