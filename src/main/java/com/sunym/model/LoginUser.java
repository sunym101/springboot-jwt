package com.sunym.model;

public class LoginUser {

	/** 用户名 */
	private String username;

	/** 用户密码 */
	private String password;

	/** 是否记住我（Token有效时间不同） */
	private Integer rememberMe;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(Integer rememberMe) {
		this.rememberMe = rememberMe;
	}
}
