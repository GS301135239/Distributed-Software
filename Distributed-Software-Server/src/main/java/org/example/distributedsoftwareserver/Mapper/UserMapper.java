package org.example.distributedsoftwareserver.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.distributedsoftwareserver.Entity.Model.User;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User selectUserByUserPhone(String userPhone);
    void updateUserLoginStatus(String userPhone, Integer is_Login);
}
