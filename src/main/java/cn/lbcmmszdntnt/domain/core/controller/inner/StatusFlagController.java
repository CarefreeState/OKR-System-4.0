package cn.lbcmmszdntnt.domain.core.controller.inner;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.config.StatusFlagConfig;
import cn.lbcmmszdntnt.domain.core.model.converter.StatusFlagConverter;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.*;
import cn.lbcmmszdntnt.domain.core.model.entity.inner.StatusFlag;
import cn.lbcmmszdntnt.domain.core.model.message.operate.StatusFlagUpdate;
import cn.lbcmmszdntnt.domain.core.service.inner.StatusFlagService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.FourthQuadrantService;
import cn.lbcmmszdntnt.domain.core.util.OkrCoreUpdateMessageUtil;
import cn.lbcmmszdntnt.domain.okr.factory.OkrOperateServiceFactory;
import cn.lbcmmszdntnt.domain.okr.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:21
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/flag")
@Tag(name = "OKR 内核/内件/状态指标")
@Intercept
public class StatusFlagController {

    private final StatusFlagService statusFlagService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    private final FourthQuadrantService fourthQuadrantService;

    private final StatusFlagConfig statusFlagConfig;

    @PostMapping("/add")
    @Operation(summary = "增加一条状态指标")
    public SystemJsonResponse<?> addStatusFlag(@Valid @RequestBody OkrStatusFlagDTO okrStatusFlagDTO) {
        // 检查
        User user = InterceptorContext.getUser();
        StatusFlagDTO statusFlagDTO = okrStatusFlagDTO.getStatusFlagDTO();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrStatusFlagDTO.getScene());
        StatusFlag statusFlag = StatusFlagConverter.INSTANCE.statusFlagDTOToStatusFlag(statusFlagDTO);
        // 检测身份
        Long fourthQuadrantId = statusFlagDTO.getFourthQuadrantId();
        Long coreId = fourthQuadrantService.getFourthQuadrantCoreId(fourthQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        Long id = null;
        if(user.getId().equals(userId)) {
            // 插入
            id = statusFlagService.addStatusFlag(statusFlag);
            StatusFlagUpdate statusFlagUpdate = StatusFlagUpdate.builder().userId(userId).coreId(coreId).build();
            OkrCoreUpdateMessageUtil.sendStatusFlagUpdate(statusFlagUpdate);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS(id);
    }

    @PostMapping("/remove")
    @Operation(summary = "删除一条指标")
    public SystemJsonResponse<?> remove(@Valid @RequestBody OkrStatusFlagRemoveDTO okrStatusFlagRemoveDTO) {
        User user = InterceptorContext.getUser();
        Long statusFlagId = okrStatusFlagRemoveDTO.getId();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrStatusFlagRemoveDTO.getScene());
        // 检测身份
        Long fourthQuadrantId = statusFlagService.getFlagFourthQuadrantId(statusFlagId);
        Long coreId = fourthQuadrantService.getFourthQuadrantCoreId(fourthQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            statusFlagService.removeStatusFlag(statusFlagId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/update")
    @Operation(summary = "更新一条指标")
    public SystemJsonResponse<?> update(@Valid @RequestBody OkrStatusFlagUpdateDTO okrStatusFlagUpdateDTO) {
        // 检查
        User user = InterceptorContext.getUser();
        StatusFlagUpdateDTO statusFlagUpdateDTO = okrStatusFlagUpdateDTO.getStatusFlagUpdateDTO();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrStatusFlagUpdateDTO.getScene());
        StatusFlag statusFlag = StatusFlagConverter.INSTANCE.statusFlagUpdateDTOToStatusFlag(statusFlagUpdateDTO);
        Long statusFlagId = statusFlagUpdateDTO.getId();
        // 检测身份
        Long flagFourthQuadrantId = statusFlagService.getFlagFourthQuadrantId(statusFlagId);
        Long coreId = fourthQuadrantService.getFourthQuadrantCoreId(flagFourthQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            statusFlagService.updateStatusFlag(statusFlag);
            StatusFlagUpdate statusFlagUpdate = StatusFlagUpdate.builder().userId(userId).coreId(coreId).build();
            OkrCoreUpdateMessageUtil.sendStatusFlagUpdate(statusFlagUpdate);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/check")
    @Operation(summary = "检查当前用户的状态指标")
    public SystemJsonResponse<Boolean> updateKeyResult() {
        // 校验
        Long userId = InterceptorContext.getUser().getId();
        double average = statusFlagConfig.calculateStatusFlag(userId);
        boolean isTouch = statusFlagConfig.isTouch(average);
        log.info("检查用户 {} 状态指标 {}", userId, isTouch);
        return SystemJsonResponse.SYSTEM_SUCCESS(isTouch);
    }

}
