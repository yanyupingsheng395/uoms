package com.linksteady.mdss.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author hxcao
 * @date 2019-07-10
 */
public interface RpcService extends Remote {

    public String queryName(String no) throws RemoteException;
}
