package com.zhangjx.commons.web.tree.fancytree;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.zhangjx.commons.web.tree.BaseTreeNode;
import com.zhangjx.commons.web.tree.ITreeNode;

/**
 * Fancytree jquery plugin node model in backend
 * @author zhang jianxin
 * @date 2015-06-02
 */
public class FancyTreeNode extends BaseTreeNode {
	
	@JsonProperty
	private String key;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private boolean folder;
	
	@JsonProperty
	private boolean lazy;
	
	@JsonProperty
	private String tooltip;
	
	@JsonProperty
	private String href;
	
	@JsonProperty
	private String extraClasses;
	
	@JsonProperty
	private boolean expanded;
	
	@JsonProperty
	private List<ITreeNode> children;
	
}

