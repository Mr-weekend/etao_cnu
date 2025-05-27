package cn.etaocnu.cloud.order.service;

import cn.etaocnu.cloud.model.vo.NewOrderVo;
import cn.etaocnu.cloud.model.vo.OrderVo;

import java.util.List;

public interface OrderInfoService {
    // 创建订单
    Boolean createOrder(NewOrderVo newOrderVo);
    // 获取订单详细信息
    OrderVo getOrderInfoById(int orderId);
    // 获取用户订单列表
    List<OrderVo> getOrderListByUserId(int userId);
    // 获取用户买到的订单列表
    List<OrderVo> getOrderListAsBuyer(int userId, int page, int size);
    // 获取用户卖出的订单列表
    List<OrderVo> getOrderListAsSeller(int userId, int page, int size);
    // 搜索用户买到的订单列表
    List<OrderVo> searchOrderAsBuyer(String keyword, int userId, int page, int size);
    // 搜索用户卖出的订单列表
    List<OrderVo> searchOrderAsSeller(String keyword, int userId, int page, int size);
    // 获取用户买到的待评价订单列表
    List<OrderVo> getUncommentOrderAsBuyer(int userId, int page, int size);
    // 获取用户卖出的待评价订单列表
    List<OrderVo> getUncommentOrderAsSeller(int userId, int page, int size);
    // 用户确认收货
    Boolean completedOrder(int userId, int orderId);
    // 用户取消订单
    Boolean cancelOrder(int userId, int orderId);
    // 用户删除订单
    Boolean deleteOrder(int userId, int orderId);
    // 获取买到的订单总数
    int countOrderAsBuyer(int userId);
    // 获取卖出的订单总数
    int countOrderAsSeller(int userId);
    // 获取总订单数
    int countOrder(int userId);
    // 关键字搜索用户买到的订单总数
    int countSearchOrderAsBuyer(String keyword, int userId);
    // 关键字搜索用户卖出的订单总数
    int countSearchOrderAsSeller(String keyword, int userId);
    // 获取用户买到的待评价订单数
    int countUncommentOrderAsBuyer(int userId);
    // 获取用户卖出的待评价订单数
    int countUncommentOrderAsSeller(int userId);
    // 获取用户待评价订单总数
    int countUncommentOrder(int userId);
}
