package com.cn.controller;



import com.cn.dao.UserDao;
import com.cn.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class UserController {

    @Autowired(required = false)
    private UserDao userDao;

    @RequestMapping(value ="/phone", method = RequestMethod.GET)
    public User test(){
        return userDao.findByAppid("10524782571");
    }

    //-http://localhost:8080/test/add?name=abc&passwd=123&appid=95955542783
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public User save(@RequestParam("name") String name,
                     @RequestParam(value = "passwd",defaultValue = "0") String passwd,
                     @RequestParam(value = "appid",defaultValue = "0") String appid) {

        User user = new User(name,passwd,appid);
        userDao.addUser(user);
        return userDao.findById(user.getId());
    }



    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void delete(@RequestParam("id") Integer id) {
        userDao.deleteById(id);
    }

}

