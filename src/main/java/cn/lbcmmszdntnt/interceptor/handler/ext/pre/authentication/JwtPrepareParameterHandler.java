package cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication;

import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import cn.lbcmmszdntnt.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 13:54
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtPrepareParameterHandler extends InterceptorHandler {

    @Override
    public Boolean condition() {
        return !InterceptorContext.isAuthenticated() && !StringUtils.hasText(InterceptorContext.getJwt());
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String jwt = JwtUtil.getJwtFromParameter(request);
        InterceptorContext.setJwt(jwt);
    }
}
