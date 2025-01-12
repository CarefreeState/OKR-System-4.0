package cn.lbcmmszdntnt.domain.okr.service;

import cn.lbcmmszdntnt.domain.core.model.vo.OKRCreateVO;
import cn.lbcmmszdntnt.domain.okr.model.entity.TeamOkr;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamOkrStatisticVO;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamOkrVO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_okr(团队 OKR 表)】的数据库操作Service
* @createDate 2024-01-20 02:25:52
*/
public interface TeamOkrService extends IService<TeamOkr> {

    List<TeamOkr> selectChildTeams(Long id);

    TeamOkr findRootTeam(Long id);

    List<TeamOkrVO> getTeamOkrList(User user);

    void checkManager(Long teamId, Long managerId);

    @Transactional
    OKRCreateVO grantTeamForMember(Long teamId, Long managerId, Long userId, String teamName);

    List<TeamOkrStatisticVO> countCompletionRate(List<Long> ids);

    void deleteTeamNameCache(Long teamId);

}