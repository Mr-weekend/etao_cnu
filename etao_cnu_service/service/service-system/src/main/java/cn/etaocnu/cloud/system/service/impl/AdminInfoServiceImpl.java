package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.model.entity.Admin;
import cn.etaocnu.cloud.system.mapper.AdminMapper;
import cn.etaocnu.cloud.system.service.AdminInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Service
public class AdminInfoServiceImpl implements AdminInfoService {

    @Resource
    private AdminMapper adminMapper;
    
    @Resource
    private PasswordEncoder passwordEncoder;
    
    @Resource
    private RedisTemplate<String, Integer> redisTemplate;
    
    // Redis中存储admin登录token的key前缀
    private static final String ADMIN_LOGIN_KEY_PREFIX = "admin:login:";

    @Override
    public int login(String adminName, String password) {
        try{
            if (!StringUtils.hasText(adminName) || !StringUtils.hasText(password)) {
                return 0;
            }
            // 查询用户名是否存在
            Example example = new Example(Admin.class);
            example.createCriteria().andEqualTo("adminName", adminName);
            Admin admin = adminMapper.selectOneByExample(example);

            if (admin == null) {
                log.error("管理员不存在: {}", adminName);
                return -1;
            }
            // 检查密码是否正确
            if (!passwordEncoder.matches(password, admin.getPassword())) {
                log.error("密码错误: {}", adminName);
                return -2;
            }
            //返回管理员id
            return admin.getAdminId();
        }catch (Exception e){
            log.error("管理员登录失败！");
            return -3;
        }
    }

    @Override
    public boolean logout(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        String key = ADMIN_LOGIN_KEY_PREFIX + token;
        Boolean hasKey = redisTemplate.hasKey(key);

        if (Boolean.TRUE.equals(hasKey)) {
            Boolean delete = redisTemplate.delete(key);
            return Boolean.TRUE.equals(delete);
        }

        return false;
    }

    @Override
    public boolean addAdmin(Admin admin) {
        if (admin == null || !StringUtils.hasText(admin.getAdminName()) || !StringUtils.hasText(admin.getPassword())) {
            return false;
        }
        
        // 检查用户名是否已存在
        Example example = new Example(Admin.class);
        example.createCriteria().andEqualTo("adminName", admin.getAdminName());
        if (adminMapper.selectCountByExample(example) > 0) {
            log.error("用户名已存在: {}", admin.getAdminName());
            return false;
        }
        
        // 密码加密
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        // 插入数据库
        int result = adminMapper.insertSelective(admin);
        return result > 0;
    }

    @Override
    public boolean updateAdminName(Integer adminId, String newAdminName) {
        if (adminId == null || !StringUtils.hasText(newAdminName)) {
            return false;
        }
        
        // 检查新用户名是否已存在
        Example example = new Example(Admin.class);
        example.createCriteria().andEqualTo("adminName", newAdminName);
        if (adminMapper.selectCountByExample(example) > 0) {
            log.error("新用户名已存在: {}", newAdminName);
            return false;
        }
        
        // 更新用户名
        Admin admin = new Admin();
        admin.setAdminId(adminId);
        admin.setAdminName(newAdminName);
        
        int result = adminMapper.updateByPrimaryKeySelective(admin);
        return result > 0;
    }

    @Override
    public boolean updatePassword(Integer adminId, String oldPassword, String newPassword) {
        if (adminId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return false;
        }
        
        // 查询管理员
        Admin admin = adminMapper.selectByPrimaryKey(adminId);
        if (admin == null) {
            log.error("管理员不存在: {}", adminId);
            return false;
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            log.error("旧密码错误: {}", adminId);
            return false;
        }
        
        // 更新密码
        Admin updateAdmin = new Admin();
        updateAdmin.setAdminId(adminId);
        updateAdmin.setPassword(passwordEncoder.encode(newPassword));
        
        int result = adminMapper.updateByPrimaryKeySelective(updateAdmin);
        return result > 0;
    }

    @Override
    public Admin getAdminInfoByToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        
        Integer adminId = redisTemplate.opsForValue().get(ADMIN_LOGIN_KEY_PREFIX + token);
        if (adminId == null) {
            return null;
        }

        return adminMapper.selectByPrimaryKey(adminId);
    }
} 