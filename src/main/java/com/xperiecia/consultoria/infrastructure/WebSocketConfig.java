package com.xperiecia.consultoria.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar broker simple para tópicos y colas
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para mensajes de aplicación
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefijo para destinatarios de usuario
        config.setUserDestinationPrefix("/user");
        
        System.out.println("🔌 WebSocket Message Broker configurado");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket con SockJS para compatibilidad
        registry.addEndpoint("/ws/notifications")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
        
        // Endpoint WebSocket nativo
        registry.addEndpoint("/ws/notifications")
                .setAllowedOrigins("http://localhost:3000");
        
        System.out.println("🔌 WebSocket endpoints registrados:");
        System.out.println("   - /ws/notifications (con SockJS)");
        System.out.println("   - /ws/notifications (nativo)");
        System.out.println("   - Permitido origen: http://localhost:3000");
    }
}
