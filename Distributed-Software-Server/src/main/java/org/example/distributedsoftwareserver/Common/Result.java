package org.example.distributedsoftwareserver.Common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result <T>{
    private Integer Code;//状态码
    private String Message;//消息
    private T Data;//数据

    public static <T> Result<T> success(T data){
        return new Result<>(200,"success",data);
    }

    public static <T> Result<T> success(String message,T data){
        return new Result<>(200,message,data);
    }

    public static <T> Result<T> error(Integer code,String message){
        return new Result<>(code, message,null);
    }

    public static <T> Result<T> error(String message){
        return new Result<>(500, message,null);
    }
}
