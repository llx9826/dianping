package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.hmdp.constant.RedisKeyConstants;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public List<ShopType>  getShopTypeList(){
        String key = RedisKeyConstants.SHOP_TYPE_PER_FIX;
        String shopTypeString = stringRedisTemplate.opsForValue().get(key);
        List<ShopType> shopTypes;
        if (Objects.nonNull(shopTypeString)){
            shopTypes = JSONUtil.toList(shopTypeString, ShopType.class);
            log.debug("shoptype"+shopTypes);
        }else {
            shopTypes = query().orderByAsc("sort").list();
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shopTypes),3, TimeUnit.MINUTES);
        }
        return shopTypes;
    }
}
