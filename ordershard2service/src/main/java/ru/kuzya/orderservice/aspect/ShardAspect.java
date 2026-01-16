package ru.kuzya.orderservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.kuzya.orderservice.config.Shard;
import ru.kuzya.orderservice.config.ShardContextHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
public class ShardAspect {

    @Around("@annotation(DetermineShard)")
    public Object determineShard(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        Long userId = extractUserId(method, args);

        if (userId != null) {
            Shard shard = (userId % 2 == 0) ? Shard.SHARD_1 : Shard.SHARD_2;

            log.info("=== ShardAspect: Setting shard {} for user {} ===", shard, userId);
            log.info("Method: {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());

            ShardContextHolder.setShardKey(shard);

            try {
                Object result = joinPoint.proceed();
                log.info("=== ShardAspect: Successfully processed for shard {} ===", shard);
                return result;
            } finally {
                log.info("=== ShardAspect: Clearing shard context ===");
                ShardContextHolder.clear();
            }
        } else {
            log.warn("=== ShardAspect: UserId not found in method parameters ===");
            return joinPoint.proceed();
        }
    }

    private Long extractUserId(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            // Ищем по имени параметра
            if (parameters[i].getName().equals("userId") && args[i] instanceof Long) {
                return (Long) args[i];
            }
            // Или по аннотации
            if (parameters[i].isAnnotationPresent(ShardKey.class) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }
        return null;
    }

}
