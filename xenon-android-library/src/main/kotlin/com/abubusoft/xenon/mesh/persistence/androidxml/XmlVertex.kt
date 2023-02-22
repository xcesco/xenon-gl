package com.abubusoft.xenon.mesh.persistence.androidxml;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;

@BindType
public class XmlVertex {

	@Bind("position")
	public XmlPosition position;
	
	@Bind("normal")
	public XmlPosition normal;
	
	@Bind("texcoord")
	public XmlTexCoord texcoord;
}
