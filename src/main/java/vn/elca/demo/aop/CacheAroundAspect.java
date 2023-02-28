package vn.elca.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.elca.demo.model.Params;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.util.MicroStreamCache;

import java.util.Arrays;

@Component
@Aspect
public class CacheAroundAspect {
    @Autowired
    MicroStreamCache cached;


    @Around("@annotation(vn.elca.demo.aop.CustomMicrostreamCached)")
    public Object cacheAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        StringBuffer key = new StringBuffer();
        key.append(proceedingJoinPoint.getSignature().getName()).append("(");

        MethodSignature methodSig = (MethodSignature) proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        String[] parametersName = methodSig.getParameterNames();
        for (int i = 0; i < parametersName.length; i++) {
            key.append(parametersName[i]).append("=").append(args[i]);
            if (i != parametersName.length - 1) {
                key.append(",");
            } else {
                key.append(")");
            }
        }
        Params params = new Params(key.toString());
        Object value = cached.get(params);
        if (value != null) {
            System.out.println("Get data from cache");
            return value;

        }
        try {
            value = proceedingJoinPoint.proceed();
            cached.put(params, (ShopAvailabilityData) value);
            return value;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

}
