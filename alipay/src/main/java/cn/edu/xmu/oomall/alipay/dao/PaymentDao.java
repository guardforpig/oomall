package cn.edu.xmu.oomall.alipay.dao;

import cn.edu.xmu.oomall.alipay.mapper.AlipayPaymentPoMapper;
import cn.edu.xmu.oomall.alipay.model.bo.Payment;
import cn.edu.xmu.oomall.alipay.model.po.AlipayPaymentPo;
import cn.edu.xmu.oomall.alipay.model.po.AlipayPaymentPoExample;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

@Repository
public class PaymentDao {
    @Autowired
    private AlipayPaymentPoMapper alipayPaymentPoMapper;

    private Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    public boolean insertPayment(Payment payment)
    {
        try
        {
            AlipayPaymentPo alipayPaymentPo= (AlipayPaymentPo) Common.cloneVo(payment,AlipayPaymentPo.class);
            alipayPaymentPoMapper.insertSelective(alipayPaymentPo);
            return true;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return false;
        }
    }

    public Payment selectPaymentByOutTradeNo(String outTradeNo)
    {
        try
        {
            AlipayPaymentPoExample alipayPaymentPoExample = new AlipayPaymentPoExample();
            AlipayPaymentPoExample.Criteria criteria = alipayPaymentPoExample.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);

            List<AlipayPaymentPo> alipayPaymentPoList= alipayPaymentPoMapper.selectByExample(alipayPaymentPoExample);
            //一个outTradeNo只可能对应一个AlipayPaymentPo
            if(alipayPaymentPoList.size()!=0)
            {
                return (Payment) cloneVo(alipayPaymentPoList.get(0),Payment.class);
            }
            else{
                //找不到
                return null;
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }

    public boolean updatePayment(Payment payment)
    {
        try{
            AlipayPaymentPo alipayPaymentPo= (AlipayPaymentPo) Common.cloneVo(payment,AlipayPaymentPo.class);
            alipayPaymentPoMapper.updateByPrimaryKeySelective(alipayPaymentPo);
            return true;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return false;
        }
    }
}
