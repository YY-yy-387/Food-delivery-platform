package com.sky.mapper;

import com.sky.entity.Review;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper {
    @Insert("INSERT INTO review(order_id, user_id, rating, content, create_time) VALUES(#{orderId}, #{userId}, #{rating}, #{content}, #{createTime})")
    void insert(Review review);

    @Select("SELECT * FROM review WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Review> getByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM review ORDER BY create_time DESC LIMIT #{start}, #{pageSize}")
    List<Review> pageQuery(@Param("start") int start, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM review")
    int count();
}