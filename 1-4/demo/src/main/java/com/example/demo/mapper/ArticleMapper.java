package com.example.demo.mapper;

import com.example.demo.pojo.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/9/6
 */
public interface ArticleMapper {
    @Select("select * from t_article limit #{offset}, #{limit}")
    List<Article> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
