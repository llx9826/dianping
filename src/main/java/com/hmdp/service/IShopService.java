package com.hmdp.service;

import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

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
