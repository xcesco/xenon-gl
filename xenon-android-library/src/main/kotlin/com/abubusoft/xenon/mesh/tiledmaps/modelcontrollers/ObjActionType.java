package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers;

public enum ObjActionType {
	MOVE_UP(0,-1f),
	MOVE_RIGHT(1f,0f),
	MOVE_DOWN(0f,1f),
	MOVE_LEFT(-1f,0f),
	
	STAY_UP(0f,0f),
	STAY_RIGHT(0f,0f),
	STAY_DOWN(0f,0f),
	STAY_LEFT(0f,0f);
	
	public float x;
	
	public float y;
	
	ObjActionType(float x, float y)
	{
		this.x=x;
		this.y=y;
	}
	
}
