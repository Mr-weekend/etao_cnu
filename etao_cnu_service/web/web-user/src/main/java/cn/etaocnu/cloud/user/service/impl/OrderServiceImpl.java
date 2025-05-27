package cn.etaocnu.cloud.user.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.NewOrderVo;
import cn.etaocnu.cloud.model.vo.OrderVo;
import cn.etaocnu.cloud.order.client.OrderFeignClient;
import cn.etaocnu.cloud.user.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    
    @Resource
    private OrderFeignClient orderFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<Boolean> createOrder(String token, NewOrderVo newOrderVo) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return orderFeignClient.createOrder(newOrderVo);
    }

    @Override
    public Result<OrderVo> getOrderInfoById(String token, int orderId) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return orderFeignClient.getOrderInfo(orderId);
    }

    @Override
    public Result<Map<String, Object>> getOrderListByUserId(String token, int page, int size) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.getOrderList(userId, page, size);
        }catch (Exception e){
            log.error("获取订单列表失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getOrderListAsBuyer(String token, int page, int size) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.getOrderListAsBuyer(userId, page, size);
        }catch (Exception e){
            log.error("获取用户买到的订单列表失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getOrderListAsSeller(String token, int page, int size) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.getOrderListAsSeller(userId, page, size);
        }catch (Exception e){
            log.error("获取用户卖出的订单列表失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> searchOrderAsBuyer(String keyword, String token, int page, int size) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.searchOrderAsBuyer(keyword, userId, page, size);
        }catch (Exception e){
            log.error("关键字搜索用户买到的订单列表失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> searchOrderAsSeller(String keyword, String token, int page, int size) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.searchOrderAsSeller(keyword, userId, page, size);
        }catch (Exception e){
            log.error("关键字搜索用户卖出的订单失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> completedOrder(String token, int orderId) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.completedOrder(orderId, userId);
        }catch (Exception e){
            log.error("确认收货失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> cancelOrder(String token, int orderId) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.cancelOrder(orderId, userId);
        }catch (Exception e){
            log.error("取消订单失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteOrder(String token, int orderId) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.deleteOrder(orderId, userId);
        }catch (Exception e){
            log.error("删除订单失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getUncommentOrderAsBuyer(String token, int page, int size) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.getUncommentOrderAsBuyer(userId, page, size);
        }catch (Exception e){
            log.error(e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getUncommentOrderAsSeller(String token, int page, int size) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.getUncommentOrderAsSeller(userId, page, size);
        }catch (Exception e){
            log.error(e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Integer> countUncommentOrderAsBuyer(String token) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.countUncommentOrderAsBuyer(userId);
        }catch (Exception e){
            log.error(e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Integer> countUncommentOrderAsSeller(String token) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.countUncommentOrderAsSeller(userId);
        }catch (Exception e){
            log.error(e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Integer> countUncommentOrder(String token) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            return orderFeignClient.countUncommentOrder(userId);
        }catch (Exception e){
            log.error(e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Integer> countOrderAsBuyer(String token) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            Result<Integer> result = orderFeignClient.countOrderAsBuyer(userId);
            if (result.getCode() != 200){
                return Result.fail(result.getMessage());
            }
            return Result.success(result.getMessage(), result.getData());
        }catch (Exception e){
            log.error("统计用户买到的订单数量失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Integer> countOrderAsSeller(String token) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            Result<Integer> result = orderFeignClient.countOrderAsSeller(userId);
            if (result.getCode() != 200){
                return Result.fail(result.getMessage());
            }
            return Result.success(result.getMessage(), result.getData());
        }catch (Exception e){
            log.error("统计用户卖出的订单数量失败！");
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Integer> countOrder(String token) {
        try{
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("token过期，请重新登录！");
            }
            Result<Integer> result = orderFeignClient.countOrder(userId);
            if (result.getCode() != 200){
                return Result.fail(result.getMessage());
            }
            return Result.success(result.getMessage(), result.getData());
        }catch (Exception e){
            log.error("统计订单数量失败！");
            return Result.fail(e.getMessage());
        }
    }

    private Integer getUserIdFromToken(String token) {
        String userId = redisTemplate.opsForValue().get("user:login:" + token);
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return Integer.parseInt(userId);
    }
} 