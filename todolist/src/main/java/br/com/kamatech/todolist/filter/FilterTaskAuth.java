package br.com.kamatech.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.kamatech.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth  extends OncePerRequestFilter{

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

      var servletPath = request.getServletPath();

      if (servletPath.equals("/tasks/")) {

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

        // validar usuário
        var user = this.userRepository.findByUsername(username);
        if(user == null) {
          response.sendError(401, "User not found");
        } else {
          // validar senha
          var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
          if(passwordVerify.verified) {
            filterChain.doFilter(request, response);
          } else {
            response.sendError(401);
          }
        }
      } else {
          filterChain.doFilter(request, response);
      }

  }
}
