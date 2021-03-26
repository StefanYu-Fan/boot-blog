package com.stefan.blog.service;

import com.stefan.blog.model.UserContent;
import com.stefan.blog.utils.PageHelper;

public interface SolrService {
    /**
     * 根据关键字搜索文章并分页
     * @param keyword
     * @return
     */
    PageHelper.Page<UserContent> findByKeyWords(String keyword, Integer pageNum, Integer pageSize);


    /**
     * 添加文章到solr索引库中
     * @param userContent
     */
    void addUserContent(UserContent userContent);

    /**
     * 根据solr索引库
     * @param userContent
     */
    void updateUserContent(UserContent userContent);

    /**
     * 根据文章id删除索引库
     */
    void deleteById(Long id);
}
