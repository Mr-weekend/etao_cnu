package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.common.result.Result;

import java.util.Map;

public interface SysOrderService {
    // 查看所有订单信息
    Result<Map<String, Object>> getAllOrderList(int page,int size);
    // 按闲置物品关键字查询订单信息
    Result<Map<String, Object>> searchOrderByGoodsKey(String goodsKeyword, int page, int size);
    // 按用户ID查询用户订单信息
    Result<Map<String, Object>> searchOrderByUserId(int userId, int page, int size);
    // 获取订单数量
    Result<Integer> countOrder();


}
