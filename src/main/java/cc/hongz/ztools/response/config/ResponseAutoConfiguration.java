package cc.hongz.ztools.response.config;

import cc.hongz.ztools.response.ajax.AjaxStatusRegistry;
import cc.hongz.ztools.response.handler.GlobalExceptionHandler;
import cc.hongz.ztools.response.i18n.I18nResponseBuilder;
import cc.hongz.ztools.response.interceptor.InterceptorProperties;
import cc.hongz.ztools.response.interceptor.ResponseInterceptStrategy;
import cc.hongz.ztools.response.interceptor.ResponseInterceptor;
import cc.hongz.ztools.response.interceptor.strategy.EntityToAjaxResultStrategy;
import cc.hongz.ztools.response.interceptor.strategy.MapToAjaxResultStrategy;
import cc.hongz.ztools.response.interceptor.strategy.StringToAjaxResultStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 响应模块 Spring 配置.
 * <p>
 * 注册国际化构建器、全局异常处理器、响应拦截器及内置转换策略。
 * 在 Spring Boot 应用中可通过 {@code @Import(ResponseAutoConfiguration.class)} 引入；
 * Phase 4 将提供独立 Starter 自动装配。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Configuration
public class ResponseAutoConfiguration {

    /**
     * 全局响应配置属性.
     *
     * @return ResponseProperties
     */
    @Bean
    public ResponseProperties responseProperties() {
        return new ResponseProperties();
    }

    /**
     * 响应拦截器配置属性.
     *
     * @return InterceptorProperties
     */
    @Bean
    public InterceptorProperties interceptorProperties() {
        return new InterceptorProperties();
    }

    /**
     * 默认消息源（classpath messages*.properties）.
     *
     * @param responseProperties 全局配置
     * @return MessageSource
     */
    @Bean
    public ResourceBundleMessageSource responseMessageSource(final ResponseProperties responseProperties) {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(responseProperties.getMessageBasenames());
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * AJAX 业务状态码注册表.
     *
     * @param responseProperties 全局配置
     * @return AjaxStatusRegistry
     */
    @Bean
    public AjaxStatusRegistry ajaxStatusRegistry(final ResponseProperties responseProperties) {
        final AjaxStatusRegistry registry = new AjaxStatusRegistry();
        if (responseProperties.isAjaxStatusRegistryEnabled()) {
            registry.init();
        }
        return registry;
    }

    /**
     * 国际化响应构建器.
     *
     * @param responseMessageSource 消息源
     * @param responseProperties    全局配置
     * @return I18nResponseBuilder
     */
    @Bean
    public I18nResponseBuilder i18nResponseBuilder(
            final ResourceBundleMessageSource responseMessageSource,
            final ResponseProperties responseProperties) {
        final I18nResponseBuilder builder = new I18nResponseBuilder(responseMessageSource);
        builder.setI18nEnabled(responseProperties.isI18nEnabled());
        builder.setDefaultLocale(Locale.forLanguageTag(
                responseProperties.getDefaultLocale().replace('_', '-')));
        return builder;
    }

    /**
     * 全局异常处理器.
     *
     * @param i18nResponseBuilder 国际化构建器
     * @return GlobalExceptionHandler
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler(final I18nResponseBuilder i18nResponseBuilder) {
        return new GlobalExceptionHandler(i18nResponseBuilder);
    }

    /**
     * Map 转 AjaxResult 策略.
     *
     * @param interceptorProperties 拦截器配置
     * @return MapToAjaxResultStrategy
     */
    @Bean
    public MapToAjaxResultStrategy mapToAjaxResultStrategy(final InterceptorProperties interceptorProperties) {
        return new MapToAjaxResultStrategy(interceptorProperties);
    }

    /**
     * 字符串转 AjaxResult 策略.
     *
     * @param interceptorProperties 拦截器配置
     * @return StringToAjaxResultStrategy
     */
    @Bean
    public StringToAjaxResultStrategy stringToAjaxResultStrategy(final InterceptorProperties interceptorProperties) {
        return new StringToAjaxResultStrategy(interceptorProperties);
    }

    /**
     * 实体类转 AjaxResult 策略.
     *
     * @param interceptorProperties 拦截器配置
     * @return EntityToAjaxResultStrategy
     */
    @Bean
    public EntityToAjaxResultStrategy entityToAjaxResultStrategy(final InterceptorProperties interceptorProperties) {
        return new EntityToAjaxResultStrategy(interceptorProperties);
    }

    /**
     * 响应体拦截器.
     *
     * @param mapStrategy    Map 策略
     * @param stringStrategy 字符串策略
     * @param entityStrategy 实体策略
     * @param properties     拦截器配置
     * @return ResponseInterceptor
     */
    @Bean
    public ResponseInterceptor responseInterceptor(
            final MapToAjaxResultStrategy mapStrategy,
            final StringToAjaxResultStrategy stringStrategy,
            final EntityToAjaxResultStrategy entityStrategy,
            final InterceptorProperties properties) {
        final List<ResponseInterceptStrategy> strategies = Arrays.asList(
                mapStrategy,
                stringStrategy,
                entityStrategy
        );
        return new ResponseInterceptor(strategies, properties);
    }
}
