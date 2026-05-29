# newEagleBackEnd

战鹰系统后端项目，基于 Spring Boot、MyBatis 和 SQLite，提供人员位置、案件热力图、工作量统计、腾讯地图代理和地理编码缓存等接口。

## 技术栈

- Java 8
- Spring Boot 2.7.18
- MyBatis
- SQLite
- HikariCP
- Tencent Map WebService API
- Maven Wrapper

## 环境要求

- JDK 8 或兼容版本
- Maven Wrapper 已包含在项目中，可直接使用 `mvnw.cmd`
- 真实 SQLite 数据库路径通过环境变量配置
- 腾讯地图 Key 通过环境变量配置，不要提交到仓库

## 本地配置

仓库提交了 `.env.example` 作为配置模板。Windows PowerShell 示例：

```powershell
$env:TENCENT_MAP_KEY="你的腾讯地图WebServiceKey"
$env:TENCENT_MAP_JS_KEY="你的腾讯地图JSKey"
$env:SPRING_DATASOURCE_URL="jdbc:sqlite:D:/PICC_SYSTEM/EAGLE_SYSTEM/db.sqlite3"
```

默认配置文件在：

```text
src/main/resources/application.properties
```

默认值说明：

```properties
server.port=${SERVER_PORT:8080}
tencent.map.key=${TENCENT_MAP_KEY:}
tencent.map.js-key=${TENCENT_MAP_JS_KEY:}
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:sqlite:./db.sqlite3}
app.heatmap.enable-geocode=${APP_HEATMAP_ENABLE_GEOCODE:true}
app.heatmap.max-points=${APP_HEATMAP_MAX_POINTS:10000}
app.heatmap.region.min-lng=${APP_HEATMAP_REGION_MIN_LNG:102.5}
app.heatmap.region.max-lng=${APP_HEATMAP_REGION_MAX_LNG:104.9}
app.heatmap.region.min-lat=${APP_HEATMAP_REGION_MIN_LAT:30.0}
app.heatmap.region.max-lat=${APP_HEATMAP_REGION_MAX_LAT:31.5}
app.geocode.permits-per-second=${APP_GEOCODE_PERMITS_PER_SECOND:3}
```

如果没有配置 `SPRING_DATASOURCE_URL`，服务会使用当前目录下的 `db.sqlite3`。真实业务数据请显式指定数据库路径。

## 本地启动

Windows：

```bash
mvnw.cmd spring-boot:run
```

或先编译：

```bash
mvnw.cmd -DskipTests compile
```

默认服务地址：

```text
http://localhost:8080
```

## 主要接口

### 热力图

```text
GET  /api/hotmap?date=YYYY-MM-DD
GET  /api/hotmap/progress?date=YYYY-MM-DD
POST /api/hotmap/clearCache?date=YYYY-MM-DD
GET  /api/statsCardsData?date=YYYY-MM-DD
```

说明：

- `/api/hotmap` 先返回已有经纬度热力点。
- 如果存在只有中文地址、没有经纬度的数据，后端会启动异步解析。
- `/api/hotmap/progress` 返回解析进度、成功数、失败数和当前缓存点数量。
- `/api/hotmap/clearCache` 清理指定日期的内存热力图缓存，使用 `POST`，因为它有副作用。

### 人员位置

```text
GET /api/locations
GET /api/locations/latest
GET /api/locations/latest/progress
GET /api/locations/user/{usercode}
GET /api/locations/groups
```

### 工作量统计

```text
GET /api/cur_gzl/list
GET /api/cur_gzl_bm/list
GET /api/cur_gzl_group/list
GET /api/cur_gzl_rs/list
```

### 腾讯地图代理

```text
GET /api/map/geocoder
GET /api/map/district/search
GET /api/map/district/getchildren
GET /api/map/jsapi
```

## 热力图异步解析逻辑

业务要求：

> 先返回已有经纬度，后台再慢慢补缺失地址，缓存表只辅助异步解析。

当前实现流程：

1. 前端请求 `/api/hotmap?date=YYYY-MM-DD`。
2. 后端从案件表查询已有经纬度，立即返回热力点。
3. 后端统计同一天缺失经纬度但有中文地址的数据。
4. 如果存在缺失数据，提交后台异步任务。
5. 异步任务先按中文地址去重。
6. 批量查询 `acd_location_address_cache` 缓存表，命中的地址直接合并为热力点。
7. 缓存没有命中的地址，按限流策略调用腾讯地图 WebService。
8. 解析成功后写入缓存表，并合并到内存热力图缓存。
9. 前端轮询 `/api/hotmap/progress`，完成后重新拉取 `/api/hotmap`。

缓存表不是主数据源，只用于复用历史解析结果，减少地图 API 调用量。

## 调度和限流

`GeocodeScheduler` 负责后台任务调度和 API 调用节流：

- 使用线程池执行异步地理编码任务。
- 同一个任务 key 正在运行时，不重复提交。
- 使用简化令牌桶控制请求速率，避免超过地图 API 每秒限制。
- `acquire()` 在令牌不足时等待，并在重新获得令牌后继续请求。

任务 key 示例：

```text
hotmap:2026-05-28
location:2026-05-28
```

## 重点文件

```text
src/main/java/com/example/demo/controller/HotmapController.java
src/main/java/com/example/demo/service/impl/HotmapServiceImpl.java
src/main/java/com/example/demo/service/impl/AsyncGeocodeServiceImpl.java
src/main/java/com/example/demo/service/impl/HeatDataCacheServiceImpl.java
src/main/java/com/example/demo/util/GeocodeScheduler.java
src/main/java/com/example/demo/util/LocationAddressConverter.java
src/main/resources/mapper/PrplCheckTaskMapper.xml
src/main/resources/mapper/LocationCacheMapper.xml
```

## 验证命令

```bash
mvnw.cmd -DskipTests compile
```

接口冒烟：

```bash
curl "http://localhost:8080/api/hotmap?date=2026-05-28"
curl "http://localhost:8080/api/hotmap/progress?date=2026-05-28"
curl -X POST "http://localhost:8080/api/hotmap/clearCache?date=2026-05-28"
```

## 不要提交的文件

```text
target/
*.log
.env
.env.local
```
