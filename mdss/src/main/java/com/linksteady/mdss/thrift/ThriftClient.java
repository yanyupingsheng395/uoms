package com.linksteady.mdss.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class ThriftClient {
    private  ThriftService.Client thriftService;
    private TBinaryProtocol protocol;
    private TSocket transport;
    private String host;
    private int port;
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public void init() {
        transport = new TSocket(host, port);
        protocol = new TBinaryProtocol(transport);
        thriftService = new ThriftService.Client(protocol);
    }

    public ThriftService.Client getThriftService() {
        return thriftService;
    }

    public  void  open() throws TTransportException {
        transport.open();
    }

    public  void  close()
    {
        transport.close();
    }

}
