package com.xperiecia.consultoria.infrastructure;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro CORS personalizado para manejar peticiones preflight
 * 
 * Este filtro se ejecuta antes que cualquier otro filtro para asegurar
 * que las peticiones OPTIONS (preflight) sean manejadas correctamente.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // Configurar headers CORS
        response.setHeader("Access-Control-Allow-Origin", "https://www.xperiecia.com");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD, TRACE, CONNECT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization");

        // Manejar peticiones OPTIONS (preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            System.out
                    .println("✅ Petición OPTIONS (preflight) manejada correctamente para: " + request.getRequestURI());
            return;
        }

        // Continuar con la cadena de filtros
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("✅ CorsFilter inicializado");
    }

    @Override
    public void destroy() {
        System.out.println("✅ CorsFilter destruido");
    }
}
