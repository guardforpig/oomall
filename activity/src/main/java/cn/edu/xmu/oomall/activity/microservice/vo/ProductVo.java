package cn.edu.xmu.oomall.activity.microservice.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductVo{
    @ApiModelProperty(value = "货品id")
    private Long productId;
    @ApiModelProperty(value = "货品名称")
    private String name;
    @ApiModelProperty(value = "图片链接")
    private String imageUrl;

}