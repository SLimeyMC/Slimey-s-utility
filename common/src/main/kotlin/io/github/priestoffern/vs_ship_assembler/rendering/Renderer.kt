package io.github.priestoffern.vs_ship_assembler.rendering

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



