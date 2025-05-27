package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.model.vo.OrderVo;

import java.util.List;

public interface SysOrderInfoService {
    // 查看所有订单信息
    List<OrderVo> getAllOrderList(int page,int size);
    // 获取订单数量
    int countOrder();
    // 按闲置物品关键字查询订单信息
    List<OrderVo> searchOrderByGoodsKey(String goodsKeyword, int page, int size);
    // 获取按闲置物品关键字查询订单信息数量
    int countOrderByGoodsKey(String goodsKeyword);
    // 按用户ID查询用户订单信息
    List<OrderVo> searchOrderByUserId(int userId, int page, int size);
    // 获取按用户ID查询用户订单信息数量
    int countOrderByUserId(int userId);
}
