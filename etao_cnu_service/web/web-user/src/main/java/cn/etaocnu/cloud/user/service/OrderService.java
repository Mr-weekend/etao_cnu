package cn.etaocnu.cloud.user.service;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.NewOrderVo;
import cn.etaocnu.cloud.model.vo.OrderVo;

import java.util.Map;

public interface OrderService {
    // 创建订单
    Result<Boolean> createOrder(String token, NewOrderVo newOrderVo);
    // 获取订单详细信息
    Result<OrderVo> getOrderInfoById(String token,int orderId);
    // 获取订单列表
    Result<Map<String, Object>> getOrderListByUserId(String token, int page, int size);
    // 获取用户买到的订单列表
    Result<Map<String, Object>> getOrderListAsBuyer(String token, int page, int size);
    // 获取用户卖出的订单列表
    Result<Map<String, Object>> getOrderListAsSeller(String token, int page, int size);
    // 搜索用户买到的订单列表
    Result<Map<String, Object>> searchOrderAsBuyer(String keyword, String token, int page, int size);
    // 搜索用户卖出的订单列表
    Result<Map<String, Object>> searchOrderAsSeller(String keyword, String token, int page, int size);
    // 获取用户买到的待评价订单列表
    Result<Map<String, Object>> getUncommentOrderAsBuyer(String token, int page, int size);
    // 获取用户卖出的待评价订单列表
    Result<Map<String, Object>> getUncommentOrderAsSeller(String token, int page, int size);
    // 用户确认收货
    Result<Boolean> completedOrder(String token, int orderId);
    // 用户取消订单
    Result<Boolean> cancelOrder(String token, int orderId);
    // 用户删除订单
    Result<Boolean> deleteOrder(String token, int orderId);
    //获取买到的订单总数
    Result<Integer> countOrderAsBuyer(String token);
    //获取卖出的订单总数
    Result<Integer> countOrderAsSeller(String token);
    //获取总订单数
    Result<Integer> countOrder(String token);
    // 获取用户买到的待评价订单数
    Result<Integer> countUncommentOrderAsBuyer(String token);
    // 获取用户卖出的待评价订单数
    Result<Integer> countUncommentOrderAsSeller(String token);
    // 获取用户待评价订单总数
    Result<Integer> countUncommentOrder(String token);
}