package com.xperiecia.consultoria.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Habilitar broker simple para tópicos y colas
        config.enableSimpleBroker("/topic", "/queue");

        // Prefijo para mensajes de aplicación
        config.setApplicationDestinationPrefixes("/app");

        // Prefijo para destinatarios de usuario
        config.setUserDestinationPrefix("/user");

        System.out.println("🔌 WebSocket Message Broker configurado");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Endpoint WebSocket con SockJS para compatibilidad
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*") // Permitir todos los orígenes en producción (o especificar lista)
                .withSockJS();

        // Endpoint WebSocket nativo
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*"); // Permitir todos los orígenes en producción

        System.out.println("🔌 WebSocket endpoints registrados con CORS abierto (*)");
    }
}
