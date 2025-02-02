package de.laures.cewolf.taglib.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @author glaures
 */
public abstract class CewolfTag extends TagSupport {

	protected final int doAfterEndTag(int returnVal) {
		reset();
		return returnVal;
	}

	protected abstract void reset();

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		return doAfterEndTag(super.doEndTag());
	}

}
