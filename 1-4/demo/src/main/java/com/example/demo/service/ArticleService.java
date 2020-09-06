package com.example.demo.service;

import com.example.demo.pojo.Article;

import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/9/6
 */
public interface ArticleService {
    List<Article> queryAll(int pageNum, int pageSize);

}
