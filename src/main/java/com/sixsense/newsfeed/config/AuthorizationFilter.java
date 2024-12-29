package com.sixsense.newsfeed.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.ErrorResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;
import static com.sixsense.newsfeed.constant.Url.isWhiteList;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest httpRequest,
                         ServletResponse httpResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) httpRequest;
        HttpServletResponse response = (HttpServletResponse) httpResponse;

        log.debug("Processing authorization for request: {}", request.getRequestURI());

        if (!isWhiteListRequest(request)) {
            if (!validateToken(request, response)) {
                return; // 토큰이 유효하지 않으면 필터 체인 중단
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 화이트리스트 요청인지 확인
     */
    private boolean isWhiteListRequest(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        boolean isWhitelisted = isWhiteList(requestUrl);
        if (isWhitelisted) {
            // 추가할 로직 있으면 추가
            log.debug("Request URL '{}' is whitelisted. Skipping token validation.", requestUrl);
        }
        return isWhitelisted;
    }

    /**
     * 토큰 검증
     */
    private boolean validateToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);

        if (accessToken == null) {
            log.error("Missing JWT token. Sending error response");
            sendErrorResponse(response, ErrorCode.TOKEN_NOT_FOUND, ErrorCode.TOKEN_NOT_FOUND.getMessage());
            return false;
        }

        if (tokenProvider.isValidToken(accessToken) == false) {
            log.error("Invalid JWT token. Sending error response.");
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
            return false;
        }

        log.debug("JWT token is valid for request.");
        return true;
    }

    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}