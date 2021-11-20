package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.RedisUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.oomall.freight.model.bo.FreightModel;
import cn.edu.xmu.oomall.freight.model.po.FreightModelPo;
import cn.edu.xmu.oomall.freight.model.po.FreightModelPoExample;
import cn.edu.xmu.oomall.freight.model.vo.FreightModelRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Repository
public class FreightModelDao {
    private Logger log = LoggerFactory.getLogger(FreightModelDao.class);
    @Autowired
    FreightModelPoMapper freightModelPoMapper;

    @Value("${oomall.freight.model.expiretime}")
    private Long freightModelRedisTimeout;

    @Autowired
    private RedisUtil redisUtil;

    private final String defaultFreightModelKey = "defaultFrightModel";
    private final String freightModelKey = "freightModel_";


    private FreightModel getDefaultFreightInternal() {
        try {
            FreightModel defaultFreightModel = (FreightModel) redisUtil.get(defaultFreightModelKey);
            if (defaultFreightModel == null) {
                FreightModelPoExample freightModelPoExample = new FreightModelPoExample();
                FreightModelPoExample.Criteria criteria = freightModelPoExample.createCriteria();
                criteria.andDefaultModelEqualTo((byte) 1);
                List<FreightModelPo> freightModelPoList = freightModelPoMapper.selectByExample(freightModelPoExample);
                FreightModelPo defaultFreightModelPo = freightModelPoList.get(0);

                defaultFreightModel = (FreightModel) Common.cloneVo(defaultFreightModelPo, FreightModel.class);
                redisUtil.set(defaultFreightModelKey, defaultFreightModel, freightModelRedisTimeout);
            }
            return defaultFreightModel;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private void setDefaultFreightInternal(FreightModel defaultFreightModel) {
        try {
            //先删redis
            redisUtil.del(defaultFreightModelKey);

            //更新老的运费模板
            FreightModel oldDefaultFreightModel=getDefaultFreightInternal();
            if(oldDefaultFreightModel!=null)
            {
                oldDefaultFreightModel.setDefaultModel((byte)0);
                freightModelPoMapper.updateByPrimaryKeySelective((FreightModelPo) Common.cloneVo(oldDefaultFreightModel,FreightModelPo.class));
            }
            //更新defaultFreightModel为新的运费模板
            redisUtil.set(defaultFreightModelKey, defaultFreightModel, freightModelRedisTimeout);
            FreightModelPo freightModelPo= (FreightModelPo)Common.cloneVo(defaultFreightModel,FreightModelPo.class);
            freightModelPoMapper.updateByPrimaryKeySelective(freightModelPo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }



    public ReturnObject addFreightModel(FreightModel freightModel) {
        try {
            FreightModelPo freightModelPo = (FreightModelPo) Common.cloneVo(freightModel, FreightModelPo.class);
            freightModelPoMapper.insertSelective(freightModelPo);
            return new ReturnObject(freightModel);
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }


    public ReturnObject showFreightModel(String name, Integer page, Integer pageSize) {
        try {
            List<FreightModelPo> freightModelPoList;
            PageHelper.startPage(page, pageSize);
            //如果name非空那就用name筛选
            if (name != null && !"".equals(name)) {
                FreightModelPoExample example = new FreightModelPoExample();
                FreightModelPoExample.Criteria criteria = example.createCriteria();
                criteria.andNameEqualTo(name);
                freightModelPoList = freightModelPoMapper.selectByExample(example);
            }
            else
            {
                freightModelPoList = freightModelPoMapper.selectByExample(null);
            }
            List<FreightModel> freightModelList = new ArrayList<>();
            for (FreightModelPo fmPo : freightModelPoList) {
                freightModelList.add((FreightModel) Common.cloneVo(fmPo, FreightModel.class));
            }
            PageInfo<FreightModel> pageInfo = new PageInfo<>(freightModelList);
            return new ReturnObject<>(pageInfo);
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.IMG_FORMAT_ERROR);
        }
    }


    /**
     * 获得运费模板详情
     *
     * @param id 运费模板id
     * @return 运费模板
     */
    public ReturnObject showFreightModelById(Long id) {
        try {
            //查redis
            String key = freightModelKey + id;
            FreightModel freightModel;
            freightModel = (FreightModel) redisUtil.get(key);
            if (null != freightModel) {
                return new ReturnObject<>(freightModel);
            }

            FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(id);
            //查不到返回
            if (freightModelPo == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            freightModel = (FreightModel) Common.cloneVo(freightModelPo, FreightModel.class);
            redisUtil.set(key, freightModel, freightModelRedisTimeout);

            return new ReturnObject<>(freightModel);
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    /**
     * 管理员修改运费模板
     *
     * @param freightModel 运费模板
     * @return 运费模板
     */
    public ReturnObject updateFreightModel(FreightModel freightModel) {
        try {
            FreightModelPo freightModelPo = (FreightModelPo) Common.cloneVo(freightModel, FreightModelPo.class);
            int ret = freightModelPoMapper.updateByPrimaryKeySelective(freightModelPo);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                redisUtil.del(freightModelKey + freightModel.getId());
                return new ReturnObject<>();
            }
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 管理员删除运费模板
     *
     * @param id 运费模板id
     * @return 删除结果
     */
    public ReturnObject<FreightModelRetVo> deleteFreightModel(Long id) {
        try {
            int ret = freightModelPoMapper.deleteByPrimaryKey(id);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject<>();
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }


    public ReturnObject getDefaultModel() {
        FreightModel defaultFreightModel= getDefaultFreightInternal();
        if(defaultFreightModel != null) {
           return new ReturnObject(defaultFreightModel);
        }
        return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
    }

    public  ReturnObject setDefaultModel(FreightModel defaultFreightModel) {
        setDefaultFreightInternal(defaultFreightModel);
        return new ReturnObject();
    }
}