package com.sunym.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

public class JwtTokenUtils {

	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";

	/** token秘钥，请勿泄露，请勿随便修改 backups: JwtTokenSecret */
	private static final String SECRET = "JwtTokenSecret";

	/** 发行人 */
	private static final String ISS = "jwtService";

	/** 角色的key */
	private static final String ROLE_CLAIMS = "role";

	// 过期时间是3600秒，既是1个小时
	private static final long EXPIRATION = 3600L;

	// 选择了记住我之后的过期时间为7天
	private static final long EXPIRATION_REMEMBER = 604800L;

	// 创建token
	public static String createToken(String username, String role, boolean isRememberMe) {
		long expiration = isRememberMe ? EXPIRATION_REMEMBER : EXPIRATION;
		HashMap<String, Object> map = new HashMap<>();
		map.put(ROLE_CLAIMS, role);
		Date now = new Date();
		return Jwts.builder()
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.setClaims(map)// 存放角色信息
				.setIssuer(ISS)// 发行人
				.setSubject(username)// 主题
				.setIssuedAt(new Date())// 发布时间
				.setExpiration(new Date(now.getTime() + expiration * 1000))// 到期时间
				.compact();
	}

	// 从token中获取用户名
	public static String getUsername(String token) {
		return getTokenBody(token).getSubject();
	}

	// 获取用户角色
	public static String getUserRole(String token) {
		return (String) getTokenBody(token).get(ROLE_CLAIMS);
	}

	// 是否已过期
	public static boolean isExpiration(String token) {
		return getTokenBody(token).getExpiration().before(new Date());
	}

	public static Claims getTokenBody(String token) {
		return Jwts.parser()
				.setSigningKey(SECRET)
				.parseClaimsJws(token)
				.getBody();
	}

	public static void responseOutWithJson(HttpServletResponse response, String responseObject) {
		// 将实体对象转换为JSON Object转换
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.append(responseObject);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static JSONObject createResponseJsonSuccess(String token) {
		Date issuedTime = JwtTokenUtils.getTokenBody(token).getIssuedAt();
		Date expireTime = JwtTokenUtils.getTokenBody(token).getExpiration();

		JSONObject jsonResult = new JSONObject(true);
		jsonResult.put("token", token);
		jsonResult.put("issuedat", issuedTime.getTime());
		jsonResult.put("expiredat", expireTime.getTime());

		JSONObject json = new JSONObject(true);
		json.put("result_code", "0000");
		json.put("result_messge", "OK");
		json.put("result", jsonResult);
		return json;
	}

	public static JSONObject createResponseJsonFailed(String msgCode, String msg) {
		JSONObject json = new JSONObject(true);
		json.put("result_code", msgCode);
		json.put("result_messge", msg);
		return json;
	}
}
