package com.jetbrains.ycjapp

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import compose.data.repos.GalleryFolder
import compose.data.repos.GalleryRepository
import compose.data.repos.MediaFile
import compose.data.repos.MediaType
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidGalleryRepository(private val context: Context) : GalleryRepository {

    override suspend fun getGalleryFolders(): List<GalleryFolder> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC"

        val foldersMap = mutableMapOf<String, GalleryFolder>()

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,  // No selection
            null,  // No selection args
            sortOrder
        )

        cursor?.use {
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getString(bucketIdColumn)
                val name = cursor.getString(bucketNameColumn)

                val folder = foldersMap[id]
                if (folder == null) {
                    foldersMap[id] = GalleryFolder(id, name, 1)
                } else {
                    foldersMap[id] = folder.copy(mediaCount = folder.mediaCount + 1)
                }
            }
        }

        // 최근 항목 폴더 추가
        addRecentFolder(foldersMap)

        // 즐겨찾기 항목을 추가
        addFavoriteFolder(foldersMap)

        // 폴더 목록 정렬: 최근 항목 -> 즐겨찾기 -> 파일 개수 DESC
        val folderList = foldersMap.values.sortedWith(
            compareByDescending<GalleryFolder> { it.id == "recent" }
                .thenByDescending { it.id == "favorites" }
                .thenByDescending { it.mediaCount }
        )

        val endTime = System.currentTimeMillis() // 종료 시간 기록
        val duration = endTime - startTime // 소요 시간 계산

        Napier.d("getGalleryFolders 소요 시간: $duration ms") // 소요 시간 로그 출력

        return@withContext folderList
    }

    private fun addRecentFolder(foldersMap: MutableMap<String, GalleryFolder>) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )
        // 현재는 200개로 설정
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            var count = 0
            while (cursor.moveToNext() && count < 200) {
                count++
            }
            if (count > 0) {
                foldersMap["recent"] = GalleryFolder("recent", "최근 항목", count)
            }
        }
    }
    private fun addFavoriteFolder(foldersMap: MutableMap<String, GalleryFolder>) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        val selection = "${MediaStore.Images.Media.IS_FAVORITE} = 1"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use {
            if (cursor.count > 0) {
                // "favorites" ID는 고유하게 설정할 수 있습니다.
                val favoritesFolderId = "favorites"
                val folder = foldersMap[favoritesFolderId]
                if (folder == null) {
                    foldersMap[favoritesFolderId] = GalleryFolder(favoritesFolderId, "즐겨찾기", cursor.count)
                }
            }
        }
    }

    override suspend fun getMediaFilesInFolder(folderId: String): List<MediaFile> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE
        )

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(folderId)
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val mediaFiles = mutableListOf<MediaFile>()

        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idColumn)
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val contentUri = ContentUris.withAppendedId(uri, id.toLong())

                val mediaType = when {
                    mimeType.startsWith("image") -> MediaType.IMAGE
                    mimeType.startsWith("video") -> MediaType.VIDEO
                    else -> continue
                }

                mediaFiles.add(MediaFile(id, name, contentUri.toString(), mediaType))
            }
        }

        return@withContext mediaFiles
    }
}