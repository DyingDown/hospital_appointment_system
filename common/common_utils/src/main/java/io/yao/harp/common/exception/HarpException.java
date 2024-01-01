package io.yao.harp.common.exception;
import io.swagger.v3.oas.annotations.media.Schema;
import io.yao.harp.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义全局异常类
 *
 * @author qy
 */
@Data
@Schema(description = "Custom Global Exception Class")
public class HarpException extends RuntimeException {

    @Schema(description = "Exception Status Code")
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public HarpException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public HarpException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "YyghException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
