package cn.lbcmmszdntnt.domain.medal.factory.locator;


import cn.lbcmmszdntnt.domain.medal.factory.TeamAchievementServiceFactory;
import cn.lbcmmszdntnt.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 15:47
 */
@Component
public class TeamAchievementServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        super.setServiceLocatorInterface(TeamAchievementServiceFactory.class);
        super.setServiceMappings(PROPERTIES.getTeamAchievementServiceMap());
    }
}
