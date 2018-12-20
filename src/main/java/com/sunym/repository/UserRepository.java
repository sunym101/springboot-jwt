package com.sunym.repository;

import org.springframework.data.repository.CrudRepository;

import com.sunym.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	User findByUsername(String username);

	/*
	 *
	 * Repository接口,他是一个空接口,仅仅是一个标识 .hql操作的是对象,而sql操作的是表,所以这里的User指的是类
	 * 可以直接使用Repository提供的一些方法,但是必须方法的命名要遵守一定的规则,例如findByXx,findByXxLike,
	 * findByXxStartingWith...
	 * 
	 * 使用@Query注解来解决定义规则的麻烦和实现复杂查询,工作中用的比较多
	 * 
	 * @Query("select o from User o where o.uno = ?1 and o.uname = ?2") public
	 * User findUser(String uno, String uname);
	 * 
	 * //配合@Modifying注解和事务,可以完成更新和删除的操作,jpql不支持增加的操作
	 * 
	 * @Modifying
	 * 
	 * @Query("delete from User where uno=:uno") public void
	 * delUser(@Param("uno") int uno);
	 * 
	 * @Modifying
	 * 
	 * @Query("update User o set o.uname = :uname where uno = :uno") public void
	 * updUser(@Param("uno")int uno,@Param("uname")String uname);
	 */
}
