package org.redis4j.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * Configures access to the Spring application context.
 */
@Component
public class Redis4jBeanConfig implements BeanFactoryPostProcessor {

    private static ConfigurableListableBeanFactory beanFactory;

    /**
     * Retrieves the ConfigurableListableBeanFactory instance.
     *
     * @return the ConfigurableListableBeanFactory instance
     */
    public static ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * Retrieves a bean by name from the Spring application context.
     *
     * @param name the name of the bean to retrieve
     * @param <T>  the type of the bean
     * @return the bean instance
     * @throws BeansException if an error occurs while retrieving the bean
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T getBean(String name) throws BeansException {
        return (T) getBeanFactory().getBean(name);
    }

    /**
     * Retrieves a bean by type from the Spring application context.
     *
     * @param clazz the class of the bean to retrieve
     * @param <T>   the type of the bean
     * @return the bean instance
     * @throws BeansException if an error occurs while retrieving the bean
     */
    public static <T> T getBean(Class<T> clazz) throws BeansException {
        return getBeanFactory().getBean(clazz);
    }

    /**
     * Checks if a bean with the specified name exists in the Spring application context.
     *
     * @param name the name of the bean to check
     * @return true if the bean exists, false otherwise
     */
    public static boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    /**
     * Checks if the bean with the specified name is a singleton in the Spring application context.
     *
     * @param name the name of the bean to check
     * @return true if the bean is a singleton, false otherwise
     * @throws NoSuchBeanDefinitionException if the bean definition cannot be found
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isSingleton(name);
    }

    /**
     * Retrieves the type of the bean with the specified name from the Spring application context.
     *
     * @param name the name of the bean
     * @return the type of the bean
     * @throws NoSuchBeanDefinitionException if the bean definition cannot be found
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getType(name);
    }

    /**
     * Retrieves the aliases for the bean with the specified name from the Spring application context.
     *
     * @param name the name of the bean
     * @return an array of alias names
     * @throws NoSuchBeanDefinitionException if the bean definition cannot be found
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getAliases(name);
    }

    @SuppressWarnings({"NullableProblems"})
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        Redis4jBeanConfig.beanFactory = factory;
    }
}