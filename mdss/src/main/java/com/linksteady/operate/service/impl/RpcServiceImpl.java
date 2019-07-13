package com.linksteady.operate.service.impl;

import com.linksteady.operate.service.RpcService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author hxcao
 * @date 2019-07-10
 */
public class RpcServiceImpl implements RpcService {
    @Override
    public String queryName(String no) throws RemoteException {
        // 方法的具体实现
       System.out.println("hello" + no);
       return String.valueOf(System.currentTimeMillis());
    }

    public static void main(String[] args) throws RemoteException{
        // 注册管理器
        Registry registry = null;
        // 创建一个服务注册管理器
        registry = LocateRegistry.createRegistry(8088);
        // 创建一个服务
        RpcServiceImpl server = new RpcServiceImpl();
        // 将服务绑定命名
        registry.rebind("vince", server);
    }
}
