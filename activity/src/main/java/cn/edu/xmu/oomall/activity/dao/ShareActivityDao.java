package cn.edu.xmu.oomall.activity.dao;

import cn.edu.xmu.oomall.activity.mapper.ShareActivityPoMapper;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivityStatesBo;
import cn.edu.xmu.oomall.activity.model.po.ShareActivityPo;
import cn.edu.xmu.oomall.activity.model.po.ShareActivityPoExample;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.core.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 12:55
 */
@Repository
public class ShareActivityDao {
    private static final Logger logger = LoggerFactory.getLogger(ShareActivityDao.class);
    @Autowired
    private ShareActivityPoMapper shareActivityPoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${oomall.activity.share.expiretime}")
    private long shareActivityExpireTime;

    /**
     * 显示所有状态的分享活动
     *
     * @param shopId    店铺Id
     * @param shareIds  相关的分享活动id 集合
     * @param beginTime 晚于此开始时间
     * @param endTime   早于此结束时间
     * @param state     分享活动状态
     * @param page      页码
     * @param pageSize  每页数目
     * @return
     */
    public ReturnObject getShareByShopId(Long shopId, List<Long> shareIds, LocalDateTime beginTime,
                                         LocalDateTime endTime, Byte state, Integer page, Integer pageSize) {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        if (shareIds != null && !shareIds.isEmpty()) {
            criteria.andIdIn(shareIds);
        }
        if (beginTime != null) {
            criteria.andBeginTimeGreaterThanOrEqualTo(beginTime);
        }
        if (endTime != null) {
            criteria.andEndTimeLessThanOrEqualTo(endTime);
        }
        if (state != null) {
            criteria.andStateEqualTo(state);
        }
        try {
            PageHelper.startPage(page, pageSize);
            List<ShareActivityPo> shareActivityPos = shareActivityPoMapper.selectByExample(example);
            PageInfo pageInfo = new PageInfo(shareActivityPos);
            List<RetShareActivityListVo> retShareActivityListVos = new ArrayList<>();
            for (ShareActivityPo shareActivityPo : shareActivityPos) {
                //cloneVO
                RetShareActivityListVo retShareActivityListVO = (RetShareActivityListVo) Common.cloneVo(shareActivityPo, RetShareActivityListVo.class);
                retShareActivityListVos.add(retShareActivityListVO);
            }
            pageInfo.setList(retShareActivityListVos);
            return new ReturnObject(pageInfo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 管理员新增分享活动
     *
     * @param createName       创建者姓名
     * @param createId         创建者id
     * @param shopName         商铺名字
     * @param shopId           商铺id
     * @param shareActivityDTO 新增商铺内容
     * @return
     */
    public ReturnObject addShareAct(String createName, Long createId, String shopName,
                                    Long shopId, ShareActivityDTO shareActivityDTO) {
        ShareActivityPo shareActivityPo = new ShareActivityPo();
        shareActivityPo.setName(shareActivityDTO.getName());
        shareActivityPo.setBeginTime(shareActivityDTO.getBeginTime());
        shareActivityPo.setEndTime(shareActivityDTO.getEndTime());
        shareActivityPo.setStrategy(JacksonUtil.toJson(shareActivityDTO.getStrategy()));
        shareActivityPo.setShopId(shopId);
        shareActivityPo.setShopName(shopName);
        shareActivityPo.setState(ShareActivityStatesBo.DRAFT.getCode());
        Common.setPoCreatedFields(shareActivityPo, createId, createName);
        Common.setPoModifiedFields(shareActivityPo, createId, createName);
        try {
            int flag = shareActivityPoMapper.insert(shareActivityPo);
            if (flag == 0) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            //使用clonevo
            RetShareActivityInfoVo retShareActivityInfoVO = (RetShareActivityInfoVo) Common.cloneVo(shareActivityPo, RetShareActivityInfoVo.class);
            if (shareActivityPo.getStrategy() != null) {
                List<StrategyVo> strategyVos = (List<StrategyVo>) JacksonUtil.toObj(shareActivityPo.getStrategy(), new ArrayList<StrategyVo>().getClass());
                retShareActivityInfoVO.setStrategy(strategyVos);
            }
            retShareActivityInfoVO.setShop(new ShopVo(shareActivityPo.getShopId(), shareActivityPo.getShopName()));
            return new ReturnObject(retShareActivityInfoVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 查询分享活动 只显示上线状态的分享活动
     *
     * @param shopId    店铺Id
     * @param shareIds  分享活动id 集合
     * @param beginTime 晚于此开始时间
     * @param endTime   早于此结束时间
     * @param page      页码
     * @param pageSize  每页数目
     * @return
     */
    public ReturnObject getShareActivity(Long shopId, List<Long> shareIds, LocalDateTime beginTime,
                                         LocalDateTime endTime, Integer page, Integer pageSize) {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        if (shopId != null) {
            criteria.andShopIdEqualTo(shopId);
        }
        if (shareIds != null && !shareIds.isEmpty()) {
            criteria.andIdIn(shareIds);
        }
        if (beginTime != null) {
            criteria.andBeginTimeGreaterThanOrEqualTo(beginTime);
        }
        if (endTime != null) {
            criteria.andEndTimeLessThanOrEqualTo(endTime);
        }
        criteria.andStateEqualTo((byte) 1);
        try {
            PageHelper.startPage(page, pageSize);
            List<ShareActivityPo> shareActivityPos = shareActivityPoMapper.selectByExample(example);
            PageInfo pageInfo = new PageInfo(shareActivityPos);
            List<RetShareActivityListVo> retShareActivityListVos = new ArrayList<>();
            for (ShareActivityPo shareActivityPo : shareActivityPos) {
                //cloneVO
                RetShareActivityListVo retShareActivityListVO = (RetShareActivityListVo) Common.cloneVo(shareActivityPo, RetShareActivityListVo.class);
                retShareActivityListVos.add(retShareActivityListVO);
            }
            pageInfo.setList(retShareActivityListVos);
            return new ReturnObject(pageInfo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 查看分享活动详情 只显示上线状态的分享活动
     *
     * @param id 分享活动Id
     * @return
     */
    public ReturnObject getShareActivityById(Long id) {
        String key = "shareactivivybyid_" + id;
        try {
            Serializable serializable = redisUtil.get(key);
            if (serializable != null) {
                RetShareActivityInfoVo ret = JacksonUtil.toObj(serializable.toString(), RetShareActivityInfoVo.class);
                return new ReturnObject(ret);
            }
            ShareActivityPoExample shareActivityPoExample = new ShareActivityPoExample();
            ShareActivityPoExample.Criteria criteria = shareActivityPoExample.createCriteria();
            criteria.andIdEqualTo(id);
            List<ShareActivityPo> shareActivityPos = shareActivityPoMapper.selectByExample(shareActivityPoExample);
            if (shareActivityPos.isEmpty()) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            //使用clonevo
            ShareActivityPo shareActivityPo = shareActivityPos.get(0);
            RetShareActivityInfoVo retShareActivityInfoVO = (RetShareActivityInfoVo) Common.cloneVo(shareActivityPo, RetShareActivityInfoVo.class);
            if (shareActivityPo.getStrategy() != null) {
                List<StrategyVo> strategyVos = (List<StrategyVo>) JacksonUtil.toObj(shareActivityPo.getStrategy(), new ArrayList<StrategyVo>().getClass());
                retShareActivityInfoVO.setStrategy(strategyVos);
            }
            retShareActivityInfoVO.setShop(new ShopVo(shareActivityPo.getShopId(), shareActivityPo.getShopName()));
            //查不到插入redis设置超时时间
            redisUtil.set(key, JacksonUtil.toJson(retShareActivityInfoVO), shareActivityExpireTime);
            return new ReturnObject(retShareActivityInfoVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 查看特定分享活动详情,显示所有状态的分享活动
     *
     * @param shopId 店铺Id
     * @param id     分享活动Id
     * @return
     */
    public ReturnObject getShareActivityByShopIdAndId(Long shopId, Long id) {
        String key = "shareactivivyidandshopid_" + id + "_" + shopId;
        try {
            Serializable serializable = redisUtil.get(key);
            if (serializable != null) {
                RetShareActivitySpecificInfoVo ret = JacksonUtil.toObj(serializable.toString(), RetShareActivitySpecificInfoVo.class);
                return new ReturnObject(ret);
            }
            ShareActivityPoExample shareActivityPoExample = new ShareActivityPoExample();
            ShareActivityPoExample.Criteria criteria = shareActivityPoExample.createCriteria();
            criteria.andIdEqualTo(id);
            criteria.andShopIdEqualTo(shopId);
            List<ShareActivityPo> shareActivityPos = shareActivityPoMapper.selectByExample(shareActivityPoExample);
            if (shareActivityPos.isEmpty()) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ShareActivityPo shareActivityPo = shareActivityPos.get(0);
            RetShareActivitySpecificInfoVo retShareActivitySpecificInfoVO = (RetShareActivitySpecificInfoVo) Common.cloneVo(shareActivityPo, RetShareActivitySpecificInfoVo.class);
            if (shareActivityPo.getStrategy() != null) {
                List<StrategyVo> strategyVos = (List<StrategyVo>) JacksonUtil.toObj(shareActivityPo.getStrategy(), new ArrayList<StrategyVo>().getClass());
                retShareActivitySpecificInfoVO.setStrategy(strategyVos);
            }
            retShareActivitySpecificInfoVO.setShop(new ShopVo(shareActivityPo.getShopId(),shareActivityPo.getShopName()));
            redisUtil.set(key, JacksonUtil.toJson(retShareActivitySpecificInfoVO), shareActivityExpireTime);
            return new ReturnObject(retShareActivitySpecificInfoVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }
}
