package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateStatus(Integer status, Long id);

    void update(Dish dish);

    List<Dish> list(Dish dish);

    /**
     * 根据状态统计菜品数量
     * @param status
     * @return
     */
    @Select("select count(id) from dish where status = #{status}")
    Integer countByStatus(Integer status);

    /**
     * 扣减库存（乐观锁：stock>0才扣，0表示不限量）
     */
    @Update("UPDATE dish SET stock = stock - #{num}, version = version + 1 WHERE id = #{id} AND stock >= #{num} AND stock > 0")
    int decreaseStock(@Param("id") Long id, @Param("num") Integer num);

    @Update("UPDATE dish SET stock = stock + #{num} WHERE id = #{id} AND stock > 0")
    void increaseStock(@Param("id") Long id, @Param("num") Integer num);
}
