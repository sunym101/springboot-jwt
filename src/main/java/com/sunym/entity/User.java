package com.sunym.entity;

import javax.persistence.*;

/**
 * 资源用户Entity类
 * 
 * @author sunyongmeng
 * @date 2018/12/19
 * @version 1.0.1
 */
@Entity
@Table(name = "m_user")
public class User {

	/** id: 自增长类型 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	/** 用户名 */
	@Column(name = "username")
	private String username;

	/** 用户密码(加密) */
	@Column(name = "password")
	private String password;

	/** 用户权限 */
	@Column(name = "role")
	private String role;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User{id=" + id + ", username='" + username + "', password='" + password + "', role='" + role + "'}";
	}
}
