package com.linksteady.common.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class ThriftClient {
    private ProdInsightService.Client insightService;
    private TBinaryProtocol tBinaryProtocol;
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
        insightService = new ProdInsightService.Client(tBinaryProtocol);
    }

    public ProdInsightService.Client getInsightService() {
        return insightService;
    }

    public void open() throws TTransportException {
        if(!isOpend())
        {
            transport.open();
        }

    }

    public boolean isOpend() {
        return transport.isOpen();
    }

    public void close() {
        transport.close();
    }

}
