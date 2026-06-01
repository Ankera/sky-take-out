package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Before("@annotation(com.sky.annotation.AutoFill)")
    public void autoFillAspect(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Object arg = args[0];

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AutoFill annotation = method.getAnnotation(AutoFill.class);
        if (annotation != null) {
            OperationType value = annotation.value();
            log.info("开始进行公共字段自动填充 => " + value);

            Class<?> aClass = arg.getClass();
            if (value == OperationType.INSERT) {
                try {
                    Field f1 = aClass.getDeclaredField("createTime");;
                    Field f2 = aClass.getDeclaredField("updateTime");
                    Field f3 = aClass.getDeclaredField("createUser");
                    Field f4 = aClass.getDeclaredField("updateUser");
                    f1.setAccessible(true);
                    f2.setAccessible(true);
                    f3.setAccessible(true);
                    f4.setAccessible(true);

                    f1.set(arg, LocalDateTime.now());
                    f2.set(arg, LocalDateTime.now());

                    f3.set(arg, BaseContext.getCurrentId());
                    f4.set(arg, BaseContext.getCurrentId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (value == OperationType.UPDATE) {
                try {
                    Field f2 = aClass.getDeclaredField("updateTime");
                    Field f4 = aClass.getDeclaredField("updateUser");
                    f2.setAccessible(true);
                    f4.setAccessible(true);

                    f2.set(arg, LocalDateTime.now());

                    f4.set(arg, BaseContext.getCurrentId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
