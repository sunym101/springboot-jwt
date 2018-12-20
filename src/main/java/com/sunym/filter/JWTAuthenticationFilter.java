package com.sunym.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunym.entity.JwtUser;
import com.sunym.model.LoginUser;
import com.sunym.utils.JwtTokenUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 验证成功后的用户鉴权。每一次需要权限的请求都需要检查该用户是否有该权限去操作该资源，框架帮我们做的。<br/>
 * 我们只要告诉spring- security该用户是否已登录，是什么角色，拥有什么权限就可以了。
 * 
 * @author sunyongmeng
 * @date 2018/12/19
 */
@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private ThreadLocal<Integer> rememberMe = new ThreadLocal<>();
	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		super.setFilterProcessesUrl("/auth/login");
	}

	/**
	 * 首先过滤器会调用自身的attemptAuthentication方法(方法重载)，追加是否记住内容处理。<br>
	 * 从request中取出authentication,
	 * authentication是在org.springframework.security.web.context.
	 * SecurityContextPersistenceFilter过滤器中通过捕获用户提交的登录表单中的内容生成的一个org.
	 * springframework.security.core.Authentication接口实例.
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {

		String username = null;
		// 从输入流中获取到登录的信息
		try {
			LoginUser loginUser = new ObjectMapper().readValue(request.getInputStream(), LoginUser.class);
			username = loginUser.getUsername();
			log.debug("User login, username=[" + username + "], rememberMe=[" + loginUser.getRememberMe() + "]");

			rememberMe.set(loginUser.getRememberMe());
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), new ArrayList<>()));
		} catch (InternalAuthenticationServiceException e) {
			// 用户不存在异常
			log.error("用户登录失败: username=[" + username + "] 不存在.");
			return null;
		} catch (IOException e) {
			// 请求参数异常
			log.error("User login failed: 请求参数异常。");
			return null;
		}
	}

	/**
	 * 成功验证后调用的方法。<br>
	 * 如果验证成功，就生成token并返回。<br>
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		JwtUser jwtUser = (JwtUser) authResult.getPrincipal();
		boolean isRemember = rememberMe.get() == 1;
		log.debug("jwtUser:" + jwtUser.toString() + ", rememberMe:" + rememberMe);

		String role = "";
		Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
		for (GrantedAuthority authority : authorities) {
			role = authority.getAuthority();
		}
		log.debug("jwtUser:" + jwtUser.toString() + ", role:" + role);

		String token = JwtTokenUtils.createToken(jwtUser.getUsername(), role, isRemember);
		JSONObject json = JwtTokenUtils.createResponseJsonSuccess(token);
		
		// 返回创建成功的token, 但是这里创建的token只是单纯的token
		// 按照jwt的规定，最后请求的时候应该是 `Bearer token`
		token = JwtTokenUtils.TOKEN_PREFIX + token;
		response.setHeader("token", token);
		JwtTokenUtils.responseOutWithJson(response, JSONObject.toJSONString(json, true));
		log.debug("jwtUser:" + jwtUser.toString() + ", token:" + token);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {
		log.debug("authentication failed, reason: " + failed.getMessage());
		response.getWriter().write("authentication failed, reason: " + failed.getMessage());
		
		JSONObject json = JwtTokenUtils.createResponseJsonFailed("8001", "authentication failed, reason: " + failed.getMessage());
		JwtTokenUtils.responseOutWithJson(response, JSONObject.toJSONString(json, true));
	}
}
