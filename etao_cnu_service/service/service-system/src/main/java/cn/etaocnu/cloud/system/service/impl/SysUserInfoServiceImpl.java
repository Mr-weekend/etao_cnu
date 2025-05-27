package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.model.entity.User;
import cn.etaocnu.cloud.model.entity.UserAuth;
import cn.etaocnu.cloud.model.vo.UserAuthVo;
import cn.etaocnu.cloud.system.mapper.SysUserAuthMapper;
import cn.etaocnu.cloud.system.mapper.SysUserMapper;
import cn.etaocnu.cloud.system.service.SysUserInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysUserInfoServiceImpl implements SysUserInfoService {
    
    @Resource
    private SysUserMapper sysUserMapper;
    
    @Resource
    private SysUserAuthMapper sysUserAuthMapper;

    @Override
    public List<User> getUserList(int page, int size, String keyword) {
        try {
            Example example = new Example(User.class);
            
            // 添加查询条件（如果有关键字）
            if (StringUtils.hasText(keyword)) {
                Example.Criteria criteria = example.createCriteria();
                criteria.orLike("userName", "%" + keyword + "%");
            }
            // 排序（按用户ID降序）
            example.orderBy("userId").desc();
            
            // 分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);
            
            // 执行查询
            List<User> users = sysUserMapper.selectByExampleAndRowBounds(example, rowBounds);

            return users;
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return null;
        }
    }

    @Override
    public int countUserList(String keyword) {
        try {
            Example example = new Example(User.class);
            
            // 添加查询条件（如果有关键字）
            if (StringUtils.hasText(keyword)) {
                Example.Criteria criteria = example.createCriteria();
                criteria.orLike("userName", "%" + keyword + "%");
            }
            
            // 执行计数查询
            return sysUserMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("计算用户总数失败", e);
            return 0;
        }
    }

    @Override
    public Boolean banUser(int userId, String violationReason) {
        try {
            if (!StringUtils.hasText(violationReason)) {
                log.error("封禁用户需要提供违规原因");
                return false;
            }
            
            Example example = new Example(User.class);
            example.createCriteria().andEqualTo("userId", userId);
            
            User user = new User();
            user.setStatus((byte) 0); // 0表示禁用状态
            user.setViolationReason(violationReason); // 设置违规原因
            user.setUpdatedAt(new Date());
            
            int result = sysUserMapper.updateByExampleSelective(user, example);
            log.info("封禁用户 userId={}, 违规原因={}, 结果={}", userId, violationReason, result > 0);
            
            return result > 0;
        } catch (Exception e) {
            log.error("封禁用户失败, userId={}, 违规原因={}", userId, violationReason, e);
            return false;
        }
    }

    @Override
    public Boolean unbanUser(int userId) {
        try {
            Example example = new Example(User.class);
            example.createCriteria().andEqualTo("userId", userId);
            User existingUser = sysUserMapper.selectOneByExample(example);
            if (existingUser == null) {
                log.error("未能查询到用户，userId={}", userId);
                return false;
            }

            User user = new User();
            BeanUtils.copyProperties(existingUser, user);
            user.setStatus((byte) 1); // 1表示正常状态
            user.setUpdatedAt(new Date());
            user.setViolationReason(null);
            int result = sysUserMapper.updateByExample(user, example);
            log.info("解禁用户 userId={}, 结果={}", userId, result > 0);
            
            return result > 0;
        } catch (Exception e) {
            log.error("解禁用户失败, userId={}", userId, e);
            return false;
        }
    }

    @Override
    public List<UserAuthVo> getUserAuthList(int page, int size) {
        try {
            // 查询状态为"待审核"的认证申请
            Example example = new Example(UserAuth.class);
            example.createCriteria().andEqualTo("authStatus", 0);  // 0表示待审核状态
            example.orderBy("createdAt").desc(); // 按创建时间降序
            
            // 分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);
            
            // 执行查询
            List<UserAuth> authList = sysUserAuthMapper.selectByExampleAndRowBounds(example, rowBounds);
            
            // 转换为VO
            List<UserAuthVo> authVoList = new ArrayList<>();
            for (UserAuth auth : authList) {
                UserAuthVo authVo = new UserAuthVo();
                BeanUtils.copyProperties(auth, authVo);
                
                // 获取用户信息
                User user = sysUserMapper.selectByPrimaryKey(auth.getUserId());
                if (user != null) {
                    authVo.setUserName(user.getUserName());
                }
                
                authVoList.add(authVo);
            }
            
            return authVoList;
        } catch (Exception e) {
            log.error("分页获取认证申请列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public int countUserAuthList() {
        try {
            Example example = new Example(UserAuth.class);
            example.createCriteria().andEqualTo("authStatus", 0);  // 0表示待审核
            
            return sysUserAuthMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("计算认证申请总数失败", e);
            return 0;
        }
    }

    @Override
    @Transactional
    public Boolean passAuth(int userId) {
        try {
            // 先查询当前认证记录，检查是否有拒绝原因
            Example queryExample = new Example(UserAuth.class);
            queryExample.createCriteria().andEqualTo("userId", userId);
            UserAuth existingAuth = sysUserAuthMapper.selectOneByExample(queryExample);
            
            if (existingAuth == null) {
                log.error("未找到用户认证记录, userId={}", userId);
                return false;
            }
            
            // 更新认证状态为"已通过"
            Example example = new Example(UserAuth.class);
            example.createCriteria().andEqualTo("userId", userId);
            
            UserAuth auth = new UserAuth();
            BeanUtils.copyProperties(existingAuth, auth);
            auth.setAuthStatus((byte) 1);
            auth.setReviewedAt(new Date());
            // 明确设置rejectReason为null，避免框架将其转为空字符串
            auth.setRejectReason(null);
            // 使用以下方式更新，确保null值正确传递到数据库
            int result = sysUserAuthMapper.updateByExample(auth, example);

            if (result > 0) {
                // 更新用户表中的认证状态
                Example userExample = new Example(User.class);
                userExample.createCriteria().andEqualTo("userId", userId);
                
                User user = new User();
                user.setStatus((byte) 1); // 1表示正常
                user.setUpdatedAt(new Date());
                
                int userResult = sysUserMapper.updateByExampleSelective(user, userExample);
                
                log.info("认证通过 userId={}, 结果={}", userId, userResult > 0);
                return userResult > 0;
            }
            
            return false;
        } catch (Exception e) {
            log.error("处理认证通过失败, userId={}", userId, e);
            throw new RuntimeException("处理认证通过失败", e);
        }
    }

    @Override
    @Transactional
    public Boolean unPassAuth(int userId, String rejectReason) {
        try {
            if (!StringUtils.hasText(rejectReason)) {
                log.error("驳回理由不能为空");
                return false;
            }

            Example example = new Example(UserAuth.class);
            example.createCriteria().andEqualTo("userId", userId);
            
            UserAuth auth = new UserAuth();
            auth.setAuthStatus((byte) 2);
            auth.setRejectReason(rejectReason);
            auth.setReviewedAt(new Date());
            
            int result = sysUserAuthMapper.updateByExampleSelective(auth, example);
            //更新用户表中的认证状态
            if (result > 0) {
                Example userExample = new Example(User.class);
                userExample.createCriteria().andEqualTo("userId", userId);
                User user = new User();
                user.setStatus((byte) 2);
                user.setUpdatedAt(new Date());
                sysUserMapper.updateByExampleSelective(user, userExample);
            }
            log.info("认证驳回 userId={}, 理由={}, 结果={}", userId, rejectReason, result > 0);
            
            return result > 0;
        } catch (Exception e) {
            log.error("处理认证驳回失败, userId={}", userId, e);
            throw new RuntimeException("处理认证驳回失败", e);
        }
    }
}
