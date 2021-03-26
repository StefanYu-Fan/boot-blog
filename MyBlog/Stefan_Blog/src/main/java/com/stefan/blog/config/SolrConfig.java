package com.stefan.blog.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//@Configuration
//@EnableTransactionManagement
//public class SolrConfig {
//
//    @Bean
//    public HttpSolrClient solrClient() {
//        HttpSolrClient httpSolrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
//        return httpSolrClient;
//    }
//
//    @Bean
//    public SolrOperations solrTemplate(){
//        return new SolrTemplate(solrClient());
//    }
//}
