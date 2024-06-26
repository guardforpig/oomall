package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ImgHelper;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.OnSaleGetDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.microservice.ActivityService;
import cn.edu.xmu.oomall.goods.microservice.FreightService;
import cn.edu.xmu.oomall.goods.microservice.ShopService;
import cn.edu.xmu.oomall.goods.microservice.vo.*;
import cn.edu.xmu.oomall.goods.microservice.vo.CategoryDetailRetVo;
import cn.edu.xmu.oomall.goods.microservice.vo.RetShareActivitySpecificInfoVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleCategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopVo;
import cn.edu.xmu.oomall.goods.model.bo.OnSaleGetBo;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Autowired
    private OnSaleGetDao onsaleGetDao;

    @Autowired
    private ShopService shopService;

    @Autowired
    private FreightService freightService;

    @Autowired
    private ActivityService activityService;

    @Value("${productservice.webdav.username}")
    private String davUsername;

    @Value("${productservice.webdav.password}")
    private String davPassWord;

    @Value("${productservice.webdav.baseUrl}")
    private String baseUrl;

    @Transactional(readOnly = true,rollbackFor=Exception.class)
    public ReturnObject listProductsByFreightId(Long shopId,Long fid,Integer pageNumber, Integer pageSize)
    {
        if(shopId!=0){
            return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
        }
        return productDao.listProductsByFreightId(fid,pageNumber,pageSize) ;
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject publishProduct(Long shopId,Long productId)
    {
        try{

        ReturnObject<Product> ret=productDao.publishById(productId);
        if(!ret.getCode().equals(ReturnNo.OK))
        {
            return ret;
        }
        Product product= ret.getData();
        ProductVo productVo=cloneVo(product,ProductVo.class);
        return new ReturnObject(productVo);}
        catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject onshelvesProduct(Long shopId,Long productId)
    {
        Product product= productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该货品不属于该商铺");
        }
        ReturnObject ret=productDao.alterProductStates(product,(byte)Product.ProductState.ONSHELF.getCode(),(byte)Product.ProductState.OFFSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK);
        }else{
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject offshelvesProduct(Long shopId,Long productId) {
        Product product = productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该货品不属于该商铺");
        }
        ReturnObject ret = productDao.alterProductStates(product, (byte) Product.ProductState.OFFSHELF.getCode(), (byte) Product.ProductState.ONSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject allowProduct(Long productId) {
        Product product = productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        ReturnObject ret = productDao.alterProductStates(product, (byte) Product.ProductState.OFFSHELF.getCode(), (byte) Product.ProductState.BANNED.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject prohibitProduct(Long productId)
    {
        Product product= productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        ReturnObject ret=productDao.alterProductStates(product,(byte)Product.ProductState.BANNED.getCode(),(byte)Product.ProductState.OFFSHELF.getCode(),(byte)Product.ProductState.ONSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
    @Transactional(readOnly = true)
    public ReturnObject<Object> getProductsOfCategories(Long did, Long cid, Integer page, Integer pageSize) {
        InternalReturnObject categoryReturnObj=null;
        try
        {
            categoryReturnObj = shopService.getCategoryById(cid);
        }catch (Exception e)
        {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
//        if (!categoryReturnObj.getErrno().equals(ReturnNo.OK.getCode())){
//            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
//        }
        Category categoryDetailRetVo=(Category)categoryReturnObj.getData();
        if(categoryDetailRetVo.getPid()==0)
        {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
       return new ReturnObject<>(productDao.getProductsOfCategories(did,cid,page,pageSize));

    }

    /**
     * 获取商品的所有状态
     *
     * @param
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    public ReturnObject getProductStates(){ return productDao.getProductState(); }

    /**
     * 查询商品
     *
     * @param shopId,barCode,page,pageSize
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/11
     */
    @Transactional(rollbackFor = Exception.class,readOnly=true)
    public ReturnObject getAllProducts(Long shopId, String barCode, Integer page, Integer pageSize) {
        return productDao.getAllProducts(shopId, barCode, page, pageSize);
    }

    /**
     * 获取product详细信息(非后台用户调用）
     *
     * @param productId
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(readOnly=true)
    public ReturnObject<ProductRetVo> getProductDetails(Long productId) {
        try{
        ReturnObject ret = productDao.getProductInfo(productId);
        if (ret.getCode() != ReturnNo.OK) {
            return ret;
        }
            OnSalePo onSalePo=null;
        Product product = (Product) ret.getData();
        if (product.getState()== Product.ProductState.ONSHELF.getCode()){
            onSalePo = productDao.getValidOnSale(productId);
            if (onSalePo!=null){
                product.setOnsaleId(onSalePo.getId());
                product.setPrice(onSalePo.getPrice());
                product.setQuantity(onSalePo.getQuantity());
            }
        }

        //查找categoryName
        InternalReturnObject object = shopService.getCategoryDetailById(product.getCategoryId());
        if (!object.getErrno().equals(0)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = cloneVo(object.getData(),SimpleCategoryVo.class);
        product.setCategoryName(categoryVo.getName());
        ProductRetVo vo = (ProductRetVo) cloneVo(product, ProductRetVo.class);
        if(vo.getState()!=Product.ProductState.OFFSHELF.getCode()) {
            ReturnObject returnObject = onsaleGetDao.selectOnSaleByProductId(productId);
            if (returnObject.getCode().equals(ReturnNo.OK)) {
                OnSaleGetBo onSaleGetBo = (OnSaleGetBo) returnObject.getData();
                if (onSaleGetBo != null) {
                    vo.setOnsaleId(onSaleGetBo.getId());
                    vo.setPrice(onSaleGetBo.getPrice());
                    vo.setQuantity(onSaleGetBo.getQuantity());
                }
            }
        }
            if ((onSalePo!=null)&&(onSalePo.getShareActId()!=null)){
                InternalReturnObject<RetShareActivitySpecificInfoVo> shareActivityByShopIdAndId = activityService.getShareActivityByShopIdAndId(onSalePo.getShopId(), onSalePo.getShareActId());
                if (shareActivityByShopIdAndId.getErrno()!=0){
                    return new ReturnObject(shareActivityByShopIdAndId);
                }
                RetShareActivitySpecificInfoVo data = shareActivityByShopIdAndId.getData();
                if (data.getState()==(byte)1){
                    vo.setShareable(true);
                }else {
                    vo.setShareable(false);
                }
            }else {
                vo.setShareable(false);
            }
        return new ReturnObject(vo);}
        catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 将product添加到good中
     *
     * @param
     * @return ReturnObject
     * @author wyg
     * @date 2021/11/10
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject addProductToGood(Long shopId, ProductDetailVo productVo, Long loginUser, String loginUsername) {
        try{
        ProductDraftPo po = (ProductDraftPo) cloneVo(productVo, ProductDraftPo.class);
        po.setProductId(0L);
        po.setShopId(shopId);
        setPoCreatedFields(po,loginUser,loginUsername);

        ReturnObject ret = productDao.newProduct(po);
        if(!ret.getCode().equals(ReturnNo.OK))
        {
            return ret;
        }
        Product product = (Product) ret.getData();

        //查找shopName
        InternalReturnObject object = shopService.getSimpleShopById(product.getShopId());
        if(!object.getErrno().equals(ReturnNo.OK.getCode())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleShopVo simpleShopVo = (SimpleShopVo) cloneVo(object.getData(),SimpleShopVo.class);
        product.setShopName(simpleShopVo.getName());
        //查找categoryName
        InternalReturnObject<Category> object1 = shopService.getCategoryById(product.getCategoryId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        Category categoryVo = (Category) object1.getData();
        product.setCategoryName(categoryVo.getName());

        ProductNewReturnVo vo = (ProductNewReturnVo) cloneVo(product, ProductNewReturnVo.class);
        if (ret.getCode() != ReturnNo.OK) {
            return ret;
        }
        return new ReturnObject(vo);}
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 上传货品图片
     *
     * @param shopId, id, multipartFile
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject upLoadProductImg(Long shopId, Long id, MultipartFile multipartFile, Long loginUser, String loginUsername) {
        ReturnObject ret = productDao.matchProductShop(id,shopId);
        if(ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        Product product = productDao.getProduct(id);
        ReturnObject returnObject;

        try {
            returnObject = ImgHelper.remoteSaveImg(multipartFile, 2, davUsername, davPassWord, baseUrl);
            //文件上传错误
            if (returnObject.getCode() != ReturnNo.OK) {
                return returnObject;
            }

            //更新数据库
            String oldFilename = product.getImageUrl();
            Product updateProduct = new Product();
            updateProduct.setImageUrl(returnObject.getData().toString());
            updateProduct.setId(product.getId());
            updateProduct.setShopId(shopId);
            setPoModifiedFields(updateProduct,loginUser,loginUsername);

            ReturnObject updateReturnObject = productDao.addDraftProduct(updateProduct,loginUser,loginUsername);

            //数据库更新失败，需删除新增的图片
            if (updateReturnObject.getCode() == ReturnNo.FIELD_NOTVALID || updateReturnObject.getCode() == ReturnNo.INTERNAL_SERVER_ERR) {
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(), davUsername, davPassWord, baseUrl);
                return updateReturnObject;
            }

            //数据库更新成功，删除原来的图片
            if (updateReturnObject.getCode() == ReturnNo.OK) {
                ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassWord, baseUrl);
                return updateReturnObject;
            }

        } catch (IOException e) {
            return new ReturnObject(ReturnNo.FILE_NO_WRITE_PERMISSION);
        }
        return returnObject;
    }

    /**
     * 物理删除Products
     *
     * @param shopId, id
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject deleteDraftProductById(Long shopId, Long id, Long loginUser, String loginUsername) {
        if(shopId!=0){
            ProductDraftPo productDraftPo = productDao.getProductDraft(id);
            if(productDraftPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(shopId.longValue()!=productDraftPo.getShopId()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
        }
        ReturnObject ret = productDao.deleteDraftProductById(id);
        if(ret.getCode()!= ReturnNo.OK){
            return ret;
        }
        return ret;
    }

    /**
     * 添加DraftProduct
     *
     * @param shopId, id, productChangeVo
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject addDraftProduct(Long shopId, Long id, ProductChangeVo productChangeVo, Long loginUser, String loginUsername) {
        Product product = (Product) cloneVo(productChangeVo, Product.class);
        Product product1=productDao.getProduct(id);
        if(product1.getState()==(byte)-1)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        product.setId(id);
        product.setShopId(shopId);
        if(product.getName()==null&&product1.getName()!=null)
        {
            product.setName(product1.getName());
        }
        if(product.getWeight()==null&&product1.getWeight()!=null)
        {
            product.setWeight(product1.getWeight());
        }
        if(product.getSkuSn()==null&&product1.getSkuSn()!=null)
        {
            product.setSkuSn(product1.getSkuSn());
        }
        if(product.getOriginalPrice()==null&&product1.getOriginalPrice()!=null)
        {
            product.setOriginalPrice(product1.getOriginalPrice());
        }
        if(product.getCategoryId()==null&&product1.getCategoryId()!=null)
        {
            product.setCategoryId(product1.getCategoryId());
        }
        if(product.getOriginalPrice()==null&&product1.getOriginalPrice()!=null)
        {
            product.setOriginPlace(product1.getOriginPlace());
        }
        if(product.getBarcode()==null&&product1.getBarcode()!=null)
        {
            product.setBarcode(product1.getBarcode());
        }
        if(product.getUnit()==null&&product1.getUnit()!=null)
        {
            product.setUnit(product1.getUnit());
        }
        ReturnObject ret = productDao.addDraftProduct(product,loginUser,loginUsername);
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateDraftProduct(Long shopId,ProductChangeVo p,Long draftId,Long userId,String userName)
    {
        Product product = (Product) cloneVo(p, Product.class);
        product.setShopId(shopId);
        setPoModifiedFields(product,userId,userName);
        return productDao.updateDraftById(product,draftId);
    }

    @Transactional(rollbackFor = Exception.class,readOnly = true)
    public ReturnObject getDraftById(Long shopId,Long id)
    {
        return productDao.getDraftById(shopId,id);
    }

    @Transactional(rollbackFor = Exception.class,readOnly = true)
    public ReturnObject getDraftProduct(Long shopId,Integer page,Integer pageSize)
    {
        return productDao.getDraftByShopId(shopId,page,pageSize);
    }

    /**
     * 获取某一商铺的product详细信息（后台用户调用）
     *
     * @param shopId,productId
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(readOnly=true)
    public ReturnObject getShopProductDetails(Long shopId, Long productId, Long loginUser , String loginUsername) {
        ReturnObject ret = productDao.getProductInfo(productId);
        if (ret.getCode() != ReturnNo.OK) {
            return ret;
        }
        Product product = (Product) ret.getData();
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        OnSalePo onSalePo = productDao.getValidOnSale(productId);
        if(onSalePo!=null&&onSalePo.getState().equals(OnSaleGetBo.State.ONLINE.getCode()))
        {
            product.setOnsaleId(onSalePo.getId());
        }
        InternalReturnObject object = shopService.getCategoryById(product.getCategoryId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        Category categoryVo = (Category) object.getData();
        product.setCategoryName(categoryVo.getName());

        ProductShopRetVo vo = (ProductShopRetVo) cloneVo(product, ProductShopRetVo.class);
        return new ReturnObject(vo);
    }

    /**
     * 获取Good集合中的Product
     *
     * @param id
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    @Transactional(readOnly=true)
    public ReturnObject<GoodsRetVo> getGoodsProductById(Long id) {
        ReturnObject ret = productDao.getGoodsProductById(id);
        if(ret.getCode() != ReturnNo.OK){
            return ret;
        }
        GoodsRetVo vo = (GoodsRetVo)ret.getData();
        return new ReturnObject(vo);
    }

    /**
     * 将上线态的秒杀商品加载到Redis
     *
     * @param beginTime, endTime
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/16
     */
    @Transactional(readOnly = true)
    public Object loadSecondKillProduct(LocalDateTime beginTime, LocalDateTime endTime) {
            return productDao.loadSecondKillProduct(beginTime,endTime);
    }

    @Transactional(readOnly = true)
    public ReturnObject getFreightModels(Long shopId, Long id, Long loginUser, String loginUsername) {
        ReturnObject ret = productDao.matchProductShop(id,shopId);
        if(ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        Product p = productDao.getProduct(id);
        var freightModelRet = freightService.getFreightModel(shopId,p.getFreightId());
        if (freightModelRet.getErrno() == 0) {
            return new ReturnObject(freightModelRet.getData());
        } else {
            return new ReturnObject(ReturnNo.getByCode(freightModelRet.getErrno()), freightModelRet.getErrmsg());
        }
    }

    @Transactional(rollbackFor= Exception.class)
    public ReturnObject changeFreightModels(Long shopId, Long id,Long fid, Long loginUser, String loginUsername) {
        if (shopId != 0) {
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        }
        var freightModelRet = freightService.getFreightModel(0L, fid);
        if (freightModelRet.getErrno() != 0 || !freightModelRet.getData().getId().equals(fid)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        Product p = new Product();
        p.setId(id);
        p.setFreightId(fid);
        ReturnObject ret = productDao.updateProduct(p);
        return ret;
    }

    /**
     * 根据productId获取种类利率
     * @author 李智樑
     */
    public ReturnObject getCommissionRateByProductId(Long id) {
        Product product = productDao.getProduct(id);

        if (product.getState().equals((byte)-1)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        CategoryDetailRetVo categoryRetVo =
                shopService.getCategoryDetailById(product.getCategoryId()).getData();

        return new ReturnObject(categoryRetVo.getCommissionRatio());
    }

}
