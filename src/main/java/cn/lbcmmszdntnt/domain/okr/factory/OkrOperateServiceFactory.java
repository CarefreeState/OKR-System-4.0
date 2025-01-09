package cn.lbcmmszdntnt.domain.okr.factory;


import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.okr.enums.OkrType;
import cn.lbcmmszdntnt.domain.okr.service.OkrOperateService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-04
 * Time: 10:53
 */
@Configuration
@ConfigurationProperties(prefix = "okr.service.okr-operate-service")
@Data
public class OkrOperateServiceFactory {

    private Map<OkrType, String> map;

    public OkrOperateService getService(OkrType okrType) {
        return SpringUtil.getBean(map.get(okrType), OkrOperateService.class);
    }

}
