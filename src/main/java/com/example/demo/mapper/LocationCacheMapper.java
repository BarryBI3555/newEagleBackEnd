package com.example.demo.mapper;

import com.example.demo.entity.LocationCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LocationCacheMapper {

    int insert(LocationCache locationCache);

    LocationCache selectById(Long id);

    int updateById(LocationCache locationCache);

    int deleteById(Long id);

    LocationCache findByCoordinates(@Param("longitude") Double longitude, @Param("latitude") Double latitude);
    
    LocationCache findValidByCoordinates(@Param("longitude") Double longitude, @Param("latitude") Double latitude, @Param("currentTime") String currentTime);
    
    LocationCache findByAddress(@Param("address") String address);
    
    LocationCache findValidByAddress(@Param("address") String address, @Param("currentTime") String currentTime);
    
    LocationCache findByAddressLike(@Param("addressPattern") String addressPattern);
    
    LocationCache findValidByAddressLike(@Param("addressPattern") String addressPattern, @Param("currentTime") String currentTime);
}