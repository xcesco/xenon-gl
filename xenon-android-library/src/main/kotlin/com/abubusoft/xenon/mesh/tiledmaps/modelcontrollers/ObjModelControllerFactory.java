package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers;

import java.util.Locale;

import com.abubusoft.xenon.animations.TiledMapAnimation;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.core.util.ResourceUtility;

import android.content.Context;

public class ObjModelControllerFactory {

	/**
	 * <p>Crea un obj controller partendo da due array di stringhe.</p>
	 * 
	 * @param context
	 * 			context
	 * @param objName
	 * 			nome del player
	 * @param spriteBaseName
	 * 			nome base dell'animazione
	 * @param map
	 * 			tiledmape
	 * @param controllerOptions
	 * 			opzioni
	 * @return
	 */
	public static ObjModelController create(Context context, String objName, String spriteBaseName,TiledMap map, ObjModelControllerOptions controllerOptions)
	{
		ObjActionType type;
		
		String keys[]=ResourceUtility.resolveArrayOfString(context, controllerOptions.resourceKeys);
		String values[]=ResourceUtility.resolveArrayOfString(context, controllerOptions.resourceValues);
		
		if (keys.length!=values.length)
		{
			throw new RuntimeException("Can not create ObjModelController, keys and values are not compatible!");
		}
		
		ObjDefinition obj=ObjModelController.objectFind(map,objName);
		ObjModelController ret=new ObjModelController(obj, map, keys.length, MeshOptions.build());
		
		// definiamo per ogni key
		for(int i=0; i<keys.length;i++)
		{
			type=ObjActionType.valueOf(keys[i].toUpperCase(Locale.ENGLISH));
			if (type==null) throw new RuntimeException("Unknown movement "+keys[i].toUpperCase(Locale.ENGLISH));
			
			TiledMapAnimation animation=new TiledMapAnimation();
			animation.setAnimation(type.x*map.tileWidth, type.y*map.tileHeight, spriteBaseName+"_"+type.toString().toLowerCase(Locale.ENGLISH));
			// impostiamo il loop a false
			animation.setLoop(false);
			
			ret.animations.set(type.ordinal(), animation);
		}
		
		// animazione iniziale
		if (controllerOptions.status!=null)
		{
			ret.timeline.add(ret.animations.get(controllerOptions.status.ordinal()), true);
		} 
		
		return ret;
	}
}
