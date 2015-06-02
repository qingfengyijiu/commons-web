/**
 * 
 */
package com.zhangjx.commons.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.zhangjx.commons.io.Resources;
import com.zhangjx.commons.logging.Log;
import com.zhangjx.commons.logging.LogFactory;

/**
 * @author zhang jianxin
 * @date 2015-06-02
 */
public class ExternalStaticResourceLoaderFilter implements Filter {
	
	private static final Log log = LogFactory.getLog(ExternalStaticResourceLoaderFilter.class);
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String DEFAULT_EXTERNAL = "external";
	private static final String RESOURCE_FOLDER = "META-INF/";
	
	private String project;
	
	private String charset = DEFAULT_CHARSET;
	
	private String external = DEFAULT_EXTERNAL;
	
	private Map<String, SoftReference<String>> externalCache = new HashMap<String, SoftReference<String>>();

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// clear the cache
		for(Entry<String, SoftReference<String>> entry : externalCache.entrySet()) {
			String key = entry.getKey();
			SoftReference<String> softReference = entry.getValue();
			softReference.clear();
			externalCache.remove(key);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String uri = httpServletRequest.getRequestURI();
		log.debug("request external resource :" + uri);
		if(!project.startsWith("/")) {
			project = "/" + project;
		}
		if(!external.startsWith("/")) {
			external = "/" + external;
		}
		String prefix = project + external + "/";
		if(uri.startsWith(prefix)) {
			uri = uri.substring(prefix.length());
			
			// init response before get the printwriter
			String contentType = null;
			if(uri.endsWith(".js")) {
				contentType = "text/javascript";
			} else if(uri.endsWith(".css")) {
				contentType = "text/css";
			} else if(uri.endsWith(".json")) {
				contentType = "application/json";
			} else if(uri.endsWith(".html") || uri.endsWith(".htm")) {
				contentType = "text/html";
			} else {
				contentType = "application/object";
			}
			contentType += ";charset=" + charset;
			response.setContentType(contentType);
			response.setCharacterEncoding(charset);
			PrintWriter pw = response.getWriter();
			
			// determine whether to use cache
			if(externalCache.containsKey(uri)) {
				String cachedExternalContent = externalCache.get(uri).get();
				if(cachedExternalContent != null) {
					//use cache
					pw.write(cachedExternalContent);
					pw.close();
					return;
				}
			}
			// not use cache
			InputStream is = Resources.getResourceAsStream(RESOURCE_FOLDER + uri);
			try {
				StringBuffer sb = new StringBuffer();
				byte[] bt = new byte[1024];
				int len;
				while ((len = is.read(bt)) != -1) {
					sb.append(new String(bt, 0, len, DEFAULT_CHARSET));
				}
				String content = sb.toString();
				
				// cache the requested resource
				SoftReference<String> softReference = new SoftReference<String>(content);
				this.externalCache.put(uri, softReference);
				
				// output the resource to response
				pw.write(content);
				pw.close();
				return;
			} catch (Exception e) {
				log.warn("request external resource :" + uri + " failed");
				filterChain.doFilter(request, response);
			} finally {
				if(is != null) {
					is.close();
					is = null;
				}
				if(pw != null) {
					pw.close();
					pw = null;
				}
			}
		} else {
			filterChain.doFilter(request, response);
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String project = filterConfig.getInitParameter("project");
		String charset = filterConfig.getInitParameter("charset");
		String external = filterConfig.getInitParameter("external");
		if(project == null || project.length() == 0) {
			throw new IllegalArgumentException("argument 'project' should not be empty！");
		} else {
			this.project = project;
		}
		if(charset != null && charset.length() > 0) {
			this.charset = charset;
		}
		if(external != null && external.length() > 0) {
			this.external = external;
		}
	}

}
