/**
 * 
 */
package com.zhangjx.commons.web.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang jianxin
 * @date 2015
 */
public class BaseTreeNode implements ITreeNode {
	
	private String key;
	
	private String title;
	
	private boolean folder;
	
	private boolean lazy;
	
	private String tooltip;
	
	private String href;
	
	private String extraClasses;
	
	private boolean expanded;
	
	private List<ITreeNode> children;

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#isFolder()
	 */
	@Override
	public boolean isFolder() {
		return this.folder;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setFolder(boolean)
	 */
	@Override
	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#getChildren()
	 */
	@Override
	public List<ITreeNode> getChildren() {
		return this.children;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setChildren(java.util.List)
	 */
	@Override
	public void setChildren(List<ITreeNode> children) {
		this.children = children;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#addChild(com.zhangjx.commons.web.tree.ITreeNode)
	 */
	@Override
	public void addChild(ITreeNode child) {
		if(this.children == null) {
			this.children = new ArrayList<ITreeNode>();
		}
		this.children.add(child);
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#getKey()
	 */
	@Override
	public String getKey() {
		return this.key;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setKey(java.lang.String)
	 */
	@Override
	public void setKey(String key) {
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#getTitle()
	 */
	@Override
	public String getTitle() {
		return this.title;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#getTooltip()
	 */
	@Override
	public String getTooltip() {
		return this.tooltip;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setTooltip(java.lang.String)
	 */
	@Override
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#isLazy()
	 */
	@Override
	public boolean isLazy() {
		return this.lazy;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setLazy(boolean)
	 */
	@Override
	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#getHref()
	 */
	@Override
	public String getHref() {
		return this.href;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setHref(java.lang.String)
	 */
	@Override
	public void setHref(String href) {
		this.href = href;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#getExtraClasses()
	 */
	@Override
	public String getExtraClasses() {
		return this.extraClasses;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setExtraClasses(java.lang.String)
	 */
	@Override
	public void setExtraClasses(String extraClasses) {
		this.extraClasses = extraClasses;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#getExpanded()
	 */
	@Override
	public boolean getExpanded() {
		return this.expanded;
	}

	/* (non-Javadoc)
	 * @see com.zhangjx.commons.web.tree.ITreeNode#setExpanded(boolean)
	 */
	@Override
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

}
