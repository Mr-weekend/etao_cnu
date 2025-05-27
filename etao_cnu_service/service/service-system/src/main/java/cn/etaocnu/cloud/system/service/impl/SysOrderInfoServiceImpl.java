package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.entity.Order;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import cn.etaocnu.cloud.model.vo.OrderVo;
import cn.etaocnu.cloud.model.vo.UserVo;
import cn.etaocnu.cloud.system.mapper.SysCategoryMapper;
import cn.etaocnu.cloud.system.mapper.SysOrderMapper;
import cn.etaocnu.cloud.system.service.SysOrderInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SysOrderInfoServiceImpl implements SysOrderInfoService {

    @Resource
    private SysOrderMapper sysOrderMapper;
    @Resource

    private SysCategoryMapper sysCategoryMapper;
    @Override
    public List<OrderVo> getAllOrderList(int page,int size) {
        try{
            Example example = new Example(Order.class);
            example.orderBy("createdAt").desc();
            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);

            List<Order> orderList = sysOrderMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<OrderVo> orderVoList = new ArrayList<OrderVo>();
            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = sysOrderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = sysOrderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(sysOrderMapper.getGoodsImageList(order.getGoodsId()));
                //设置分类信息
                goodsVo.setCategoryName(sysCategoryMapper.selectByPrimaryKey(goods.getCategoryId()).getCategoryName());
                //查询卖家信息
                UserVo sellerInfo = sysOrderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);

                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }
            return orderVoList;
        }catch (Exception e){
            log.info("获取所有订单失败！");
            return null;
        }
    }

    @Override
    public int countOrder() {
        try{
            return sysOrderMapper.selectCountByExample(new Example(Order.class));
        }catch (Exception e){
            log.info("获取订单数量失败！");
            return -1;
        }
    }

    @Override
    public List<OrderVo> searchOrderByGoodsKey(String goodsKeyword, int page, int size) {
        try{
            // 通过闲置物品关键字查询订单
            List<Order> orderList = sysOrderMapper.getOrderByGoodsKey(goodsKeyword, (page - 1) * size, size);
            List<OrderVo> orderVoList = new ArrayList<OrderVo>();
            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = sysOrderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = sysOrderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(sysOrderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = sysOrderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);
                goodsVo.setCategoryName(sysCategoryMapper.selectByPrimaryKey(goods.getCategoryId()).getCategoryName());
                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }
            return orderVoList;
        }catch (Exception e){
            log.info("闲置物品关键字查询订单失败！");
            return null;
        }
    }

    @Override
    public List<OrderVo> searchOrderByUserId(int userId, int page, int size) {
        try{
            // 查询用户所有订单
            List<Order> orderList = sysOrderMapper.getOrderListByUserId(userId, (page - 1) * size, size);
            List<OrderVo> orderVoList = new ArrayList<OrderVo>();
            // 转换为VO对象
            for (Order order : orderList) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order, orderVo);
                //查询买家信息
                UserVo buyerInfo = sysOrderMapper.getUserInfo(order.getBuyerId());
                orderVo.setBuyer(buyerInfo);
                //查询闲置物品信息
                Goods goods = sysOrderMapper.getGoodsById(order.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(sysOrderMapper.getGoodsImageList(order.getGoodsId()));
                //查询卖家信息
                UserVo sellerInfo = sysOrderMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(sellerInfo);
                goodsVo.setCategoryName(sysCategoryMapper.selectByPrimaryKey(goods.getCategoryId()).getCategoryName());
                orderVo.setGoodsInfo(goodsVo);
                orderVoList.add(orderVo);
            }
            return orderVoList;
        }catch (Exception e){
            log.info("获取用户订单信息失败！");
            return null;
        }
    }

    @Override
    public int countOrderByGoodsKey(String goodsKeyword) {
        try{
            return sysOrderMapper.countOrderByGoodsKey(goodsKeyword);
        }catch (Exception e){
            log.info("获取关键字查询闲置物品订单数量失败！");
            return -1;
        }
    }

    @Override
    public int countOrderByUserId(int userId) {
        try{
            return sysOrderMapper.countOrderListByUserId(userId);
        }catch (Exception e){
            log.info("获取用户订单数量失败！");
            return -1;
        }
    }
}
