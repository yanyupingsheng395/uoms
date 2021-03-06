package com.linksteady.system.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.config.SystemProperties;
import com.linksteady.common.controller.BaseController;
import com.linksteady.system.domain.Menu;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    @Autowired
    CommonFunService commonFunService;

    @Autowired
    SystemProperties systemProperties;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Log("获取菜单信息")
    @RequestMapping("menu")
    @RequiresPermissions("menu:list")
    public String index() {
        return "system/menu/menu";
    }


    @RequestMapping("menu/getMenu")
    @ResponseBody
    public ResponseBo getMenu(Long menuId) {
        try {
            Menu menu = this.menuService.findById(menuId);
            return ResponseBo.ok(menu);
        } catch (Exception e) {
            log.error("获取菜单信息失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return ResponseBo.error("获取信息失败，请联系管理员！");
        }
    }

    /**
     * 角色 授权时候 查询出所有的菜单
     * @return
     */
    @RequestMapping("menu/menuButtonTree")
    @ResponseBody
    public ResponseBo getMenuButtonTree() {
        try {
            Tree<Menu> tree = this.menuService.getMenuButtonTree();
            return ResponseBo.ok(tree);
        } catch (Exception e) {
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            log.error("获取菜单列表失败", e);
            return ResponseBo.error("获取菜单列表失败！");
        }
    }

    @RequestMapping("menu/tree")
    @ResponseBody
    public ResponseBo getMenuTree(String sysCode) {
        try {
            Tree<Menu> tree = this.menuService.getMenuTree(sysCode);
            return ResponseBo.ok(tree);
        } catch (Exception e) {
            log.error("获取菜单树失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return ResponseBo.error("获取菜单树失败！");
        }
    }

    /**
     * 菜单列表界面
     * @param menu
     * @return
     */
    @RequestMapping("menu/list")
    @RequiresPermissions("menu:list")
    @ResponseBody
    public List<Menu> menuList(Menu menu) {
        try {
            return this.menuService.findAllMenus(menu);
        } catch (Exception e) {
            log.error("获取菜单集合失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return new ArrayList<>();
        }
    }

    /**
     * 检查菜单名称是否已经存在
     * @param menuName
     * @param type
     * @return
     */
    @RequestMapping("menu/checkMenuName")
    @ResponseBody
    public boolean checkMenuName(String menuName, String type,Long menuId) {
        //menuId不为空，表示更新
        Menu result = this.menuService.findByNameAndType(menuName,type,menuId);
        return result == null;
    }

    @Log("新增菜单/按钮")
    @RequiresPermissions("menu:add")
    @RequestMapping("menu/add")
    @ResponseBody
    public ResponseBo addMenu(Menu menu) {
        String name;
        if (Menu.TYPE_MENU.equals(menu.getType())) {
            name = "菜单";
        } else {
            name = "按钮";
        }
        try {
            this.menuService.addMenu(menu);
            return ResponseBo.ok("新增" + name + "成功！");
        } catch (Exception e) {
            log.error("新增{}失败", name, e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return ResponseBo.error("新增" + name + "失败，请联系管理员！");
        }
    }

    @Log("删除菜单")
    @RequiresPermissions("menu:delete")
    @RequestMapping("menu/delete")
    @ResponseBody
    public ResponseBo deleteMenus(String ids) {
        try {
            this.menuService.deleteMeuns(ids);
            return ResponseBo.ok("删除成功！");
        } catch (Exception e) {
            log.error("获取菜单失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return ResponseBo.error("删除失败，请联系管理员！");
        }
    }

    @Log("修改菜单/按钮")
    @RequiresPermissions("menu:update")
    @RequestMapping("menu/update")
    @ResponseBody
    public ResponseBo updateMenu(Menu menu) {
        String name;
        if (Menu.TYPE_MENU.equals(menu.getType())) {
            name = "菜单";
        } else {
            name = "按钮";
        }
        try {
            this.menuService.updateMenu(menu);
            return ResponseBo.ok("修改" + name + "成功！");
        } catch (Exception e) {
            log.error("修改{}失败", name, e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return ResponseBo.error("修改" + name + "失败，请联系管理员！");
        }
    }

    @GetMapping("menu/urlList")
    @ResponseBody
    public List<Map<String, String>> getAllUrl() {
        return this.menuService.getAllUrl("1");
    }

}
