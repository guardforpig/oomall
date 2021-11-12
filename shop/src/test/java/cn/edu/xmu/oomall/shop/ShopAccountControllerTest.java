package cn.edu.xmu.oomall.shop;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.shop.model.vo.ShopAccountVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author  Xusheng Wang
 * @date  2021-11-11
 */

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ShopAccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void addShopAccountTest() throws Exception {

        ShopAccountVo shopAccountVo= new ShopAccountVo();
        shopAccountVo.setAccount("1115");
        shopAccountVo.setType((byte) 1);
        shopAccountVo.setPriority((byte) 1);
        shopAccountVo.setName("test5");
        String requestJson= JacksonUtil.toJson(shopAccountVo);

        //测试新增记录需要移动优先级的情况
        mvc.perform(post("/shops/2/accounts").contentType("application/json;charset=UTF-8")
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        //测试新增记录不需要移动优先级的情况
        mvc.perform(post("/shops/5/accounts").contentType("application/json;charset=UTF-8")
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void getShopAccountsTest() throws Exception{
        mvc.perform(get("/shops/2/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteShopAccountTest() throws Exception{
        //测试正确删除
        mvc.perform(delete("/shops/2/accounts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andDo(MockMvcResultHandlers.print());
        //测试accountId和shopId无法对应的情况
        mvc.perform(delete("/shops/2/accounts/5"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ReturnNo.RESOURCE_ID_NOTEXIST.getCode()))
                .andDo(MockMvcResultHandlers.print());

    }
}
