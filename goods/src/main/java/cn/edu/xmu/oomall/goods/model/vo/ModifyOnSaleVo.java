package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/15
 */
@Data
public class ModifyOnSaleVo {


    @Min(0)
    private Long price;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime endTime;

    @Min(1)
    private Integer quantity;


}
