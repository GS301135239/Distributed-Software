package org.example.distributedsoftwareserver.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.distributedsoftwareserver.Common.Result;
import org.example.distributedsoftwareserver.Entity.DTO.CreateGoodDTO;
import org.example.distributedsoftwareserver.Service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Component
@Tag(name = "商品管理")
@RequestMapping("/good")
public class GoodController {
    @Autowired
    private GoodService goodService;

    @Operation(summary = "获取商品列表")
    @GetMapping("/getGoodList")
    public Result<?> firstInit(HttpServletRequest request) {
        return goodService.getGoodList(request);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/getGoodByID")
    public Result<?> getGoodByID(@RequestParam Long goodId, HttpServletRequest request) {
        return goodService.getGoodByID(goodId, request);
    }

    @Operation(summary = "创建新商品")
    @PostMapping("/createGood")
    public Result<?> createGood(@RequestBody CreateGoodDTO createGoodDTO, HttpServletRequest request) {
        return goodService.createGood(createGoodDTO, request);
    }
}
