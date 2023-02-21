package com.abubusoft.xenon.mesh.persistence.androidxml;

import java.util.ArrayList;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;

@BindType
public class XmlSubmeshnames {

	@Bind("submesh")
	public ArrayList<XmlName> names;	
}
