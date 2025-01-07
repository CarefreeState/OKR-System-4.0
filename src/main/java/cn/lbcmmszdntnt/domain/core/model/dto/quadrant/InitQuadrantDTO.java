package cn.lbcmmszdntnt.domain.core.model.dto.quadrant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 20:07
 */
@Schema(description = "初始化第二第三象限数据")
@Data
public class InitQuadrantDTO {

    @Schema(description = "象限 ID")
    @NotNull(message = "象限 ID 不能为空")
    private Long id;

    @Schema(description = "截止时间")
    @NotNull(message = "截止时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    @Schema(description = "象限周期")
    @NotNull(message = "象限周期不能为空")
    @Min(value = 0, message = "象限周期必须大于 0")
    private Integer quadrantCycle;

}