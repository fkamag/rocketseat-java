package br.com.kamatech.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth  extends OncePerRequestFilter{

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

        var authorization = request.getHeader("authorization");

        // retirando a palavra Basic e o espaço
        var user_password = authorization.substring("Basic".length()).trim();

        // convertendo o array de bytes
        byte[] authDecode = Base64.getDecoder().decode(user_password);

        // convertendo o array de byte em String
        var authString = new String(authDecode);

        // separando username e password
        String[] credentials = authString.split(":");
        String username = credentials[0];
        String password = credentials[1];

        System.out.println("username: " + username);
        System.out.println("password: " + password);

        filterChain.doFilter(request, response);
    
  }

  
}
