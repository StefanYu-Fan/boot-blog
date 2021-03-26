package com.stefan.blog.service.impl;

import com.stefan.blog.model.UserContent;
import com.stefan.blog.service.SolrService;
import com.stefan.blog.utils.PageHelper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * solr 查询配置
 */

@Service
public class SolrServiceImpl implements SolrService {
    @Autowired
    SolrClient solrClient;

    @Override
    public PageHelper.Page<UserContent> findByKeyWords(String keyword, Integer pageNum, Integer pageSize) {
        SolrQuery solrQuery = new SolrQuery( );
        //设置查询条件
        solrQuery.setQuery( "title:"+keyword );
        //设置高亮
        solrQuery.setHighlight( true );
        solrQuery.addHighlightField( "title" );
        solrQuery.setHighlightSimplePre( "<span style='color:red'>" );
        solrQuery.setHighlightSimplePost( "</span>" );

        //分页
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 7;
        }
        solrQuery.setStart( (pageNum-1)*pageSize );
        solrQuery.setRows( pageSize );
        solrQuery.addSort("rpt_time", SolrQuery.ORDER.desc);
        //开始查询

        try {
            QueryResponse response = solrClient.query( solrQuery );
            //获得高亮数据集合
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            //获得结果集
            SolrDocumentList resultList = response.getResults();
            //获得总数量
            long totalNum = resultList.getNumFound();
            List<UserContent> list = new ArrayList<UserContent>(  );
            for (SolrDocument solrDocument : resultList){
                //创建文章对象
                UserContent content = new UserContent();
                //文章id
                String id = (String) solrDocument.get( "id" );
                List content1 = (List) solrDocument.get( "content" );
                List commentNum = (List) solrDocument.get( "comment_num" );
                List downvote = (List) solrDocument.get( "downvote" );
                List upvote = (List) solrDocument.get( "upvote" );
                List nickName = (List) solrDocument.get( "nick_name" );
                List imgUrl = (List) solrDocument.get( "img_url" );
                List uid = (List) solrDocument.get( "u_id" );
                List rpt_time = (List) solrDocument.get( "rpt_time" );
                List category = (List) solrDocument.get( "category" );
                List personal = (List) solrDocument.get( "personal" );
                //取得高亮数据集合中的文章标题
                Map<String, List<String>> map = highlighting.get( id );
                String title = map.get( "title" ).get( 0 );

                content.setId( Long.parseLong( id ) );
                content.setCommentNum( Integer.parseInt( commentNum.get(0).toString() ) );
                content.setDownvote( Integer.parseInt( downvote.get(0).toString() ) );
                content.setUpvote( Integer.parseInt( upvote.get(0).toString() ) );
                content.setNickName( nickName.get(0).toString() );
                content.setImgUrl( imgUrl.get(0).toString() );
                content.setuId( Long.parseLong( uid.get(0).toString() ) );
                content.setTitle( title );
                content.setPersonal( personal.get(0).toString() );
                Date date = (Date)rpt_time.get(0);
                content.setRptTime(date);
                List<String> clist = (ArrayList)content1;
                content.setContent( clist.get(0).toString() );
                content.setCategory( category.toString() );
                list.add( content );
            }
            PageHelper.startPage(pageNum, pageSize);//开始分页
            PageHelper.Page page = PageHelper.endPage();//分页结束
            page.setResult(list);
            page.setTotal(totalNum);
            return page;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addUserContent(UserContent cont) {
        if(cont!=null){
            addDocument(cont);
        }
    }

    @Override
    public void updateUserContent(UserContent cont) {
        if(cont!=null){
            addDocument(cont);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            solrClient.deleteById(id.toString());
            solrClient.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDocument(UserContent cont){
        try {
            SolrInputDocument inputDocument = new SolrInputDocument();
            inputDocument.addField( "comment_num", cont.getCommentNum() );
            inputDocument.addField( "downvote", cont.getDownvote() );
            inputDocument.addField( "upvote", cont.getUpvote() );
            inputDocument.addField( "nick_name", cont.getNickName());
            inputDocument.addField( "img_url", cont.getImgUrl() );
            inputDocument.addField( "rpt_time", cont.getRptTime() );
            inputDocument.addField( "content", cont.getContent() );
            inputDocument.addField( "category", cont.getCategory());
            inputDocument.addField( "title", cont.getTitle() );
            inputDocument.addField( "u_id", cont.getuId() );
            inputDocument.addField( "id", cont.getId());
            inputDocument.addField( "personal", cont.getPersonal());
            solrClient.add( inputDocument );
            solrClient.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
