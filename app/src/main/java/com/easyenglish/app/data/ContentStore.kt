package com.easyenglish.app.data

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

data class DownloadPack(val id: String, val title: String, val description: String, val url: String, val type: String, val sha256: String)

/** No se realiza ninguna solicitud de red sin pulsar Buscar o Descargar. */
class ContentStore(context: Context) {
    private val root = File(context.filesDir, "downloaded_content").apply { mkdirs() }
    private val base = "https://raw.githubusercontent.com/miguelpinto9988-byte/Easy-English-/main/content/"
    private val safeId = Regex("^[a-z0-9-]{1,48}$")
    private val validHash = Regex("^[a-f0-9]{64}$")
    private val maxBytes = 8 * 1024 * 1024

    suspend fun fetchCatalog(): List<DownloadPack> = withContext(Dispatchers.IO) {
        val data = JSONObject(String(fetch(base + "manifest.json", 256 * 1024), Charsets.UTF_8))
        val items = data.getJSONArray("items")
        require(items.length() <= 100)
        (0 until items.length()).map { index ->
            val item = items.getJSONObject(index)
            DownloadPack(
                item.getString("id"),
                item.getString("title").take(80),
                item.optString("description").take(220),
                item.getString("url"),
                item.getString("type"),
                item.getString("sha256").lowercase()
            ).also { validate(it) }
        }
    }

    private fun validate(pack: DownloadPack) {
        require(safeId.matches(pack.id) && validHash.matches(pack.sha256))
        require(pack.type == "lesson" || pack.type == "audio")
        require(pack.url.startsWith(base + "packs/"))
        require(pack.url.endsWith(if (pack.type == "lesson") ".json" else ".mp3"))
        require(URL(pack.url).protocol == "https")
    }

    private fun fileFor(pack: DownloadPack): File {
        validate(pack)
        return File(root, pack.id + (if (pack.type == "lesson") ".json" else ".mp3"))
    }

    fun hasPack(pack: DownloadPack): Boolean = try {
        val file = fileFor(pack)
        file.isFile && file.length() <= maxBytes && sha(file.readBytes()) == pack.sha256
    } catch (_: Exception) { false }

    suspend fun download(pack: DownloadPack) = withContext(Dispatchers.IO) {
        validate(pack)
        val bytes = fetch(pack.url, maxBytes)
        require(sha(bytes) == pack.sha256)
        if (pack.type == "lesson") {
            val obj = JSONObject(String(bytes, Charsets.UTF_8))
            require(obj.has("title") && obj.has("description"))
        }
        val file = fileFor(pack)
        val temp = File(root, pack.id + ".tmp")
        try {
            temp.writeBytes(bytes)
            require(temp.renameTo(file))
        } finally { temp.delete() }
    }

    fun describeLesson(pack: DownloadPack): String = try {
        if (!hasPack(pack) || pack.type != "lesson") "Todavía no descargado"
        else JSONObject(fileFor(pack).readText()).optString("description", "Lección disponible sin internet")
    } catch (_: Exception) { "No se pudo leer la lección" }

    fun playAudio(pack: DownloadPack) {
        require(pack.type == "audio" && hasPack(pack))
        val player = MediaPlayer()
        try {
            player.setDataSource(fileFor(pack).absolutePath)
            player.setOnCompletionListener { it.release() }
            player.setOnErrorListener { mp, _, _ -> mp.release(); true }
            player.prepare()
            player.start()
        } catch (e: Exception) { player.release(); throw e }
    }

    private fun fetch(link: String, maximum: Int): ByteArray {
        require(link.startsWith(base))
        val c = URL(link).openConnection() as HttpURLConnection
        try {
            c.instanceFollowRedirects = false
            c.connectTimeout = 10000
            c.readTimeout = 15000
            c.requestMethod = "GET"
            require(c.responseCode == HttpURLConnection.HTTP_OK)
            require(c.contentLengthLong in -1L..maximum.toLong())
            val stream = java.io.ByteArrayOutputStream()
            c.inputStream.use { input ->
                val chunk = ByteArray(8192)
                while (true) {
                    val n = input.read(chunk)
                    if (n < 0) break
                    require(stream.size() + n <= maximum)
                    stream.write(chunk, 0, n)
                }
            }
            return stream.toByteArray()
        } finally { c.disconnect() }
    }

    private fun sha(data: ByteArray) = MessageDigest.getInstance("SHA-256")
        .digest(data).joinToString("") { "%02x".format(it) }
}
