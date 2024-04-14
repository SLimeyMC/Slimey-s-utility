package io.github.priestoffern.vs_ship_assembler.rendering

import com.google.common.base.Supplier
import com.mojang.blaze3d.vertex.PoseStack

import net.minecraft.client.Camera
import net.minecraft.client.Minecraft

import org.valkyrienskies.mod.common.shipObjectWorld

object Renderer {
    var CurrentId:Long = 0;
    val toRender = mutableListOf<RenderingData>()

    fun addRender(renderData: RenderingData): RenderingData {
        renderData.id = CurrentId;
        CurrentId++

        toRender.add(renderData);
        return renderData;
    }

    fun removeRender(renderData: RenderingData) {
        toRender.remove(renderData);
    }
}



