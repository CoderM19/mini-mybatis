package com.example.minimybatis.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Plugin implements InvocationHandler {

    private final Object target;
    private final Interceptor interceptor;
    private final Map<Class<?>, Set<Method>> signatureMap;

    public Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.signatureMap = signatureMap;
    }

    /**
     *  包装代理对象，若目标类型匹配拦截点则返回代理对象，否则返回原对象
     * @param target 目标对象
     * @param interceptor 拦截器
     * @return 代理对象或者原对象
     */
    public static Object wrap(Object target, Interceptor interceptor) {
        //从拦截器获取@Intercepts 注解，解析出要拦截的类型和方法
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
        Class<?> type = target.getClass();
        Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
        if (interfaces.length > 0){
          return Proxy
                  .newProxyInstance(type.getClassLoader(),
                          interfaces,
                              new Plugin(target, interceptor, signatureMap));
        }
        return target;
    }


    private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
       Intercepts intercepts = interceptor.getClass().getAnnotation(Intercepts.class);
       if (intercepts == null){
            throw new RuntimeException("Interceptor must be annotated with @Intercepts");
       }
       Signature[] signatures = intercepts.value();
       Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();
       for (Signature signature : signatures){
           Set<Method> methods = signatureMap.computeIfAbsent(signature.type(), k -> new HashSet<>());
           try{
               Method targetMethod = signature.type().getMethod(signature.method(), signature.args());
               methods.add(targetMethod);
           } catch (NoSuchMethodException e) {
               throw new RuntimeException("Could not find method " + signature.method() + " on class " + signature.type(), e);
           }
       }
        return signatureMap;
    }


    private static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
          Set<Class<?>> interfaces = new HashSet<>();
          while (type != null) {
              for (Class<?> interfaceClass : type.getInterfaces()){
                  if (signatureMap.containsKey(interfaceClass)){
                      interfaces.add(interfaceClass);
                  }
              }
              type = type.getSuperclass();
          }
         return interfaces.toArray(new Class<?>[0]);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try{
           Set<Method> methods = signatureMap.get(method.getDeclaringClass());
           if (methods != null && methods.contains(method)){
               return interceptor.intercept(new Invocation(target, method, args));
           }
           return method.invoke(target, args);
        }catch (Exception e){
            throw e;
        }
    }
}
