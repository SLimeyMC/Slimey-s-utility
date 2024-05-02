package io.github.slimeyar.slimeys_utility.client.renderer

object Renderer {
    var CurrentId:Long = 0
    val toRender = mutableListOf<RenderingData>()
    val toRemove = mutableListOf<RenderingData>()
    fun addRender(renderData: RenderingData): RenderingData {
        renderData.id = CurrentId
        CurrentId++

        toRender.add(renderData)
        return renderData
    }

    fun removeRender(renderData: RenderingData) {
        toRemove.add(renderData)

    }
}



