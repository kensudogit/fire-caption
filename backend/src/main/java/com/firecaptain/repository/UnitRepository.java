package com.firecaptain.repository;

import com.firecaptain.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ユニットリポジトリ
 * 
 * 消防車両・ユニットのデータアクセスを担当するリポジトリです。
 * ユニットの検索、状態管理、位置情報の更新などの機能を提供します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    /**
     * ユニット番号でユニットを検索
     * 
     * @param unitNumber ユニット番号
     * @return ユニット（見つからない場合はnull）
     */
    Unit findByUnitNumber(String unitNumber);

    /**
     * ステータスでユニットを検索
     * 
     * @param status ユニットステータス
     * @return 該当するユニットのリスト
     */
    List<Unit> findByStatus(Unit.UnitStatus status);

    /**
     * 消防署IDでユニットを検索
     * 
     * @param fireStationId 消防署ID
     * @return 該当するユニットのリスト
     */
    List<Unit> findByFireStationId(Long fireStationId);

    /**
     * ユニットタイプでユニットを検索
     * 
     * @param unitType ユニットタイプ
     * @return 該当するユニットのリスト
     */
    List<Unit> findByUnitType(Unit.UnitType unitType);

    /**
     * 利用可能なユニットを検索
     * 
     * @return 利用可能なユニットのリスト
     */
    @Query("SELECT u FROM Unit u WHERE u.status = 'AVAILABLE' AND u.maintenanceStatus = 'OPERATIONAL'")
    List<Unit> findAvailableUnits();

    /**
     * 指定されたタイプの利用可能なユニットを検索
     * 
     * @param unitType ユニットタイプ
     * @return 利用可能なユニットのリスト
     */
    @Query("SELECT u FROM Unit u WHERE u.unitType = :unitType AND u.status = 'AVAILABLE' AND u.maintenanceStatus = 'OPERATIONAL'")
    List<Unit> findAvailableUnitsByType(@Param("unitType") Unit.UnitType unitType);

    /**
     * 指定された消防署の利用可能なユニットを検索
     * 
     * @param fireStationId 消防署ID
     * @return 利用可能なユニットのリスト
     */
    @Query("SELECT u FROM Unit u WHERE u.fireStation.id = :fireStationId AND u.status = 'AVAILABLE' AND u.maintenanceStatus = 'OPERATIONAL'")
    List<Unit> findAvailableUnitsByFireStation(@Param("fireStationId") Long fireStationId);

    /**
     * 指定された範囲内のユニットを検索
     * 
     * @param minLat 最小緯度
     * @param maxLat 最大緯度
     * @param minLng 最小経度
     * @param maxLng 最大経度
     * @return 範囲内のユニットのリスト
     */
    @Query("SELECT u FROM Unit u WHERE u.currentLatitude BETWEEN :minLat AND :maxLat AND u.currentLongitude BETWEEN :minLng AND :maxLng")
    List<Unit> findUnitsInArea(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

    /**
     * メンテナンスが必要なユニットを検索
     * 
     * @return メンテナンスが必要なユニットのリスト
     */
    @Query("SELECT u FROM Unit u WHERE u.maintenanceStatus != 'OPERATIONAL'")
    List<Unit> findUnitsNeedingMaintenance();

    /**
     * ユニット番号の存在確認
     * 
     * @param unitNumber ユニット番号
     * @return 存在する場合はtrue
     */
    boolean existsByUnitNumber(String unitNumber);
}
