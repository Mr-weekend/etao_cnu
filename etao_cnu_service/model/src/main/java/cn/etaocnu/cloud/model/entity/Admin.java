package cn.etaocnu.cloud.model.entity;

import javax.persistence.*;

/**
 * 表名：admin
 * 表注释：管理员信息表
*/
@Table(name = "admin")
public class Admin {
    /**
     * 管理员ID
     */
    @Id
    @Column(name = "admin_id")
    @GeneratedValue(generator = "JDBC")
    private Integer adminId;

    /**
     * 管理员用户名
     */
    @Column(name = "admin_name")
    private String adminName;

    /**
     * 密码哈希值
     */
    private String password;

    /**
     * 获取管理员ID
     *
     * @return adminId - 管理员ID
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * 设置管理员ID
     *
     * @param adminId 管理员ID
     */
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    /**
     * 获取管理员用户名
     *
     * @return adminName - 管理员用户名
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * 设置管理员用户名
     *
     * @param adminName 管理员用户名
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * 获取密码哈希值
     *
     * @return password - 密码哈希值
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码哈希值
     *
     * @param password 密码哈希值
     */
    public void setPassword(String password) {
        this.password = password;
    }
}