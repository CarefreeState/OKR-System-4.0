package cn.lbcmmszdntnt.domain.okr.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.okr.model.mapper.TeamPersonalOkrMapper;
import cn.lbcmmszdntnt.domain.okr.model.po.TeamOkr;
import cn.lbcmmszdntnt.domain.okr.model.po.TeamPersonalOkr;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamPersonalOkrVO;
import cn.lbcmmszdntnt.domain.okr.service.MemberService;
import cn.lbcmmszdntnt.domain.okr.util.TeamOkrUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 21:45
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final static String USER_TEAM_MEMBER = "userTeamMember:";
    private final static Long USER_TEAM_MEMBER_TTL = 30L;

    private final static TimeUnit USER_TEAM_MEMBER_TTL_UNIT = TimeUnit.DAYS;

    private final TeamPersonalOkrMapper teamPersonalOkrMapper;

    private final RedisCache redisCache;

    @Override
    public Boolean findExistsInTeam(List<Long> ids, Long userId) {
        return teamPersonalOkrMapper.getTeamPersonalOkrList(userId).stream()
                .parallel()
                .map(TeamPersonalOkrVO::getTeamId)
                .anyMatch(ids::contains);
    }

    @Override
    public void checkExistsInTeam(Long teamId, Long userId) {
        Boolean isExists = isExistsInTeam(teamId, userId);
        if(Boolean.FALSE.equals(isExists)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MEMBER);
        }
    }

    @Override
    public Boolean isExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = USER_TEAM_MEMBER + rootId;
       return (Boolean) redisCache.getCacheMapValue(redisKey, userId).orElseGet(() -> {
            List<Long> ids = TeamOkrUtil.getChildIds(rootId);
            Boolean isExists = findExistsInTeam(ids, userId);
            redisCache.getCacheMap(redisKey).orElseGet(() -> {
                Map<Long, Boolean> data = new HashMap<>();
                redisCache.setCacheMap(redisKey, data, USER_TEAM_MEMBER_TTL, USER_TEAM_MEMBER_TTL_UNIT);
                return null;
            });
           redisCache.setCacheMapValue(redisKey, userId, isExists);
            return isExists;
        });
    }

    @Override
    public Boolean haveExtendTeam(Long teamId, Long userId) {
        TeamOkr teamOkr = Db.lambdaQuery(TeamOkr.class)
                .eq(TeamOkr::getParentTeamId, teamId)
                .eq(TeamOkr::getManagerId, userId)
                .one();
        return Objects.nonNull(teamOkr);
    }

    @Override
    public void setExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = USER_TEAM_MEMBER + rootId;
        redisCache.setCacheMapValue(redisKey, userId, Boolean.TRUE);
    }

    @Override
    public void setNotExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = USER_TEAM_MEMBER + rootId;
        redisCache.setCacheMapValue(redisKey, userId, Boolean.FALSE);
    }

    @Override
    public void removeMember(Long teamId, Long memberOkrId, Long userId) {
        // 判断是否扩展了
        Boolean isExtend = haveExtendTeam(teamId, userId);
        if(Boolean.TRUE.equals(isExtend)) {
            // 无法删除
            throw new GlobalServiceException(GlobalServiceStatusCode.MEMBER_CANNOT_REMOVE);
        }
        // 删除
        Db.lambdaUpdate(TeamPersonalOkr.class)
                .eq(TeamPersonalOkr::getId, memberOkrId)
                .remove();
        // 设置为不存在
        setNotExistsInTeam(teamId, userId);
    }

}
