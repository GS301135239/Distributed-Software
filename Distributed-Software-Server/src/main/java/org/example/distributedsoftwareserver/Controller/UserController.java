package org.example.distributedsoftwareserver.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.distributedsoftwareserver.Common.Result;
import org.example.distributedsoftwareserver.Entity.DTO.LoginDTO;
import org.example.distributedsoftwareserver.Entity.DTO.RegisterDTO;
import org.example.distributedsoftwareserver.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        return userService.register(registerDTO, request);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        return userService.login(loginDTO, request);
    }
}
