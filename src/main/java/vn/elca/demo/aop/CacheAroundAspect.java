package vn.elca.demo.aop;

import one.microstream.storage.types.StorageManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.elca.demo.database.Root;
import vn.elca.demo.model.AbstractDto;
import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.Params;
import vn.elca.demo.model.enumType.DataType;
import vn.elca.demo.util.MicroStreamCache;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Aspect
public class CacheAroundAspect {
    @Autowired
    MicroStreamCache cached;

    @Around("@annotation(vn.elca.demo.model.annotation.CustomMicrostreamCached)")
    public Object cacheAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        StringBuffer key = new StringBuffer();
        key.append(proceedingJoinPoint.getSignature().getName());

        MethodSignature methodSig = (MethodSignature) proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        String[] parametersName = methodSig.getParameterNames();

        if (parametersName.length != 0) {
            key.append("(");
            for (int i = 0; i < parametersName.length; i++) {
                key.append(parametersName[i]).append("=").append(args[i]);
                if (i != parametersName.length - 1) {
                    key.append(",");
                } else {
                    key.append(")");
                }
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

            cached.put(params, value);

//            Object finalValue = value;
//            Runnable runnable = () -> {
//                cached.put(params, finalValue);
//            };
//            runnable.run();
//
            return value;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
