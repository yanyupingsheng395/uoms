package com.linksteady.system.controller;

import java.util.List;
import java.util.Map;

import com.linksteady.common.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.User;
import com.linksteady.system.service.UserService;

@Controller
public class UserController extends BaseController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    private static final String ON = "on";

    @RequestMapping("user")
    @RequiresPermissions("user:list")
    public String index(Model model) {
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        return "system/user/user";
    }

    @RequestMapping("user/checkUserName")
    @ResponseBody
    public boolean checkUserName(String username, String oldusername) {
        if (StringUtils.isNotBlank(oldusername) && username.equalsIgnoreCase(oldusername)) {
            return true;
        }
        User result = this.userService.findByName(username);
        return result == null;
    }

    @RequestMapping("user/getUser")
    @ResponseBody
    public ResponseBo getUser(Long userId) {
        try {
            User user = this.userService.findById(userId);
            return ResponseBo.ok(user);
        } catch (Exception e) {
            log.error("获取用户失败", e);
            return ResponseBo.error("获取用户失败，请联系管理员！");
        }
    }

    @Log("获取用户信息")
    @RequestMapping("user/list")
    @RequiresPermissions("user:list")
    @ResponseBody
    public Map<String, Object> userList(QueryRequest request, User user) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<User> list = this.userService.findUsers(user, request);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return getDataTable(pageInfo);
    }

    @Log("更换主题")
    @RequestMapping("user/theme")
    @ResponseBody
    public ResponseBo updateTheme(User user) {
        try {
            this.userService.updateTheme(user.getTheme(), user.getUsername());
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("修改主题失败", e);
            return ResponseBo.error();
        }
    }

    @Log("新增用户")
    @RequiresPermissions("user:add")
    @RequestMapping("user/add")
    @ResponseBody
    public ResponseBo addUser(User user, Long[] roles) {
        try {
            //判断用户是否已经存在
            User addUser=userService.findByName(user.getUsername());

            if(null!=addUser)
            {
                return ResponseBo.error("当前用户名已经存在，换个用户名吧！");
            }

            this.userService.addUser(user, roles);
            return ResponseBo.ok("新增用户成功！");
        } catch (Exception e) {
            log.error("新增用户失败", e);
            return ResponseBo.error("新增用户失败，请联系管理员！");
        }
    }

    @Log("修改用户")
    @RequiresPermissions("user:update")
    @RequestMapping("user/update")
    @ResponseBody
    public ResponseBo updateUser(User user, Long[] rolesSelect) {
        try {
            this.userService.updateUser(user, rolesSelect);
            return ResponseBo.ok("修改用户成功！");
        } catch (Exception e) {
            log.error("修改用户失败", e);
            return ResponseBo.error("修改用户失败，请联系管理员！");
        }
    }

    @Log("删除用户")
    @RequiresPermissions("user:delete")
    @RequestMapping("user/delete")
    @ResponseBody
    public ResponseBo deleteUsers(String ids) {
        try {
            this.userService.deleteUsers(ids);
            return ResponseBo.ok("删除用户成功！");
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ResponseBo.error("删除用户失败，请联系管理员！");
        }
    }

    @Log("重置用户密码")
    @RequiresPermissions("user:resetPassword")
    @RequestMapping("user/resetPassword")
    @ResponseBody
    public ResponseBo resetPassword(String userId) {
        try {
            this.userService.resetPassword(userId);
            return ResponseBo.ok("重置用户密码成功！");
        } catch (Exception e) {
            log.error("重置用户密码失败", e);
            return ResponseBo.error("重置用户密码失败，请联系管理员！");
        }
    }

    @RequestMapping("user/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password) {
        User user = getCurrentUser();
        String encrypt = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);
        return user.getPassword().equals(encrypt);
    }

    @RequestMapping("user/updatePassword")
    @ResponseBody
    public ResponseBo updatePassword(String newPassword) {
        try {
            this.userService.updatePassword(newPassword);
            return ResponseBo.ok("更改密码成功！");
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return ResponseBo.error("更改密码失败，请联系网理员！");
        }
    }

    @RequestMapping("user/profile")
    public String profileIndex(Model model) {
        User user = super.getCurrentUser();
        user = this.userService.findUserProfile(user);
        String ssex = user.getSsex();
        if (User.SEX_MALE.equals(ssex)) {
            user.setSsex("性别：男");
        } else if (User.SEX_FEMALE.equals(ssex)) {
            user.setSsex("性别：女");
        } else {
            user.setSsex("性别：保密");
        }
        model.addAttribute("user", user);
        return "system/user/profile";
    }

    @RequestMapping("user/getUserProfile")
    @ResponseBody
    public ResponseBo getUserProfile(Long userId) {
        try {
            User user = new User();
            user.setUserId(userId);
            return ResponseBo.ok(this.userService.findUserProfile(user));
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResponseBo.error("获取用户信息失败，请联系管理员！");
        }
    }

    @RequestMapping("user/updateUserProfile")
    @ResponseBody
    public ResponseBo updateUserProfile(User user) {
        try {
            this.userService.updateUserProfile(user);
            return ResponseBo.ok("更新个人信息成功！");
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return ResponseBo.error("更新用户信息失败，请联系管理员！");
        }
    }

    @RequestMapping("user/changeAvatar")
    @ResponseBody
    public ResponseBo changeAvatar(String imgName) {
        try {
            String[] img = imgName.split("/");
            String realImgName = img[img.length - 1];
            User user = getCurrentUser();
            user.setAvatar(realImgName);
            this.userService.updateNotNull(user);
            return ResponseBo.ok("更新头像成功！");
        } catch (Exception e) {
            log.error("更换头像失败", e);
            return ResponseBo.error("更新头像失败，请联系管理员！");
        }
    }
}
