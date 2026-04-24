package com.sales.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sales.customer.entity.SalesPerson;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SalesPersonMapper extends BaseMapper<SalesPerson> {
}
