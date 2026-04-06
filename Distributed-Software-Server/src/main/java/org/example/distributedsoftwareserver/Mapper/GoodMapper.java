package org.example.distributedsoftwareserver.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.distributedsoftwareserver.Entity.Model.Good;

import java.util.List;

@Mapper
public interface GoodMapper {
    List<Good> selectAllGoods(int count);
    Good selectGoodByID(Long goodId);
    void insertGood(Good good);
    int decrementInventory(Long goodId);
}
