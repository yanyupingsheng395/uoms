package com.linksteady.operate.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class ThriftClient {
    private ProdInsightService.Client insightService;
    private  ActivityService.Client activityService;
    private TBinaryProtocol tBinaryProtocol;
    private TMultiplexedProtocol insightProcessor;
    private TMultiplexedProtocol activityProcessor;
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
        tBinaryProtocol=new TBinaryProtocol(transport);
        insightProcessor = new TMultiplexedProtocol(tBinaryProtocol,"'insignt");
        activityProcessor=new TMultiplexedProtocol(tBinaryProtocol,"'activity");
        insightService = new ProdInsightService.Client(insightProcessor);
        activityService = new ActivityService.Client(activityProcessor);
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