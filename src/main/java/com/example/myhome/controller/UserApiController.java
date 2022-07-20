package com.example.myhome.controller;

import com.example.myhome.model.Board;
import com.example.myhome.model.QUser;
import com.example.myhome.model.User;
import com.example.myhome.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
class UserApiController {

    @Autowired
    private UserRepository repository;

    @GetMapping("/users")
    Iterable<User> all(@RequestParam(required = false) String method, @RequestParam(required = false) String text) {
        Iterable<User> users = null;
        if("query".equals(method)){
            users = repository.findByUsernameQuery(text);
        } else if("nativeQuery".equals(method)){
            users = repository.findByUsernameNativeQuery(text);
        } else if("querydsl".equals(method)){
            QUser user = QUser.user;
            /* 조건표현 가능
            BooleanExpression b = user.username.contains(text);
            if(true){
                b = b.and(user.username.eq("HI"));
            }
            users = repository.findAll(b);
            */

            Predicate predicate = user.username.contains(text);
            users = repository.findAll(predicate);
        } else if("querydslCustom".equals(method)){
            users = repository.findByUsernameCustom(text);
        } else if("querydslJdbc".equals(method)){
            users = repository.findByUsernameJdbc(text);
        } else{
            users = repository.findAll();
        }
        //- FetchType.LAZY TEST용
        //log.debug("getBoards().size() 호출전");
        //log.debug("getBoards().size() : {}",users.get(0).getBoards().size());
        //log.debug("getBoards().size() 호출후");


        return users;
    }
    // end::get-aggregate-root[]

    @PostMapping("/users")
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    // Single item

    @GetMapping("/users/{id}")
    User one(@PathVariable Long id) {

        return repository.findById(id).orElse(null);
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {

        return repository.findById(id)
                .map(user -> {
                    //user.setTitle(newUser.getTitle());
                    //user.setContent(newUser.getContent());
                    //orphanRemoval = false 일경우
                    user.setBoards(newUser.getBoards());
                    //orphanRemoval = true 일경우
                    //user.getBoards().clear();
                    //user.getBoards().addAll(newUser.getBoards());
                    for(Board board : user.getBoards()){
                        board.setUser(user);
                    }
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}