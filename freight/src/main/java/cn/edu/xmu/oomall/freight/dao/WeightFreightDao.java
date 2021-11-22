package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.mapper.WeightFreightPoMapper;
import cn.edu.xmu.oomall.freight.model.bo.WeightFreight;
import cn.edu.xmu.oomall.freight.model.po.WeightFreightPo;
import cn.edu.xmu.oomall.freight.model.po.WeightFreightPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyi guo
 * @date 2021/11/16
 */
@Repository
public class WeightFreightDao {

    @Autowired
    private WeightFreightPoMapper weightFreightPoMapper;

    /**
     * 管理员新增重量模板明细
     * @param weightFreightPo,userId,userName
     * @return ReturnObject
     */
    public ReturnObject addWeightItems(WeightFreightPo weightFreightPo, Long userId, String userName){

        try {
            Common.setPoCreatedFields(weightFreightPo, userId, userName);

            WeightFreightPoExample example = new WeightFreightPoExample();
            WeightFreightPoExample.Criteria criteria = example.createCriteria();
            criteria.andFreightModelIdEqualTo(weightFreightPo.getFreightModelId());
            criteria.andRegionIdEqualTo(weightFreightPo.getRegionId());
            List<WeightFreightPo> list = weightFreightPoMapper.selectByExample(example);
            if (list!=null && list.size() > 0) {
                return new ReturnObject(ReturnNo.FREIGHT_REGIONEXIST);
            }

            weightFreightPoMapper.insertSelective(weightFreightPo);
            WeightFreightPo newWeightFreightPo = weightFreightPoMapper.selectByPrimaryKey(weightFreightPo.getId());
            return new ReturnObject(Common.cloneVo(newWeightFreightPo, WeightFreight.class));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 店家或管理员查询某个重量运费模板的明细
     *
     * @param freightModelId
     * @return ReturnObject
     */
    public ReturnObject getWeightItems(Long freightModelId, Integer page, Integer pageSize) {
        try {
            PageHelper.startPage(page, pageSize);

            WeightFreightPoExample example=new WeightFreightPoExample();
            WeightFreightPoExample.Criteria criteria=example.createCriteria();
            criteria.andFreightModelIdEqualTo(freightModelId);
            List<WeightFreightPo> weightFreightPoList = weightFreightPoMapper.selectByExample(example);
            List<WeightFreight> weightFreightBoList = new ArrayList<>();
            for (WeightFreightPo wfPo : weightFreightPoList) {
                weightFreightBoList.add((WeightFreight) Common.cloneVo(wfPo,WeightFreight.class));
            }

            PageInfo<WeightFreight> pageInfo = PageInfo.of(weightFreightBoList);
            return new ReturnObject(pageInfo);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 店家或管理员查询某个重量运费模板的明细,不分页
     *
     * @param freightModelId
     * @return ReturnObject
     */
    public ReturnObject getAllWeightItems(Long freightModelId) {
        try {

            WeightFreightPoExample example=new WeightFreightPoExample();
            WeightFreightPoExample.Criteria criteria=example.createCriteria();
            criteria.andFreightModelIdEqualTo(freightModelId);
            List<WeightFreightPo> weightFreightPoList = weightFreightPoMapper.selectByExample(example);
            List<WeightFreight> weightFreightBoList = new ArrayList<>();
            for (WeightFreightPo wfPo : weightFreightPoList) {
                weightFreightBoList.add((WeightFreight) Common.cloneVo(wfPo,WeightFreight.class));
            }
            return new ReturnObject(weightFreightBoList);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 店家或管理员修改重量运费模板明细
     * @param weightFreightPo,userId,userName
     * @return ReturnObject
     */
    public ReturnObject updateWeightItems(WeightFreightPo weightFreightPo, Long userId, String userName){

        try {
            Common.setPoModifiedFields(weightFreightPo,userId,userName);

            WeightFreightPo wp = weightFreightPoMapper.selectByPrimaryKey(weightFreightPo.getId());
            if (wp == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(weightFreightPo.getRegionId()!=null) {
                WeightFreightPoExample example = new WeightFreightPoExample();
                WeightFreightPoExample.Criteria criteria = example.createCriteria();
                criteria.andRegionIdEqualTo(weightFreightPo.getRegionId());
                List<WeightFreightPo> list = weightFreightPoMapper.selectByExample(example);
                if (list!=null && list.size() > 0) {
                    if (!list.get(0).getId().equals(weightFreightPo.getId())) {
                        return new ReturnObject(ReturnNo.FREIGHT_REGIONSAME);
                    }
                }
            }

            weightFreightPoMapper.updateByPrimaryKeySelective(weightFreightPo);
            return new ReturnObject();
        } catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 店家或管理员删除重量运费模板明细
     * @param id
     * @return ReturnObject
     */
    public ReturnObject deleteWeightItems(Long id){
        try {
            int ret = weightFreightPoMapper.deleteByPrimaryKey(id);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                return new ReturnObject(ReturnNo.OK);
            }
        } catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

}
