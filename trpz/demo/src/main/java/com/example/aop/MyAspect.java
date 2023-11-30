package com.example.aop;

import com.example.util.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@Aspect
@Slf4j
public class MyAspect {



    @Around("Pointcuts.allFindMethods()")
    public Object aroundFindMethods(ProceedingJoinPoint joinPoint) throws ServiceException{
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        if(methodSignature.getName().equals("findAll")) {
            log.info("Try to find all entities in crud service");
        } else if (methodSignature.getName().equals("findOne")) {
            log.info("Try to find one entity int crud service");
        }

        Object result;
        try {
            result = joinPoint.proceed();
            log.info("Operation successful");
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("Error while saving in  crud service", e.getCause());
        }

        return result;
    }

    @Around("Pointcuts.saveMethods()")
    public void aroundSaveMethods(ProceedingJoinPoint joinPoint) throws ServiceException{
        log.info("Try to save entity in crud service");

        try {
            joinPoint.proceed();
            log.info("Entity saved");
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("Error while saving in crud service", e.getCause());
        }
    }

    @Around("Pointcuts.updateMethods()")
    public void aroundUpdateMethods(ProceedingJoinPoint joinPoint) throws ServiceException{
        log.info("Try to update entity in crud service");

        try {
            joinPoint.proceed();
            log.info("Entity deleted");
        } catch (IllegalArgumentException e ){
            throw e;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("Error while updating in crud service", e.getCause());
        }
    }

    @Around("Pointcuts.deleteMethods()")
    public void aroundDeleteMethods(ProceedingJoinPoint joinPoint) throws ServiceException{
        log.info("Try to delete entity in crud service");

        try {
            joinPoint.proceed();
            log.info("Entity deleted");
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("Error while deleting in crud service", e.getCause());
        }
    }
}
