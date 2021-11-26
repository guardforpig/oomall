package cn.edu.xmu.oomall.activity.microservice.vo;

import cn.edu.xmu.oomall.activity.model.vo.SimpleAdminUserVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnSaleInfoVo{

    @ApiModelProperty(value = "OnsaleId")
    private Long id;

    @ApiModelProperty(value = "店铺")
    private ShopInfoVO shop;

    @ApiModelProperty(value = "货品")
    private ProductVo product;

    @ApiModelProperty(value = "价格")
    private Long price;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "数量")
    private Long quantity;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "活动id")
    private Long activityId;

    @ApiModelProperty(value = "分享活动id")
    private Long shareActId;

    @ApiModelProperty(value = "创建者")
    private SimpleAdminUserVo creator;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    @ApiModelProperty(value = "修改者")
    private SimpleAdminUserVo modifier;
}