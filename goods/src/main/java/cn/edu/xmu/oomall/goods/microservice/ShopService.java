package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.oomall.goods.microservice.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Zijun Min
 * @description
 * @createTime 2021/11/29 15:47
 **/
@FeignClient(name = "shop-service",configuration= OpenFeignConfig.class)
public interface ShopService {
    @GetMapping("/shops/{id}")
    InternalReturnObject<SimpleShopVo> getSimpleShopById(@PathVariable("id")Long id);
    /**
     * @author 何赟
     * @date 2021-12-5
     */
    @GetMapping("/category/{id}")
    InternalReturnObject<Category> getCategoryById(@PathVariable("id")Long id);

    /**
     * 需要内部接口，通过cateoryId获取SimpleCategory
     * @return
     */
//    @GetMapping("/internal/categories/{id}")
//    InternalReturnObject<SimpleCategoryVo> getCategoryById(@PathVariable("id") Long id);

//    @GetMapping("/category/{id}")
//    InternalReturnObject<CategoryVo> getCategoryById(@PathVariable("id")Integer id);
    /**
     * 获取分类的详细信息
     * @author 李智樑
     */
    @GetMapping("/internal/categories/{categoryId}")
    InternalReturnObject<CategoryDetailRetVo> getCategoryDetailById(@PathVariable Long categoryId);
}
