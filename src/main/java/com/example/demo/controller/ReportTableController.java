package com.example.demo.controller;

import com.example.demo.entity.CurGzlTableRy;
import com.example.demo.entity.CurGzlTableBm;
import com.example.demo.entity.CurGzlTableGroup;
import com.example.demo.entity.CurGzlTableRs;
import com.example.demo.entity.AcdZhouqiQs;
import com.example.demo.entity.AcdZhouqiBm;
import com.example.demo.entity.AcdZhpflKhq;
import com.example.demo.entity.AcdPacllBm;
import com.example.demo.entity.AcdPacllXz;
import com.example.demo.entity.AcdPacllRy;
import com.example.demo.entity.AcdPflsgnZgs;
import com.example.demo.entity.AcdPflsgnKhq;
import com.example.demo.entity.AcdPflsgnXny;
import com.example.demo.entity.AcdAnjunCxZgs;
import com.example.demo.entity.AcdAnjunCxKhq;
import com.example.demo.entity.AcdAnjunCxXny;
import com.example.demo.entity.AcdChakanYear;
import com.example.demo.entity.AcdDingsunTjlYear;
import com.example.demo.entity.AcdDingsunWclYear;
import com.example.demo.entity.AcdCkDswcYear;
import com.example.demo.entity.AcdDingsunZflYear;
import com.example.demo.entity.AcdLisuanYear;
import com.example.demo.entity.AcdRsGzlYear;
import com.example.demo.entity.AcdChakanMonth;
import com.example.demo.entity.PageResult;
import com.example.demo.entity.Result;
import com.example.demo.service.ReportTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ReportTableController {

    private static final Logger log = LoggerFactory.getLogger(ReportTableController.class);

    @Autowired
    private ReportTableService reportTableService;

    @GetMapping("/cur_gzl/list")
    public Result<List<CurGzlTableRy>> getCurGzlList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String comName,
            @RequestParam(required = false) String groups,
            @RequestParam(required = false) String userName
    ) {
        try {
            // 日期都为空 → 取最大日期
            if ((startDate == null || startDate.trim().isEmpty())
                    && (endDate == null || endDate.trim().isEmpty())) {

                // 直接调用service
                String maxDate = reportTableService.getMaxTjDate("acd_dangri_gzl_ry");
                startDate = maxDate;
                endDate = maxDate;
            }

            // 只有开始日期
            if (endDate == null || endDate.trim().isEmpty()) {
                endDate = startDate;
            }

            List<CurGzlTableRy> data = reportTableService.getCurGzlData(
                    startDate, endDate, comName, groups, userName);

            return Result.success(data);

        } catch (Exception e) {
            log.error("获取失败", e);
            return Result.error("获取失败", e);
        }
    }

    @GetMapping("/cur_gzl_bm/list")
    public Result<List<CurGzlTableBm>> getCurGzlListBm(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String comName
    ) {
        try {
            if ((startDate == null || startDate.trim().isEmpty())
                    && (endDate == null || endDate.trim().isEmpty())) {
                String maxDate = reportTableService.getMaxTjDate("acd_dangri_gzl_bm");
                startDate = maxDate;
                endDate = maxDate;
            }

            if (endDate == null || endDate.trim().isEmpty()) {
                endDate = startDate;
            }

            List<CurGzlTableBm> data = reportTableService.getCurGzlDataBm(
                    startDate, endDate, comName);

            return Result.success(data);

        } catch (Exception e) {
            log.error("获取部门统计失败", e);
            return Result.error("获取部门统计失败", e);
        }
    }

    @GetMapping("/cur_gzl_group/list")
    public Result<List<CurGzlTableGroup>> getCurGzlListGroup(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String comName,
            @RequestParam(required = false) String groups
    ) {
        try {
            if ((startDate == null || startDate.trim().isEmpty())
                    && (endDate == null || endDate.trim().isEmpty())) {
                // 直接用咱们的通用最大日期方法
                String maxDate = reportTableService.getMaxTjDate("acd_dangri_gzl_group");
                startDate = maxDate;
                endDate = maxDate;
            }

            if (endDate == null || endDate.trim().isEmpty()) {
                endDate = startDate;
            }

            List<CurGzlTableGroup> data = reportTableService.getCurGzlDataGroup(
                    startDate, endDate, comName, groups);

            return Result.success(data);

        } catch (Exception e) {
            log.error("获取小组统计失败", e);
            return Result.error("获取小组统计失败", e);
        }
    }

    @GetMapping("/cur_gzl_rs/list")
    public Result<List<CurGzlTableRs>> getCurGzlListRs(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String comName
    ) {
        try {
            if ((startDate == null || startDate.trim().isEmpty())
                    && (endDate == null || endDate.trim().isEmpty())) {
                // 通用最大日期
                String maxDate = reportTableService.getMaxTjDate("acd_dangri_gzl_rs");
                startDate = maxDate;
                endDate = maxDate;
            }

            if (endDate == null || endDate.trim().isEmpty()) {
                endDate = startDate;
            }

            List<CurGzlTableRs> data = reportTableService.getCurGzlDataRs(
                    startDate, endDate, comName);

            return Result.success(data);

        } catch (Exception e) {
            log.error("获取住院门诊统计失败", e);
            return Result.error("获取住院门诊统计失败", e);
        }
    }

    // ==================== 新增表接口 ====================

    /** 周期-市公司 */
    @GetMapping("/zhouqi_qs/list")
    public Result<List<AcdZhouqiQs>> getZhouqiQsList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                String maxDate = reportTableService.getMaxTjDate("acd_zhouqi_qs");
                tjDate = maxDate;
            }
            List<AcdZhouqiQs> data = reportTableService.getZhouqiQsData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取周期-市公司失败", e);
            return Result.error("获取周期-市公司失败", e);
        }
    }

    /** 周期-部门 */
    @GetMapping("/zhouqi_bm/list")
    public Result<List<AcdZhouqiBm>> getZhouqiBmList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comname
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                String maxDate = reportTableService.getMaxTjDate("acd_zhouqi_bm");
                tjDate = maxDate;
            }
            List<AcdZhouqiBm> data = reportTableService.getZhouqiBmData(tjDate, comname);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取周期-部门失败", e);
            return Result.error("获取周期-部门失败", e);
        }
    }

    /** 综合赔付率-客户群 */
    @GetMapping("/zhpfl_khq/list")
    public Result<List<AcdZhpflKhq>> getZhpflKhqList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                String maxDate = reportTableService.getMaxTjDate("acd_zhpfl_khq");
                tjDate = maxDate;
            }
            List<AcdZhpflKhq> data = reportTableService.getZhpflKhqData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取综合赔付率-客户群失败", e);
            return Result.error("获取综合赔付率-客户群失败", e);
        }
    }

    /** 车险结案率-部门 */
    @GetMapping("/pacll_bm/list")
    public Result<List<AcdPacllBm>> getPacllBmList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comname
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                String maxDate = reportTableService.getMaxTjDate("acd_pacll_bm");
                tjDate = maxDate;
            }
            List<AcdPacllBm> data = reportTableService.getPacllBmData(tjDate, comname);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取车险结案率-部门失败", e);
            return Result.error("获取车险结案率-部门失败", e);
        }
    }

    /** 车险结案率-小组 */
    @GetMapping("/pacll_xz/list")
    public Result<List<AcdPacllXz>> getPacllXzList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comname,
            @RequestParam(required = false) String groups
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                String maxDate = reportTableService.getMaxTjDate("acd_pacll_xz");
                tjDate = maxDate;
            }
            List<AcdPacllXz> data = reportTableService.getPacllXzData(tjDate, comname, groups);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取车险结案率-小组失败", e);
            return Result.error("获取车险结案率-小组失败", e);
        }
    }

    /** 车险结案率-人员 */
    @GetMapping("/pacll_ry/list")
    public Result<List<AcdPacllRy>> getPacllRyList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String bm,
            @RequestParam(required = false) String groups,
            @RequestParam(required = false) String username
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                String maxDate = reportTableService.getMaxTjDate("acd_pacll_ry");
                tjDate = maxDate;
            }
            List<AcdPacllRy> data = reportTableService.getPacllRyData(tjDate, bm, groups, username);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取车险结案率-人员失败", e);
            return Result.error("获取车险结案率-人员失败", e);
        }
    }

    // ==================== 事故年赔付率 ====================

    /** 事故年赔付率-支公司 */
    @GetMapping("/pflsgn_zgs/list")
    public Result<List<AcdPflsgnZgs>> getPflsgnZgsList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pflsgn_zgs");
            }
            List<AcdPflsgnZgs> data = reportTableService.getPflsgnZgsData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取事故年赔付率-支公司失败", e);
            return Result.error("获取事故年赔付率-支公司失败", e);
        }
    }

    /** 事故年赔付率-客户群 */
    @GetMapping("/pflsgn_khq/list")
    public Result<List<AcdPflsgnKhq>> getPflsgnKhqList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pflsgn_khq");
            }
            List<AcdPflsgnKhq> data = reportTableService.getPflsgnKhqData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取事故年赔付率-客户群失败", e);
            return Result.error("获取事故年赔付率-客户群失败", e);
        }
    }

    /** 事故年赔付率-新能源 */
    @GetMapping("/pflsgn_xny/list")
    public Result<List<AcdPflsgnXny>> getPflsgnXnyList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pflsgn_xny");
            }
            List<AcdPflsgnXny> data = reportTableService.getPflsgnXnyData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取事故年赔付率-新能源失败", e);
            return Result.error("获取事故年赔付率-新能源失败", e);
        }
    }

    // ==================== 案均赔款 ====================

    /** 案均赔款-支公司（车险） */
    @GetMapping("/anjun_cx_zgs/list")
    public Result<List<AcdAnjunCxZgs>> getAnjunCxZgsList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_anjun_cx_zgs");
            }
            List<AcdAnjunCxZgs> data = reportTableService.getAnjunCxZgsData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取案均赔款-支公司（车险）失败", e);
            return Result.error("获取案均赔款-支公司（车险）失败", e);
        }
    }

    @GetMapping("/anjun_cx_khq/list")
    public Result<List<AcdAnjunCxKhq>> getAnjunCxKhqList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_anjun_cx_khq");
            }
            List<AcdAnjunCxKhq> data = reportTableService.getAnjunCxKhqData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取案均赔款-客户群（车险）失败", e);
            return Result.error("获取案均赔款-客户群（车险）失败", e);
        }
    }

    @GetMapping("/anjun_cx_xny/list")
    public Result<List<AcdAnjunCxXny>> getAnjunCxXnyList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_anjun_cx_xny");
            }
            List<AcdAnjunCxXny> data = reportTableService.getAnjunCxXnyData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取案均赔款-新能源（车险）失败", e);
            return Result.error("获取案均赔款-新能源（车险）失败", e);
        }
    }

    // ==================== 年度每月系列 ====================

    /** 查勘量-年度每月 */
    @GetMapping("/chakan_year/list")
    public Result<List<AcdChakanYear>> getChakanYearList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_chakan_year");
            }
            List<AcdChakanYear> data = reportTableService.getChakanYearData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取查勘量-年度每月失败", e);
            return Result.error("获取查勘量-年度每月失败", e);
        }
    }

    /** 定损提交量-年度每月 */
    @GetMapping("/dingsun_tjl_year/list")
    public Result<List<AcdDingsunTjlYear>> getDingsunTjlYearList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_dingsun_tjl_year");
            }
            List<AcdDingsunTjlYear> data = reportTableService.getDingsunTjlYearData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取定损提交量-年度每月失败", e);
            return Result.error("获取定损提交量-年度每月失败", e);
        }
    }

    /** 定损完成量-年度每月 */
    @GetMapping("/dingsun_wcl_year/list")
    public Result<List<AcdDingsunWclYear>> getDingsunWclYearList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_dingsun_wcl_year");
            }
            List<AcdDingsunWclYear> data = reportTableService.getDingsunWclYearData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取定损完成量-年度每月失败", e);
            return Result.error("获取定损完成量-年度每月失败", e);
        }
    }

    /** 查勘量+定损完成-年度每月 */
    @GetMapping("/ck_dswc_year/list")
    public Result<List<AcdCkDswcYear>> getCkDswcYearList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_ck_dswc_year");
            }
            List<AcdCkDswcYear> data = reportTableService.getCkDswcYearData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取查勘量+定损完成-年度每月失败", e);
            return Result.error("获取查勘量+定损完成-年度每月失败", e);
        }
    }

    /** 定损支付量-年度每月 */
    @GetMapping("/dingsun_zfl_year/list")
    public Result<List<AcdDingsunZflYear>> getDingsunZflYearList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_dingsun_zfl_year");
            }
            List<AcdDingsunZflYear> data = reportTableService.getDingsunZflYearData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取定损支付量-年度每月失败", e);
            return Result.error("获取定损支付量-年度每月失败", e);
        }
    }

    /** 理算量-年度每月 */
    @GetMapping("/lisuan_year/list")
    public Result<List<AcdLisuanYear>> getLisuanYearList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_lisuan_year");
            }
            List<AcdLisuanYear> data = reportTableService.getLisuanYearData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取理算量-年度每月失败", e);
            return Result.error("获取理算量-年度每月失败", e);
        }
    }

    /** 人伤跟踪量-年度每月 */
    @GetMapping("/rs_gzl_year/list")
    public Result<List<AcdRsGzlYear>> getRsGzlYearList(
            @RequestParam(required = false) String tjDate
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                // 共享表 acd_rs_gzl_year：按 jaflag 区分"后续跟踪"/"人伤调解"，各自取段内最大日期
                tjDate = reportTableService.getMaxTjDateByTableAndFlag(
                        "acd_rs_gzl_year", "jaflag", "后续跟踪");
            }
            List<AcdRsGzlYear> data = reportTableService.getRsGzlYearData(tjDate);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取人伤跟踪量-年度每月失败", e);
            return Result.error("获取人伤跟踪量-年度每月失败", e);
        }
    }

    /** 人伤调解量-年度每月 */
    @GetMapping("/rs_tjl_year/list")
    public Result<List<AcdRsGzlYear>> getRsTjlYearList(
            @RequestParam(required = false) String tjDate
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                // 共享表 acd_rs_gzl_year：按 jaflag 区分"后续跟踪"/"人伤调解"，各自取段内最大日期
                tjDate = reportTableService.getMaxTjDateByTableAndFlag(
                        "acd_rs_gzl_year", "jaflag", "人伤调解");
            }
            List<AcdRsGzlYear> data = reportTableService.getRsTjlYearData(tjDate);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取人伤调解量-年度每月失败", e);
            return Result.error("获取人伤调解量-年度每月失败", e);
        }
    }

    /** 查勘量-月度每日 */
    @GetMapping("/chakan_month/list")
    public Result<List<AcdChakanMonth>> getChakanMonthList(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_chakan_month");
            }
            List<AcdChakanMonth> data = reportTableService.getChakanMonthData(tjDate, comnameSgs);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取查勘量-月度每日失败", e);
            return Result.error("获取查勘量-月度每日失败", e);
        }
    }

    // ==================== 分页端点（laoxiao 9 + chakan_month） ====================
    // 旧 /list 端点保持不动；新 /page 端点带 current/size 参数并返回 PageResult<T>。

    /** 查勘量-年度每月 - 分页 */
    @GetMapping("/chakan_year/page")
    public Result<PageResult<AcdChakanYear>> getChakanYearPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_chakan_year");
            }
            return Result.success(reportTableService.getChakanYearDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取查勘量-年度每月分页失败", e);
            return Result.error("获取查勘量-年度每月分页失败", e);
        }
    }

    /** 定损提交量-年度每月 - 分页 */
    @GetMapping("/dingsun_tjl_year/page")
    public Result<PageResult<AcdDingsunTjlYear>> getDingsunTjlYearPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_dingsun_tjl_year");
            }
            return Result.success(reportTableService.getDingsunTjlYearDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取定损提交量-年度每月分页失败", e);
            return Result.error("获取定损提交量-年度每月分页失败", e);
        }
    }

    /** 定损完成量-年度每月 - 分页 */
    @GetMapping("/dingsun_wcl_year/page")
    public Result<PageResult<AcdDingsunWclYear>> getDingsunWclYearPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_dingsun_wcl_year");
            }
            return Result.success(reportTableService.getDingsunWclYearDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取定损完成量-年度每月分页失败", e);
            return Result.error("获取定损完成量-年度每月分页失败", e);
        }
    }

    /** 查勘量+定损完成-年度每月 - 分页 */
    @GetMapping("/ck_dswc_year/page")
    public Result<PageResult<AcdCkDswcYear>> getCkDswcYearPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_ck_dswc_year");
            }
            return Result.success(reportTableService.getCkDswcYearDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取查勘量+定损完成-年度每月分页失败", e);
            return Result.error("获取查勘量+定损完成-年度每月分页失败", e);
        }
    }

    /** 定损支付量-年度每月 - 分页（已 1136 行重点） */
    @GetMapping("/dingsun_zfl_year/page")
    public Result<PageResult<AcdDingsunZflYear>> getDingsunZflYearPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_dingsun_zfl_year");
            }
            return Result.success(reportTableService.getDingsunZflYearDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取定损支付量-年度每月分页失败", e);
            return Result.error("获取定损支付量-年度每月分页失败", e);
        }
    }

    /** 理算量-年度每月 - 分页 */
    @GetMapping("/lisuan_year/page")
    public Result<PageResult<AcdLisuanYear>> getLisuanYearPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_lisuan_year");
            }
            return Result.success(reportTableService.getLisuanYearDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取理算量-年度每月分页失败", e);
            return Result.error("获取理算量-年度每月分页失败", e);
        }
    }

    /** 人伤跟踪量-年度每月 - 分页（无 comnameSgs 筛选） */
    @GetMapping("/rs_gzl_year/page")
    public Result<PageResult<AcdRsGzlYear>> getRsGzlYearPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                // 共享表 acd_rs_gzl_year：按 jaflag 区分"后续跟踪"/"人伤调解"，各自取段内最大日期
                tjDate = reportTableService.getMaxTjDateByTableAndFlag(
                        "acd_rs_gzl_year", "jaflag", "后续跟踪");
            }
            return Result.success(reportTableService.getRsGzlYearDataPage(
                    tjDate,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取人伤跟踪量-年度每月分页失败", e);
            return Result.error("获取人伤跟踪量-年度每月分页失败", e);
        }
    }

    /** 人伤调解量-年度每月 - 分页（无 comnameSgs 筛选） */
    @GetMapping("/rs_tjl_year/page")
    public Result<PageResult<AcdRsGzlYear>> getRsTjlYearPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                // 共享表 acd_rs_gzl_year：按 jaflag 区分"后续跟踪"/"人伤调解"，各自取段内最大日期
                tjDate = reportTableService.getMaxTjDateByTableAndFlag(
                        "acd_rs_gzl_year", "jaflag", "人伤调解");
            }
            return Result.success(reportTableService.getRsTjlYearDataPage(
                    tjDate,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取人伤调解量-年度每月分页失败", e);
            return Result.error("获取人伤调解量-年度每月分页失败", e);
        }
    }

    /** 查勘量-月度每日 - 分页 */
    @GetMapping("/chakan_month/page")
    public Result<PageResult<AcdChakanMonth>> getChakanMonthPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_chakan_month");
            }
            return Result.success(reportTableService.getChakanMonthDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取查勘量-月度每日分页失败", e);
            return Result.error("获取查勘量-月度每日分页失败", e);
        }
    }

    // ==================== 分页端点（lpcenter/efficiency 16 端点） ====================

    /** 人员日工作量 - 分页 */
    @GetMapping("/cur_gzl/page")
    public Result<PageResult<CurGzlTableRy>> getCurGzlPage(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String comName,
            @RequestParam(required = false) String groups,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if ((startDate == null || startDate.trim().isEmpty())
                    && (endDate == null || endDate.trim().isEmpty())) {
                String maxDate = reportTableService.getMaxTjDate("acd_dangri_gzl_ry");
                startDate = maxDate; endDate = maxDate;
            }
            if (endDate == null || endDate.trim().isEmpty()) endDate = startDate;
            return Result.success(reportTableService.getCurGzlDataPage(
                    startDate, endDate, comName, groups, userName,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取人员日工作量分页失败", e);
            return Result.error("获取人员日工作量分页失败", e);
        }
    }

    /** 部门日工作量 - 分页 */
    @GetMapping("/cur_gzl_bm/page")
    public Result<PageResult<CurGzlTableBm>> getCurGzlBmPage(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String comName,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if ((startDate == null || startDate.trim().isEmpty())
                    && (endDate == null || endDate.trim().isEmpty())) {
                String maxDate = reportTableService.getMaxTjDate("acd_dangri_gzl_bm");
                startDate = maxDate; endDate = maxDate;
            }
            if (endDate == null || endDate.trim().isEmpty()) endDate = startDate;
            return Result.success(reportTableService.getCurGzlDataBmPage(
                    startDate, endDate, comName,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取部门日工作量分页失败", e);
            return Result.error("获取部门日工作量分页失败", e);
        }
    }

    /** 小组日工作量 - 分页 */
    @GetMapping("/cur_gzl_group/page")
    public Result<PageResult<CurGzlTableGroup>> getCurGzlGroupPage(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String comName,
            @RequestParam(required = false) String groups,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if ((startDate == null || startDate.trim().isEmpty())
                    && (endDate == null || endDate.trim().isEmpty())) {
                String maxDate = reportTableService.getMaxTjDate("acd_dangri_gzl_group");
                startDate = maxDate; endDate = maxDate;
            }
            if (endDate == null || endDate.trim().isEmpty()) endDate = startDate;
            return Result.success(reportTableService.getCurGzlDataGroupPage(
                    startDate, endDate, comName, groups,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取小组日工作量分页失败", e);
            return Result.error("获取小组日工作量分页失败", e);
        }
    }

    /** 人伤日工作量 - 分页 */
    @GetMapping("/cur_gzl_rs/page")
    public Result<PageResult<CurGzlTableRs>> getCurGzlRsPage(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String comName,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if ((startDate == null || startDate.trim().isEmpty())
                    && (endDate == null || endDate.trim().isEmpty())) {
                String maxDate = reportTableService.getMaxTjDate("acd_dangri_gzl_rs");
                startDate = maxDate; endDate = maxDate;
            }
            if (endDate == null || endDate.trim().isEmpty()) endDate = startDate;
            return Result.success(reportTableService.getCurGzlDataRsPage(
                    startDate, endDate, comName,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取人伤日工作量分页失败", e);
            return Result.error("获取人伤日工作量分页失败", e);
        }
    }

    /** 周期-市公司 - 分页 */
    @GetMapping("/zhouqi_qs/page")
    public Result<PageResult<AcdZhouqiQs>> getZhouqiQsPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_zhouqi_qs");
            }
            return Result.success(reportTableService.getZhouqiQsDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取周期-市公司分页失败", e);
            return Result.error("获取周期-市公司分页失败", e);
        }
    }

    /** 周期-部门 - 分页 */
    @GetMapping("/zhouqi_bm/page")
    public Result<PageResult<AcdZhouqiBm>> getZhouqiBmPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comname,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_zhouqi_bm");
            }
            return Result.success(reportTableService.getZhouqiBmDataPage(
                    tjDate, comname,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取周期-部门分页失败", e);
            return Result.error("获取周期-部门分页失败", e);
        }
    }

    /** 综合赔付率-客户群 - 分页 */
    @GetMapping("/zhpfl_khq/page")
    public Result<PageResult<AcdZhpflKhq>> getZhpflKhqPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_zhpfl_khq");
            }
            return Result.success(reportTableService.getZhpflKhqDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取综合赔付率-客户群分页失败", e);
            return Result.error("获取综合赔付率-客户群分页失败", e);
        }
    }

    /** 车险结案率-部门 - 分页 */
    @GetMapping("/pacll_bm/page")
    public Result<PageResult<AcdPacllBm>> getPacllBmPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comname,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pacll_bm");
            }
            return Result.success(reportTableService.getPacllBmDataPage(
                    tjDate, comname,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取车险结案率-部门分页失败", e);
            return Result.error("获取车险结案率-部门分页失败", e);
        }
    }

    /** 车险结案率-小组 - 分页 */
    @GetMapping("/pacll_xz/page")
    public Result<PageResult<AcdPacllXz>> getPacllXzPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comname,
            @RequestParam(required = false) String groups,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pacll_xz");
            }
            return Result.success(reportTableService.getPacllXzDataPage(
                    tjDate, comname, groups,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取车险结案率-小组分页失败", e);
            return Result.error("获取车险结案率-小组分页失败", e);
        }
    }

    /** 车险结案率-人员 - 分页 */
    @GetMapping("/pacll_ry/page")
    public Result<PageResult<AcdPacllRy>> getPacllRyPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String bm,
            @RequestParam(required = false) String groups,
            @RequestParam(required = false) String username,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pacll_ry");
            }
            return Result.success(reportTableService.getPacllRyDataPage(
                    tjDate, bm, groups, username,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取车险结案率-人员分页失败", e);
            return Result.error("获取车险结案率-人员分页失败", e);
        }
    }

    /** 事故年赔付率-支公司 - 分页 */
    @GetMapping("/pflsgn_zgs/page")
    public Result<PageResult<AcdPflsgnZgs>> getPflsgnZgsPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pflsgn_zgs");
            }
            return Result.success(reportTableService.getPflsgnZgsDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取事故年赔付率-支公司分页失败", e);
            return Result.error("获取事故年赔付率-支公司分页失败", e);
        }
    }

    /** 事故年赔付率-客户群 - 分页 */
    @GetMapping("/pflsgn_khq/page")
    public Result<PageResult<AcdPflsgnKhq>> getPflsgnKhqPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pflsgn_khq");
            }
            return Result.success(reportTableService.getPflsgnKhqDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取事故年赔付率-客户群分页失败", e);
            return Result.error("获取事故年赔付率-客户群分页失败", e);
        }
    }

    /** 事故年赔付率-新能源 - 分页 */
    @GetMapping("/pflsgn_xny/page")
    public Result<PageResult<AcdPflsgnXny>> getPflsgnXnyPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_pflsgn_xny");
            }
            return Result.success(reportTableService.getPflsgnXnyDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取事故年赔付率-新能源分页失败", e);
            return Result.error("获取事故年赔付率-新能源分页失败", e);
        }
    }

    /** 案均赔款-支公司（车险）- 分页 */
    @GetMapping("/anjun_cx_zgs/page")
    public Result<PageResult<AcdAnjunCxZgs>> getAnjunCxZgsPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_anjun_cx_zgs");
            }
            return Result.success(reportTableService.getAnjunCxZgsDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取案均赔款-支公司（车险）分页失败", e);
            return Result.error("获取案均赔款-支公司（车险）分页失败", e);
        }
    }

    /** 案均赔款-客户群（车险）- 分页 */
    @GetMapping("/anjun_cx_khq/page")
    public Result<PageResult<AcdAnjunCxKhq>> getAnjunCxKhqPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_anjun_cx_khq");
            }
            return Result.success(reportTableService.getAnjunCxKhqDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取案均赔款-客户群（车险）分页失败", e);
            return Result.error("获取案均赔款-客户群（车险）分页失败", e);
        }
    }

    /** 案均赔款-新能源（车险）- 分页 */
    @GetMapping("/anjun_cx_xny/page")
    public Result<PageResult<AcdAnjunCxXny>> getAnjunCxXnyPage(
            @RequestParam(required = false) String tjDate,
            @RequestParam(required = false) String comnameSgs,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        try {
            if (tjDate == null || tjDate.trim().isEmpty()) {
                tjDate = reportTableService.getMaxTjDate("acd_anjun_cx_xny");
            }
            return Result.success(reportTableService.getAnjunCxXnyDataPage(
                    tjDate, comnameSgs,
                    current == null ? 1 : current,
                    size == null ? 20 : size));
        } catch (Exception e) {
            log.error("获取案均赔款-新能源（车险）分页失败", e);
            return Result.error("获取案均赔款-新能源（车险）分页失败", e);
        }
    }
}
