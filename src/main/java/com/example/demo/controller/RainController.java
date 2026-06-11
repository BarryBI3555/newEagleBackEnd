package com.example.demo.controller;

import com.example.demo.entity.RainDayLevel;
import com.example.demo.entity.RainCarPlace;
import com.example.demo.entity.RainZhiban;
import com.example.demo.entity.RainSaveRepair;
import com.example.demo.entity.RainLianluo;
import com.example.demo.entity.RainItems;
import com.example.demo.entity.RainLevelProcess;
import com.example.demo.entity.AcdShuiyancheCldHz;
import com.example.demo.entity.Result;
import com.example.demo.mapper.RainMapper;
import com.example.demo.service.ShuiyancheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rain")
@CrossOrigin("*")
public class RainController {

    @Autowired
    private RainMapper rainMapper;

    @Autowired
    private ShuiyancheService shuiyancheService;

    @GetMapping("/dayLevel")
    public Result<List<RainDayLevel>> dayLevel() {
        String today = LocalDate.now().toString();
        return Result.success(rainMapper.getDayLevel(today));
    }

    @GetMapping("/carPlace")
    public Result<List<RainCarPlace>> carPlace() {
        return Result.success(rainMapper.getCarPlace());
    }

    @GetMapping("/zhiban")
    public Result<List<RainZhiban>> zhiban() {
        String today = LocalDate.now().toString();
        return Result.success(rainMapper.getZhiban(today));
    }

    @GetMapping("/repair")
    public Result<List<RainSaveRepair>> repair() {
        return Result.success(rainMapper.getRepair());
    }

    @GetMapping("/lianluo")
    public Result<List<RainLianluo>> lianluo() {
        return Result.success(rainMapper.getLianluo());
    }

    @GetMapping("/items")
    public Result<List<RainItems>> items() {
        return Result.success(rainMapper.getItems());
    }

    /**
     * 某日期预警对应的措施列表
     * date 缺省时取今天（YYYY-MM-DD）
     */
    @GetMapping("/levelProcess")
    public Result<List<RainLevelProcess>> levelProcess(@RequestParam(required = false) String date) {
        if (date == null || date.trim().isEmpty()) {
            date = LocalDate.now().toString();
        }
        return Result.success(rainMapper.getLevelProcessByDate(date));
    }

    /**
     * 卡片展示数据（报案量-当日/累计、估计赔款、已赔付件数/金额）
     */
    @GetMapping("/cardData")
    public Result<AcdShuiyancheCldHz> cardData() {
        return Result.success(shuiyancheService.getCardData());
    }

    /**
     * 数据报表表格
     */
    @GetMapping("/reportTable")
    public Result<List<AcdShuiyancheCldHz>> reportTable() {
        return Result.success(shuiyancheService.getReportTableData());
    }
}
