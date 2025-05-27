package cn.etaocnu.cloud.common.result;

import lombok.Data;

@Data
public class Result<T> {
    // 状态码（默认200表示成功）
    private int code;
    // 提示消息
    private String message;
    // 返回数据
    private T data;

    // 无参构造器（JSON序列化需要）
    public Result() {}
    public Result(int code) {
        this.code = code;
    }
    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }
    // 全参构造器
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 成功（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功（带数据），自定义消息
     */

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    /**
     * 失败（自定义错误码和消息）
     */
    public static <T> Result<T> fail() {return new Result<>(201);}
    public static <T> Result<T> fail(String message) {
        return new Result<>(201, message);
    }
    public static <T> Result<T> fail(String message, T data){return new Result<>(201, message, data);}

    public Result<T> code(int code) {
        this.code = code;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    // ------------------ Getter/Setter（JSON序列化需要） ------------------

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}