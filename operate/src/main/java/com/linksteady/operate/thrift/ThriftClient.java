package com.linksteady.operate.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class ThriftClient {
    private ProdInsightService.Client insightService;
    private  ActivityService.Client activityService;
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
        insightService = new ProdInsightService.Client(protocol);
        activityService = new ActivityService.Client(protocol);
    }

    public ProdInsightService.Client getInsightService() {
        return insightService;
    }

    public ActivityService.Client getActivityService() {
        return activityService;
    }

    public void open() throws TTransportException {
        transport.open();
    }

    public boolean isOpend() {
        return transport.isOpen();
    }

    public void close() {
        transport.close();
    }

}
