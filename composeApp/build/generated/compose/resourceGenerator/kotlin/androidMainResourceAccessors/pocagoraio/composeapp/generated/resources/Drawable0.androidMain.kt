@file:OptIn(InternalResourceApi::class)

package pocagoraio.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceItem

private const val MD: String = "composeResources/pocagoraio.composeapp.generated.resources/"

internal val Res.drawable.compose_multiplatform: DrawableResource by lazy {
      DrawableResource("drawable:compose_multiplatform", setOf(
        ResourceItem(setOf(), "${MD}drawable/compose-multiplatform.xml", -1, -1),
      ))
    }

internal val Res.drawable.`compose_multiplatform 2`: DrawableResource by lazy {
      DrawableResource("drawable:compose_multiplatform 2", setOf(
        ResourceItem(setOf(), "${MD}drawable/compose-multiplatform 2.xml", -1, -1),
      ))
    }

@InternalResourceApi
internal fun _collectAndroidMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("compose_multiplatform", Res.drawable.compose_multiplatform)
  map.put("compose_multiplatform 2", Res.drawable.`compose_multiplatform 2`)
}
