package cn.edu.xmu.oomall.shop.mapper;

import cn.edu.xmu.oomall.shop.model.po.ShopAccountPo;
import cn.edu.xmu.oomall.shop.model.po.ShopAccountPoExample;
import java.util.List;

public interface ShopAccountPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_shop_account
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_shop_account
     *
     * @mbg.generated
     */
    int insert(ShopAccountPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_shop_account
     *
     * @mbg.generated
     */
    int insertSelective(ShopAccountPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_shop_account
     *
     * @mbg.generated
     */
    List<ShopAccountPo> selectByExample(ShopAccountPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_shop_account
     *
     * @mbg.generated
     */
    ShopAccountPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_shop_account
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ShopAccountPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_shop_account
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ShopAccountPo record);
}