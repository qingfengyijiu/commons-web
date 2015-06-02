package com.zhangjx.commons.web.tree;

import java.util.List;

public interface ITreeNode {

	boolean isFolder();
	
	void setFolder(boolean folder);
	
	List<ITreeNode> getChildren();
	
	void setChildren(List<ITreeNode> children);
	
	void addChild(ITreeNode child);
	
	String getKey();
	
	void setKey(String key);
	
	String getTitle();
	
	void setTitle(String title);
	
	String getTooltip();
	
	void setTooltip(String tooltip);
	
	boolean isLazy();
	
	void setLazy(boolean lazy);
	
	String getHref();
	
	void setHref(String href);
	
	String getExtraClasses();
	
	void setExtraClasses(String extraClasses);
	
	boolean getExpanded();
	
	void setExpanded(boolean expanded);
	
}
