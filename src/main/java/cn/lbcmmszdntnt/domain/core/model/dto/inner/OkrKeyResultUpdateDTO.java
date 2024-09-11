package cn.lbcmmszdntnt.domain.core.model.dto.inner;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.KeyResultUpdateDTO;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-27
 * Time: 0:41
 */
@Schema(description = "更新关键结果所需数据")
@Data
public class OkrKeyResultUpdateDTO {

    @SchemaProperty(name = "场景")
    private String scene;

    @SchemaProperty(name = "更新关键结果的数据")
    private KeyResultUpdateDTO keyResultUpdateDTO;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(keyResultUpdateDTO)) {
            messageBuilder.append("\n-> 更新关键结果的数据 为 null");
        }
        if(!StringUtils.hasText(scene)) {
            messageBuilder.append("\n-> 缺少场景值");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
