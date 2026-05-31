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
import com.example.demo.entity.Result;
import com.example.demo.service.ReportTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ReportTableController {

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
            return Result.error("获取失败：" + e.getMessage());
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
            return Result.error("获取部门统计失败：" + e.getMessage());
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
            return Result.error("获取小组统计失败：" + e.getMessage());
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
            return Result.error("获取住院门诊统计失败：" + e.getMessage());
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
            return Result.error("获取周期-市公司失败：" + e.getMessage());
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
            return Result.error("获取周期-部门失败：" + e.getMessage());
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
            return Result.error("获取综合赔付率-客户群失败：" + e.getMessage());
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
            return Result.error("获取车险结案率-部门失败：" + e.getMessage());
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
            return Result.error("获取车险结案率-小组失败：" + e.getMessage());
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
            return Result.error("获取车险结案率-人员失败：" + e.getMessage());
        }
    }
}