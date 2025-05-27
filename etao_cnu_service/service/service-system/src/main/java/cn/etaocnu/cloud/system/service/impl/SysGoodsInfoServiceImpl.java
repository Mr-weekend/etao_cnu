package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.entity.GoodsImage;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import cn.etaocnu.cloud.model.vo.UserVo;
import cn.etaocnu.cloud.system.mapper.SysCategoryMapper;
import cn.etaocnu.cloud.system.mapper.SysGoodsImageMapper;
import cn.etaocnu.cloud.system.mapper.SysGoodsMapper;
import cn.etaocnu.cloud.system.service.SysGoodsInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysGoodsInfoServiceImpl implements SysGoodsInfoService {

    @Resource
    private SysGoodsMapper sysGoodsMapper;

    @Resource
    private SysGoodsImageMapper sysGoodsImageMapper;

    @Resource
    private SysCategoryMapper sysCategoryMapper;

    @Override
    public List<GoodsVo> getAllGoodsList(String status, int page, int size) {
        try{
            Example example = new Example(Goods.class);
            Example.Criteria criteria = example.createCriteria();
            // 如果指定了状态，按状态过滤
            if (StringUtils.hasText(status)) {
                criteria.andEqualTo("status", status);
            }
            example.orderBy("publishTime").desc();

            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);
            // 执行查询
            List<Goods> goodsList = sysGoodsMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<GoodsVo> goodsVoList = new ArrayList<>();

            // 转换为VO对象并添加对应信息
            for (Goods goods : goodsList) {
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                // 查询闲置物品图片
                Example imageExample = new Example(GoodsImage.class);
                imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
                List<GoodsImage> images = sysGoodsImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(GoodsImage::getImageUrl)
                        .collect(Collectors.toList());
                goodsVo.setImageUrls(imageUrls);
                // 设置分类名
                goodsVo.setCategoryName(sysCategoryMapper.selectByPrimaryKey(goods.getCategoryId()).getCategoryName());
                // 设置发布者信息
                UserVo userVo = sysGoodsMapper.getGoodsPublisher(goods.getUserId());
                goodsVo.setPublisher(userVo);

                goodsVoList.add(goodsVo);
            }
            return goodsVoList;
        }catch (Exception e){
            log.error("获取闲置物品列表失败！", e);
            return null;
        }
    }

    @Override
    public List<GoodsVo> SearchGoodsByKeyword(String keyword, String status, Integer categoryId, int page, int size) {
        try{
            Example example = new Example(Goods.class);
            Example.Criteria criteria = example.createCriteria();
            // 添加闲置物品描述模糊查询条件
            if (keyword != null && !keyword.trim().isEmpty()) {
                criteria.andLike("description", "%" + keyword + "%");
            }

            //按条件过滤
            if (StringUtils.hasText(status) && categoryId != null) {
                criteria.andEqualTo("status", status).andEqualTo("categoryId", categoryId);
            }else if (StringUtils.hasText(status)){
                criteria.andEqualTo("status", status);
            }else {
                criteria.andEqualTo("categoryId", categoryId);
            }
            example.orderBy("publishTime").desc();
            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);
            // 执行查询
            List<Goods> goodsList = sysGoodsMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<GoodsVo> goodsVoList = new ArrayList<>();

            // 转换为VO对象并添加对应信息
            for (Goods goods : goodsList) {
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                // 查询闲置物品图片
                Example imageExample = new Example(GoodsImage.class);
                imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
                List<GoodsImage> images = sysGoodsImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(GoodsImage::getImageUrl)
                        .collect(Collectors.toList());
                goodsVo.setImageUrls(imageUrls);
                // 设置分类名
                goodsVo.setCategoryName(sysCategoryMapper.selectByPrimaryKey(goods.getCategoryId()).getCategoryName());

                // 设置发布者信息
                UserVo userVo = sysGoodsMapper.getGoodsPublisher(goods.getUserId());
                goodsVo.setPublisher(userVo);

                goodsVoList.add(goodsVo);
            }
            return goodsVoList;
        }catch (Exception e){
            log.error("关键字查询闲置物品列表失败！");
            return null;
        }
    }
    
    @Override
    public Boolean deleteGoodsById(int goodsId) {
        try{
            return sysGoodsMapper.deleteByPrimaryKey(goodsId) > 0;
        }catch (Exception e){
            log.error("删除闲置物品失败！", e);
            return false;
        }
    }

    @Override
    public int countGoods(String status) {
        try{
            Example example = new Example(Goods.class);
            Example.Criteria criteria = example.createCriteria();
            if (StringUtils.hasText(status)) {
                criteria.andEqualTo("status", status);
            }
            return sysGoodsMapper.selectCountByExample(example);
        }catch (Exception e){
            log.error("统计闲置物品数量失败！", e);
        }
        return 0;
    }

    @Override
    public int countGoodsByKeyword(String keyword, String status, Integer categoryId) {
        try{
            Example example = new Example(Goods.class);
            Example.Criteria criteria = example.createCriteria();
            // 添加闲置物品描述模糊查询条件
            if (keyword != null && !keyword.trim().isEmpty()) {
                criteria.andLike("description", "%" + keyword + "%");
            }
            if (StringUtils.hasText(status) && categoryId != null) {
                criteria.andEqualTo("status", status).andEqualTo("categoryId", categoryId);
            }else if (StringUtils.hasText(status)){
                criteria.andEqualTo("status", status);
            }else{
                criteria.andEqualTo("categoryId", categoryId);
            }
            return sysGoodsMapper.selectCountByExample(example);
        }catch (Exception e){
            log.error("计算符合条件的闲置物品数量失败！", e);
            return 0;
        }
    }
}
