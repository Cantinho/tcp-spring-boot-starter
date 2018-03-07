package br.com.cantinho.tcpspringbootstarter.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TcpControllerBeanPostProcessor implements BeanPostProcessor {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TcpControllerBeanPostProcessor.class);

  /**
   * All TCP controller cache.
   */
  private Map<String, Class> tcpControllerCache = new HashMap<>();

  /**
   * TCP server.
   */
  @Autowired
  @Qualifier("TcpThreadPoolServer")
  private TcpServer server;

  /**
   * Post process before initialization.
   *
   * @param bean
   * @param beanName
   * @return
   * @throws BeansException
   */
  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName)
      throws BeansException {

    LOGGER.info("postProcessBeforeInitialization");
    Class<?> beanClass = bean.getClass();
    if(beanClass.isAnnotationPresent(TcpController.class)) {
      LOGGER.info("---TcoController annotation present");
      tcpControllerCache.put(beanName, beanClass);
    }
    return bean;
  }

  /**
   * Post process after initialization.
   *
   * @param bean
   * @param beanName
   * @return
   * @throws BeansException
   */
  public Object postProcessAfterInitialization(final Object bean, final String beanName)
      throws BeansException {

    LOGGER.info("postProcessAfterInitialization");
    if (tcpControllerCache.containsKey(beanName)) {
      LOGGER.info("--- contains key:" + beanName);
      List<Method> receiveMethods = new ArrayList<>();
      List<Method> connectMethods = new ArrayList<>();
      List<Method> disconnectMethods = new ArrayList<>();
      Method[] methods = bean.getClass().getMethods();
      for (Method method : methods) {
        if (method.getName().startsWith("receive") && method.getParameterCount() == 2
            && method.getParameterTypes()[0] == TcpConnection.class) {
          receiveMethods.add(method);
        } else if (method.getName().startsWith("connect") && method.getParameterCount() == 1
            && method.getParameterTypes()[0] == TcpConnection.class) {
          connectMethods.add(method);
        } else if (method.getName().startsWith("disconnect") && method.getParameterCount() == 1
            && method.getParameterTypes()[0] == TcpConnection.class) {
          disconnectMethods.add(method);
        }
      }
      server.addListener( new TcpControllerListener(bean, receiveMethods, connectMethods, disconnectMethods));
    }
    return bean;
  }

}
