package cn.lbcmmszdntnt.domain.media.config;

import cn.lbcmmszdntnt.common.util.juc.threadpool.IOThreadPool;
import cn.lbcmmszdntnt.domain.media.model.entity.DigitalResource;
import cn.lbcmmszdntnt.domain.media.service.DigitalResourceService;
import cn.lbcmmszdntnt.domain.media.service.ObjectStorageService;
import cn.lbcmmszdntnt.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 17:41
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ClearDigitalResourceXxlJobConfig {

    private final static String AUTHOR = "macaku";
    private final static String ROUTE = "ROUND";
    private final static String CRON = "0 0 * * * ? *"; // 每小时
    private final static int RIGGER_STATUS = 1;

    private final DigitalResourceService digitalResourceService;

    private final ObjectStorageService objectStorageService;

    @XxlJob(value = "clearDigitalResource")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR, triggerStatus = RIGGER_STATUS, jobDesc = "【固定任务】清除不活跃的资源")
    private void clearDigitalResource() {
        long now = System.currentTimeMillis();
        List<DigitalResource> list = digitalResourceService.lambdaQuery()
                .ge(DigitalResource::getActiveLimit, 0L)
                .list()
                .stream()
                .filter(digitalResource -> digitalResource.getActiveLimit().compareTo(now - digitalResource.getUpdateTime().getTime()) <= 0)
                .toList();
        IOThreadPool.operateBatch(
                list.stream().map(DigitalResource::getCode).toList(),
                digitalResourceService::removeResource
        );
        IOThreadPool.operateBatch(
                list.stream().map(DigitalResource::getFileName).toList(),
                objectStorageService::remove
        );
    }

}
