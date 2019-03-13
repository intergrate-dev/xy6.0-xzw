package com.founder.xy.commons;

import org.apache.solr.client.solrj.impl.CloudSolrServer;

/**
 * solrcloud工具类
 * @author Guo Qixun
 */
public class SolrCloudUtils {

    public String solrcloudServer;
    public int zkClientTimeout;
    public int zkConnectTimeout;

    public CloudSolrServer getCloundSolrServer(String coreName){
        CloudSolrServer cloudSolrServer = null;

        cloudSolrServer = new CloudSolrServer(solrcloudServer);
        cloudSolrServer.setDefaultCollection(coreName);
        cloudSolrServer.setZkClientTimeout(zkClientTimeout);
        cloudSolrServer.setZkConnectTimeout(zkConnectTimeout);

        return cloudSolrServer;
    }

    public String getSolrcloudServer() {
        return solrcloudServer;
    }

    public void setSolrcloudServer(String solrcloudServer) {
        this.solrcloudServer = solrcloudServer;
    }

    public int getZkClientTimeout() {
        return zkClientTimeout;
    }

    public void setZkClientTimeout(int zkClientTimeout) {
        this.zkClientTimeout = zkClientTimeout;
    }

    public int getZkConnectTimeout() {
        return zkConnectTimeout;
    }

    public void setZkConnectTimeout(int zkConnectTimeout) {
        this.zkConnectTimeout = zkConnectTimeout;
    }
}
