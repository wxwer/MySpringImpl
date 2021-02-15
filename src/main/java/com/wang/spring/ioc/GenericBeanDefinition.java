package com.wang.spring.ioc;
import java.util.Objects;
import com.wang.spring.constants.BeanScope;

public class GenericBeanDefinition implements BeanDefinition{

    private Class<?> beanClass;

    private BeanScope scope =BeanScope.SINGLETON;

    private String initMethodName;


    public void setBeanClass(Class<?> beanClass){
        this.beanClass = beanClass;
    }

    public void setScope(BeanScope scope) {
        this.scope = scope;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public BeanScope getScope() {
        return scope;
    }

    @Override
    public boolean isSingleton() {
        return Objects.equals(scope,BeanScope.SINGLETON);
    }

    @Override
    public boolean isPrototype() {
        return Objects.equals(scope, BeanScope.PROTOTYPE);
    }

    @Override
    public String getInitMethodName() {
        return initMethodName;
    }
}
