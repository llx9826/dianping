package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.entity.Shop;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author linxuan.li
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {
    Shop getShopById(Long id);


    Boolean updateShopById(Shop shop);
}
