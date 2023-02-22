package com.abubusoft.xenon.render

import com.abubusoft.xenon.camera.Camera

interface SceneDrawer {
    fun drawScene(camera: Camera?, enlapsedTime: Long, speedAdapter: Float)
}