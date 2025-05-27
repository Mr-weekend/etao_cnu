package cn.etaocnu.cloud.collection.service.impl;

import cn.etaocnu.cloud.collection.mapper.CollectionMapper;
import cn.etaocnu.cloud.collection.service.CollectionInfoService;
import cn.etaocnu.cloud.model.entity.Collection;
import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.vo.CollectionVo;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import cn.etaocnu.cloud.model.vo.UserVo;
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
public class CollectionInfoServiceImpl implements CollectionInfoService {
    @Resource
    private CollectionMapper collectionMapper;

    @Override
    public Boolean addCollection(int userId, int goodsId) {
        try{
            Example example = new Example(Collection.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", userId).andEqualTo("goodsId", goodsId);
            if (collectionMapper.selectCountByExample(example) > 0) {
                //有收藏记录
                return false;
            }
            Collection collection = new Collection();
            collection.setUserId(userId);
            collection.setGoodsId(goodsId);
            collection.setCreatedAt(new Date());
            //将goods的collect_count + 1
            collectionMapper.addCollectionCount(goodsId);
            return collectionMapper.insert(collection) > 0;
        }catch (Exception e){
            log.error("添加收藏失败！", e);
            return false;
        }
    }

    @Override
    public Boolean cancelCollection(int userId, int goodsId) {
        try{
            Example example = new Example(Collection.class);
            example.createCriteria()
                    .andEqualTo("userId", userId)
                    .andEqualTo("goodsId", goodsId);
            //将goods的collect_count - 1
            collectionMapper.reduceCollectionCount(goodsId);
            return collectionMapper.deleteByExample(example) > 0;
        }catch (Exception e){
            log.error("取消收藏失败！", e);
            return false;
        }
    }

    @Override
    public Boolean batchCancelCollection(int userId, List<Integer> goodsIds) {
        try {
            Example example = new Example(Collection.class);
            example.createCriteria()
                    .andEqualTo("userId", userId)
                    .andIn("goodsId", goodsIds);
            //批量减少goods的collect_count
            collectionMapper.batchReduceCollectionCount(goodsIds);
            return collectionMapper.deleteByExample(example) > 0;
        } catch (Exception e) {
            log.error("批量取消收藏失败！", e);
            return false;
        }
    }

    @Override
    public Boolean clearCollection(int userId) {
        try {
            Example example = new Example(Collection.class);
            example.createCriteria()
                    .andEqualTo("userId", userId);
            List<Collection> collectionList = collectionMapper.selectByExample(example);
            if (collectionList.isEmpty()) {
                return false;
            }
            List<Integer> goodsIds = new ArrayList<>();
            for (Collection collection : collectionList) {
                goodsIds.add(collection.getGoodsId());
            }
            //批量减少goods的collect_count
            collectionMapper.batchReduceCollectionCount(goodsIds);
            return collectionMapper.deleteByExample(example) > 0;
        } catch (Exception e) {
            log.error("清空收藏失败！", e);
            return false;
        }
    }

    @Override
    public List<CollectionVo> getCollectionList(int userId, int page, int size) {
        try{
            Example example = new Example(Collection.class);
            example.createCriteria().andEqualTo("userId", userId);
            example.orderBy("createdAt").desc();

            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);

            //查询用户收藏的闲置物品列表
            List<Collection> collectList = collectionMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<CollectionVo> collectionVoList = new ArrayList<>();
            //转为Vo对象
            for (Collection collection : collectList) {
                Goods goods = collectionMapper.getGoodsById(collection.getGoodsId());
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setImageUrls(collectionMapper.getGoodsImageList(collection.getGoodsId()));

                UserVo userVo = collectionMapper.getUserInfo(goods.getUserId());
                goodsVo.setPublisher(userVo);

                goodsVo.setCategoryName(collectionMapper.getCategoryName(collection.getGoodsId()));

                CollectionVo collectionVo = new CollectionVo();
                collectionVo.setGoodsInfo(goodsVo);
                collectionVo.setCreatedAt(collection.getCreatedAt());
                collectionVoList.add(collectionVo);
            }

            return collectionVoList;
        }catch (Exception e){
            log.error("获取收藏列表失败！");
            return null;
        }

    }

    @Override
    public int countUserCollection(int userId) {
        try {
            Example example = new Example(Collection.class);
            example.createCriteria()
                    .andEqualTo("userId", userId);
            return collectionMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("统计用户收藏数量失败", e);
            return -1;
        }
    }

    @Override
    public int isCollectionExist(int goodsId, int userId) {
        try{
            return collectionMapper.isCollectionExist(goodsId, userId) != null ? 1 : 0;
        }catch (Exception e){
            log.error("获取用户是否收藏闲置物品失败！");
            return -1;
        }
    }

}