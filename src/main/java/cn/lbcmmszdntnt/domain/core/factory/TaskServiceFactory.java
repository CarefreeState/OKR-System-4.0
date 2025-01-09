package cn.lbcmmszdntnt.domain.core.factory;


import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.core.enums.TaskType;
import cn.lbcmmszdntnt.domain.core.service.TaskService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 16:40
 */
@Configuration
@ConfigurationProperties(prefix = "okr.service.task-service")
@Data
public class TaskServiceFactory {

    private Map<TaskType, String> map;

    public TaskService getService(TaskType taskType) {
        return SpringUtil.getBean(map.get(taskType), TaskService.class);
    }

}
