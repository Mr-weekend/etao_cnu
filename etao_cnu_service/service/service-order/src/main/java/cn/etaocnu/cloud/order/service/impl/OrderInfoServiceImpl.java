package cn.etaocnu.cloud.order.service.impl;

import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.entity.Order;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import cn.etaocnu.cloud.model.vo.NewOrderVo;
import cn.etaocnu.cloud.model.vo.OrderVo;
import cn.etaocnu.cloud.model.vo.UserVo;
import cn.etaocnu.cloud.order.mapper.OrderMapper;
import cn.etaocnu.cloud.order.service.OrderInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderInfoServiceImpl implements OrderInfoService {
    
    @Resource
    private OrderMapper orderMapper;

    @Override
    public Boolean createOrder(NewOrderVo newOrderVo) {
        try {
            // 检查订单是否已存在
            Example example = new Example(Order.class);
            example.createCriteria()
                    .andEqualTo("goodsId", newOrderVo.getGoodsId())
                    .andEqualTo("buyerId", newOrderVo.getBuyerId());
            
            if (orderMapper.selectCountByExample(example) > 0) {
                log.error("订单已存在");
                return false;
            }
            Order order = new Order();
            BeanUtils.copyProperties(newOrderVo, order);
            order.setCreatedAt(new Date());
            //将闲置物品状态设置为售出
            orderMapper.updateStatus(order.getGoodsId(),new Date());
            // 创建订单
            return orderMapper.insertSelective(order) > 0;
        } catch (Exception e) {
            log.error("添加订单失败", e);
            return false;
        }
    }

    @Override
    public OrderVo getOrderInfoById(int orderId) {
        try {
            // 获取订单基本信息
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                return null;
            }
            
            // 转换为VO对象
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(order, orderVo);
            //查询买家信息
            UserVo buyerInfo = orderMapper.getUserInfo(order.getBuyerId());

            //查询闲置物品信息
            Goods goods = orderMapper.getGoodsById(order.getGoodsId());
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(goods, goodsVo);
            goodsVo.setImageUrls(orderMapper.getGoodsImageList(order.getGoodsId()));
            //查询卖家信息
            UserVo sellerInfo = orderMapper.getUserInfo(goods.getUserId());
            goodsVo.setPublisher(sellerInfo);

            orderVo.setBuyer(buyerInfo);
            orderVo.setGoodsInfo(goodsVo);
            return orderVo;
        } catch (Exception e) {
            log.error("获取订单信息失败", e);
            return null;
        }
    }

    @Override
    public List<OrderVo> getOrderListByUserId(int userId) {
        try {
            // 查询用户的所有订单
            Example example = new Example(Order.class);
            example.createCriteria().andEqualTo("buyerId", userId);
            example.orderBy("createdAt").desc();
            
            List<Order> orderList = orderMapper.selectByExample(example);
            List<OrderVo> orderVoList = new ArrayList<>();
            
            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = orderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = orderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(orderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = orderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);

                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }
            
            return orderVoList;
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return null;
        }
    }

    @Override
    public List<OrderVo> getOrderListAsBuyer(int userId, int page, int size) {
        try{
            // 查询作为买家的订单
            Example example = new Example(Order.class);
            example.createCriteria().andEqualTo("buyerId", userId).andEqualTo("buyerDeleted", false);
            example.orderBy("createdAt").desc();
            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);

            List<Order> orderList = orderMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<OrderVo> orderVoList = new ArrayList<>();

            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = orderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = orderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(orderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = orderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);

                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }

            return orderVoList;
        }catch (Exception e){
            log.error("获取用户买到的订单列表失败", e);
            return null;
        }
    }

    @Override
    public List<OrderVo> getOrderListAsSeller(int userId, int page, int size) {
        try{
            // 查询goods表状态为sold的闲置物品信息
            List<Order> orderList = orderMapper.getOrderAsSeller(userId, (page - 1) * size, size);
            List<OrderVo> orderVoList = new ArrayList<>();

            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = orderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = orderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(orderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = orderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);

                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }

            return orderVoList;
        }catch (Exception e){
            log.error("获取用户卖出的订单列表失败", e);
            return null;
        }
    }

    @Override
    public List<OrderVo> searchOrderAsBuyer(String keyword, int userId, int page, int size) {
        try{
            List<Order> orderList = orderMapper.searchOrderAsBuyer(keyword, userId, (page - 1) * size, size);
            List<OrderVo> orderVoList = new ArrayList<>();
            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = orderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = orderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(orderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = orderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);

                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }

            return orderVoList;
        }catch (Exception e){
            log.error("关键字搜索用户买到的订单列表失败", e);
            return null;
        }
    }

    @Override
    public List<OrderVo> searchOrderAsSeller(String keyword, int userId, int page, int size) {
        try{
            List<Order> orderList = orderMapper.searchOrderAsSeller(keyword, userId, (page - 1) * size, size);
            List<OrderVo> orderVoList = new ArrayList<>();
            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = orderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = orderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(orderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = orderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);

                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }

            return orderVoList;
        }catch (Exception e){
            log.error("关键字搜索用户买到的订单列表失败", e);
            return null;
        }
    }

    @Override
    public Boolean completedOrder(int userId, int orderId) {
        try{
            Example example = new Example(Order.class);
            example.createCriteria().andEqualTo("buyerId", userId).andEqualTo("orderId", orderId);
            Order order = new Order();
            order.setStatus("completed");
            order.setCompletedAt(new Date());
            return orderMapper.updateByExampleSelective(order, example) > 0;
        }catch (Exception e){
            log.info("确认收货失败！");
            return false;
        }
    }

    @Override
    public Boolean cancelOrder(int userId, int orderId) {
        try{
            Example example = new Example(Order.class);
            example.createCriteria().andEqualTo("buyerId", userId).andEqualTo("orderId", orderId);
            Order order = new Order();
            order.setStatus("canceled");
            return orderMapper.updateByExampleSelective(order, example) > 0;
        }catch (Exception e){
            log.info("取消订单失败！");
            return false;
        }
    }

    @Override
    public Boolean deleteOrder(int userId, int orderId) {
        try{
            Example example = new Example(Order.class);
            Order order = orderMapper.selectByPrimaryKey(orderId);
            Order deleteOrder = new Order();
            if (order.getBuyerId() == userId) {
                example.createCriteria().andEqualTo("buyerId", userId).andEqualTo("orderId", orderId);
                deleteOrder.setBuyerDeleted((byte) 1);
            }else{
                example.createCriteria().andEqualTo("orderId", orderId);
                deleteOrder.setSellerDeleted((byte) 1);
            }
            return orderMapper.updateByExampleSelective(deleteOrder, example) > 0;
        }catch (Exception e){
            log.info("删除订单失败！");
            return false;
        }
    }

    @Override
    public int countOrderAsBuyer(int userId) {
        try {
            Example example = new Example(Order.class);
            example.createCriteria()
                    .andEqualTo("buyerId", userId).andEqualTo("buyerDeleted", false);
            return orderMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("统计用户买到的订单数量失败！", e);
            return 0;
        }
    }

    @Override
    public int countOrderAsSeller(int userId) {
        try {
            return orderMapper.countOrderAsSeller(userId);
        } catch (Exception e) {
            log.error("统计用户卖出的订单数量失败！", e);
            return 0;
        }
    }

    @Override
    public int countSearchOrderAsBuyer(String keyword, int userId) {
        try {
            return orderMapper.countSearchOrderAsBuyer(keyword, userId);
        } catch (Exception e) {
            log.error("统计搜索用户买到的订单数量失败！", e);
            return 0;
        }
    }

    @Override
    public int countSearchOrderAsSeller(String keyword, int userId) {
        try {
            return orderMapper.countSearchOrderAsSeller(keyword, userId);
        } catch (Exception e) {
            log.error("统计搜索用户卖出的订单数量失败！", e);
            return 0;
        }
    }

    @Override
    public int countOrder(int userId) {
        try {
            Example example = new Example(Order.class);
            example.createCriteria()
                    .andEqualTo("buyerId", userId);
            return orderMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("统计订单数量失败", e);
            return 0;
        }
    }

    @Override
    public List<OrderVo> getUncommentOrderAsBuyer(int userId, int page, int size) {
        try{
            List<Order> orderList = orderMapper.getUnCommentOrderAsBuyer(userId, (page - 1) * size, size);
            List<OrderVo> orderVoList = new ArrayList<>();

            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = orderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = orderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(orderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = orderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);

                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }

            return orderVoList;
        }catch (Exception e){
            log.error("获取用户买到的待评价订单列表失败", e);
            return null;
        }
    }

    @Override
    public List<OrderVo> getUncommentOrderAsSeller(int userId, int page, int size) {
        try{
            List<Order> orderList = orderMapper.getUnCommentOrderAsSeller(userId, (page - 1) * size, size);
            List<OrderVo> orderVoList = new ArrayList<>();

            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = orderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = orderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(orderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = orderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);

                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }
            return orderVoList;
        }catch (Exception e){
            log.error("获取用户卖出的待评价订单列表失败", e);
            return null;
        }
    }

    @Override
    public int countUncommentOrderAsBuyer(int userId) {
        try {
            return orderMapper.countUnCommentOrderAsBuyer(userId);
        } catch (Exception e) {
            log.error("统计用户买到的待评价订单数量失败！", e);
            return 0;
        }
    }

    @Override
    public int countUncommentOrderAsSeller(int userId) {
        try {
            return orderMapper.countUnCommentOrderAsSeller(userId);
        } catch (Exception e) {
            log.error("统计用户卖出的待评价订单数量失败！", e);
            return 0;
        }
    }

    @Override
    public int countUncommentOrder(int userId) {
        try {
            return orderMapper.countUncommentOrder(userId);
        } catch (Exception e) {
            log.error("统计用户待评价订单数量失败！", e);
            return 0;
        }
    }
}
