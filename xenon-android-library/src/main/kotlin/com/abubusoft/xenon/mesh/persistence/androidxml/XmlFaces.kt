package com.abubusoft.xenon.mesh.persistence.androidxml;

import java.util.ArrayList;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;


@BindType
public class XmlFaces {

	@Bind("face")
	public ArrayList<XmlFace> list;
}
