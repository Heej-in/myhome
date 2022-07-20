package com.example.myhome.repository;

import com.example.myhome.model.QUser;
import com.example.myhome.model.User;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

class CustomizedUserRepositoryImpl implements CustomizedUserRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<User> findByUsernameCustom(String username) {
        QUser qUser = QUser.user;
        JPAQuery<?> query = new JPAQuery<Void>(em);
        List<User> users = query.select(qUser)
                .from(qUser)
                .where(qUser.username.contains(username))
                .fetch();
        return users;

    }

    @Override
    public List<User> findByUsernameJdbc(String username) {
        String sql = "SELECT * FROM CUSTOMER";

        List<User> users = jdbcTemplate.query(
                "SELECT * FROM USER WHERE username like ?",
                new Object[]{"%" + username + "%"},
                new BeanPropertyRowMapper(User.class));
        return users;
    }
}
