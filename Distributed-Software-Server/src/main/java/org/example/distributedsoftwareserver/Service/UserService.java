package org.example.distributedsoftwareserver.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.distributedsoftwareserver.Common.Result;
import org.example.distributedsoftwareserver.Entity.DTO.LoginDTO;
import org.example.distributedsoftwareserver.Entity.DTO.RegisterDTO;
import org.example.distributedsoftwareserver.Entity.Model.User;
import org.example.distributedsoftwareserver.Entity.VO.LoginVO;
import org.example.distributedsoftwareserver.Entity.VO.RegisterVO;
import org.example.distributedsoftwareserver.Mapper.UserMapper;
import org.example.distributedsoftwareserver.Utils.UserPhoneUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Component
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Result register(RegisterDTO registerDTO, HttpServletRequest request) {
        String UserPhone = registerDTO.getUserPhone();
        String UserName = registerDTO.getUserName();
        String UserPassword = registerDTO.getPassword();

        if(!UserPhoneUtil.isValidPhoneNumber(UserPhone)) {
            return Result.error("电话号码为空或不足11位，请重新输入！");
        }

        if(userMapper.selectUserByUserPhone(UserPhone) != null) {
            return Result.error("该电话号码已被注册，请直接登录！");
        }

        if(UserPassword == null || UserPassword.length() < 6) {
            return Result.error("密码不能为空且必须至少6位，请重新输入！");
        }

        if(UserPassword.length() > 20) {
            return Result.error("密码长度不能超过20位，请重新输入！");
        }

        if(UserName == null || UserName.isEmpty()) {
            UserName = "用户" + UserPhone;
        }

        User NewUser = new User();
        NewUser.setUserPhone(UserPhone);
        NewUser.setUserName(UserName);
        NewUser.setPassword(UserPassword);
        NewUser.setIs_Login(0);

        try{
            userMapper.insertUser(NewUser);
            log.info("User registered successfully, UserPhone: {}", NewUser.getUserPhone());
        } catch (Exception e) {
            log.error("Registration failed, exception: {}", e.getMessage());
            return Result.error("注册失败，发生异常，请稍后再试！");
        }

        RegisterVO registerVO = new RegisterVO();
        BeanUtils.copyProperties(NewUser, registerVO);
        return Result.success("注册成功，请返回重新登录！", registerVO);
    }

    public Result login(LoginDTO loginDTO, HttpServletRequest request) {
        String UserPhone = loginDTO.getUserPhone();
        String UserPassword = loginDTO.getPassword();

        if(!UserPhoneUtil.isValidPhoneNumber(UserPhone)) {
            return Result.error("电话号码为空或不足11位，请重新输入！");
        }

        User user = userMapper.selectUserByUserPhone(UserPhone);
        if(user == null) {
            return Result.error("该电话号码未注册，请先注册！");
        }

        if(!user.getPassword().equals(UserPassword)) {
            return Result.error("密码错误，请重新输入！");
        }

        if(user.getIs_Login() == 1) {
            return Result.error("用户已登录，请勿重复登录！");
        }

        try{
            userMapper.updateUserLoginStatus(UserPhone, 1);
            log.info("User logged in successfully, UserPhone: {}", user.getUserPhone());
        }
        catch(Exception e) {
            log.error("Login failed, exception: {}", e.getMessage());
            return Result.error("登录失败，发生异常，请稍后再试！");
        }

        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(user, loginVO);
        return Result.success("登录成功！", loginVO);
    }
}
