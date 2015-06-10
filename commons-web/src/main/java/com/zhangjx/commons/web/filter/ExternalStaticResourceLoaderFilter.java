/**
 * 
 */
package com.zhangjx.commons.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	private static Map<String, String> DEFAULT_MAPPINGS = new HashMap<String, String>();
	
	static {
		DEFAULT_MAPPINGS.put("html", "text/html");
		DEFAULT_MAPPINGS.put("htm", "text/html");
		DEFAULT_MAPPINGS.put("js", "text/javascript");
		DEFAULT_MAPPINGS.put("css", "text/css");
		DEFAULT_MAPPINGS.put("json", "application/json");
		DEFAULT_MAPPINGS.put("xml", "text/xml");
		DEFAULT_MAPPINGS.put("png", "image/png");
		DEFAULT_MAPPINGS.put("jpg", "image/x-jpg");
		DEFAULT_MAPPINGS.put("jpeg", "image/jpeg");
		DEFAULT_MAPPINGS.put("jpe", "image/jpeg");
		DEFAULT_MAPPINGS.put("gif", "image/gif");
		DEFAULT_MAPPINGS.put("ico", "image/x-icon");
	}
	
	private String project;
	
	private String charset = DEFAULT_CHARSET;
	
	private String external = DEFAULT_EXTERNAL;
	
	private Map<String, SoftReference<byte[]>> externalCache = new HashMap<String, SoftReference<byte[]>>();

	private Map<String, String> mimeMapping = new HashMap<String, String>();
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// clear the cache
		for(Entry<String, SoftReference<byte[]>> entry : externalCache.entrySet()) {
			String key = entry.getKey();
			SoftReference<byte[]> softReference = entry.getValue();
			softReference.clear();
			externalCache.remove(key);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String uri = request.getRequestURI();
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
			
			// init response before get the outputStream
			String contentType = this._mappingContentType(uri);
			contentType += ";charset=" + charset;
			response.setContentType(contentType);
			response.setCharacterEncoding(charset);
			OutputStream os = null;
			
			// determine whether to use cache
			if(externalCache.containsKey(uri)) {
				byte[] cachedExternalContent = externalCache.get(uri).get();
				if(cachedExternalContent != null) {
					//use cache
					response.setContentLength(cachedExternalContent.length);
					os = response.getOutputStream();
					os.write(cachedExternalContent);
					return;
				}
			}
			// not use cache
			InputStream is = Resources.getResourceAsStream(RESOURCE_FOLDER + uri);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byte[] bt = new byte[1024];
				int len;
				while ((len = is.read(bt)) != -1) {
					baos.write(bt, 0, len);
				}
				byte[] content = baos.toByteArray();
				
				// cache the requested resource
				SoftReference<byte[]> softReference = new SoftReference<byte[]>(content);
				this.externalCache.put(uri, softReference);
				
				// output the resource to response
				response.setContentLength(baos.toByteArray().length);
				os = response.getOutputStream();
				baos.writeTo(os);
				baos.close();
				return;
			} catch (Exception e) {
				log.warn("request external resource :" + uri + " failed");
				filterChain.doFilter(request, response);
			} finally {
				if(is != null) {
					is.close();
					is = null;
				}
				if(baos != null) {
					baos.close();
					baos = null;
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
		String mimeMapping = filterConfig.getInitParameter("mimeMapping");
		if(project == null || project.length() == 0) {
			throw new IllegalArgumentException("argument 'project' should not be empty！");
		} else {
			this.project = project;
		}
		if(charset != null && charset.length() > 0) {
			this.charset = charset;
		}
		this.mimeMapping.putAll(DEFAULT_MAPPINGS);
		Map<String, String> mimeMappingMap = _formatMimeMapping(mimeMapping);
		this.mimeMapping.putAll(mimeMappingMap);
	}
	
	private Map<String, String> _formatMimeMapping(String mimeMapping) {
		Map<String, String> mappings = new HashMap<String, String>();
		if(mimeMapping == null) return mappings;
		// mimeMapping format is "name : value , name : value....."
		String format = "(.+:.+,)*.+:.+";
		Pattern pattern = Pattern.compile(format);
		Matcher matcher = pattern.matcher(mimeMapping);
		boolean valid = matcher.matches();
		// if the format of the parameter is invlaid, return empty map directly
		if(!valid) return mappings;
		
		// if the format of the parameter is valid, process
		String[] mappingItems = mimeMapping.split(",");
		for(String mapping : mappingItems) {
			String[] items = mapping.split(":");
			String mimeName = items[0].trim();
			String mappingName = items[1].trim();
			mappings.put(mimeName, mappingName);
		}
		return mappings;
	}
	
	private String _mappingContentType(String uri) {
		String contentType = DEFAULT_CONTENT_TYPE;
		for(Entry<String, String> entry : mimeMapping.entrySet()) {
			String mimeName = entry.getKey();
			String mappingName = entry.getValue();
			if(uri.endsWith("." + mimeName)) {
				contentType = mappingName;
				break;
			}
		}
		return contentType;
	}

}
