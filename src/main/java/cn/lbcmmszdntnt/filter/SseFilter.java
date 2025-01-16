package cn.lbcmmszdntnt.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 15:33
 */
@Component
@WebFilter(urlPatterns = "/sse/**") // 约定 sse 请求必须有 /sse 前缀
public class SseFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, "text/event-stream");
        httpResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        httpResponse.setHeader(HttpHeaders.CONNECTION, "keep-alive");
        chain.doFilter(request, response);
    }
}
