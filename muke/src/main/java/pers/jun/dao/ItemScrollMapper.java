package pers.jun.dao;

import com.sun.tools.javac.jvm.Items;
import org.apache.ibatis.annotations.Mapper;
import pers.jun.pojo.ItemScroll;

import java.util.List;

@Mapper
public interface ItemScrollMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_scroll
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_scroll
     *
     * @mbg.generated
     */
    int insert(ItemScroll record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_scroll
     *
     * @mbg.generated
     */
    int insertSelective(ItemScroll record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_scroll
     *
     * @mbg.generated
     */
    ItemScroll selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_scroll
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ItemScroll record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_scroll
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ItemScroll record);

    //查询所有
    List<ItemScroll> getList();
}