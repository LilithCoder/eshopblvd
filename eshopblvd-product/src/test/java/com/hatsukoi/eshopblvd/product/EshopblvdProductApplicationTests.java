package com.hatsukoi.eshopblvd.product;

import com.hatsukoi.eshopblvd.product.entity.Brand;
import com.hatsukoi.eshopblvd.product.service.BrandService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EshopblvdProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        Brand brand = brandService.selectBrandById(1L);
        System.out.println("获取到的品牌是：" + brand);
        CommonPageInfo<Brand> brandCommonPageInfo = brandService.queryBrandsByShowStatus(2, 2, (byte) 1);
        System.out.println("目前能显示的第" + brandCommonPageInfo.getCurrPage() + "页的品牌是：" + brandCommonPageInfo.getListData());
    }
}
