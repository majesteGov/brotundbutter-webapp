package com.hbv;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class MyFilter implements Filter {

    ServletContext servletContext;

    public void init(FilterConfig config) throws ServletException{
        servletContext =config.getServletContext();
    }

    public void doFliter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
        MyLogger.info("in doFilter");
        if(request instanceof HttpServletRequest){
            HttpServletRequest hsr =(HttpServletRequest) request ;
            String forwadedFor = hsr.getHeader("X-Forwarded-For");
            String requestURL = ""+hsr.getRequestURL();
        }
        chain.doFilter(request,response);
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }

    public void destroy(){}
}
