package com.example.management.infrastructure.config;

import com.example.management.application.services.OrderService;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * Configuración de la capa de infraestructura para aplicar aspectos de framework
 * a los servicios de aplicación sin acoplar la capa de aplicación al framework.
 */
@Configuration
@EnableTransactionManagement
public class ApplicationServiceConfig {
    
    /**
     * Configura el OrderService como bean de Spring.
     * Las transacciones se manejan mediante AOP configurado en Spring Boot.
     * Esto permite mantener la capa de aplicación libre de anotaciones de framework.
     */
    @Bean
    public OrderService orderService(com.example.management.application.ports.out.OrderRepository orderRepository) {
        return new OrderService(orderRepository);
    }
}
