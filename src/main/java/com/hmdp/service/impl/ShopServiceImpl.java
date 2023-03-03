package com.hmdp.service.impl;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.json.JSONUtil;
import com.hmdp.constant.RedisKeyConstants;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author linxuan.li
 * @since 2021-12-22
 */
@Service
@Log4j2
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Shop getShopById(Long id){
        String key = RedisKeyConstants.SHOP_PRE_FIX + id;
        String shopString = stringRedisTemplate.opsForValue().get(key);
        Shop shop;
        if (Objects.nonNull(shopString)){
            try {
                shop = JSONUtil.toBean(shopString, Shop.class);
            } catch (ConvertException e) {
                throw new ConvertException(e);
            }
        }else{
            shop = getById(id);
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shop),3, TimeUnit.MINUTES);
        }
        return shop;
    }

    @Override
    public Boolean updateShopById(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return false;
        }
        // 1.更新数据库
        updateById(shop);
        // 2.删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return true;
    }

}
