package com.jetbrains.ycjapp

import android.annotation.SuppressLint
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

    override suspend fun getMediaFilesInFolder(folderId: String): List<MediaFile> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE ,
            MediaStore.Images.Media.DATA
        )
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val mediaFiles = mutableListOf<MediaFile>()

        val selection: String?
        val selectionArgs: Array<String>?
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

        if (folderId == "recent") {
            selection = null  // 전체 데이터를 대상으로 쿼리
            selectionArgs = null
        }else {
            selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"  // 특정 폴더만 쿼리
            selectionArgs = arrayOf(folderId)
        }

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
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idColumn)
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val contentUri = ContentUris.withAppendedId(uri, id.toLong())
                val filePath = cursor.getString(dataColumn)

                val mediaType = when {
                    mimeType.startsWith("image") -> MediaType.IMAGE
                    mimeType.startsWith("video") -> MediaType.VIDEO
                    else -> continue
                }

                mediaFiles.add(MediaFile(id, name, contentUri.toString(), filePath, mediaType))
            }
        }

        return@withContext mediaFiles
    }

    override suspend fun getFoldersWithRecentImages(): List<GalleryFolder> = withContext(Dispatchers.IO) {
        // 1. 폴더별 파일 개수를 먼저 가져옴
        val folderCounts = getFolderFileCounts()

        // 2. 폴더별 최근 4개의 이미지를 가져옴
        val recentImagesMap = getRecentImagesInFolders()

        // 3. 두 데이터를 합쳐서 GalleryFolder 객체 생성
        val folderList = folderCounts.map { (bucketId, folderData) ->
            val (folderName, mediaCount) = folderData
            val recentImages = recentImagesMap[bucketId] ?: emptyList()

            GalleryFolder(
                id = bucketId,
                name = folderName,  // 폴더 이름은 추가 로직 필요
                recentImages = recentImages,
                mediaCount = mediaCount
            )
        }

        // 폴더 목록 정렬: recent 폴더 -> favorites 폴더 -> 나머지 폴더는 파일 개수 DESC
        return@withContext folderList.sortedWith(
            compareByDescending<GalleryFolder> { it.id == "recent" }
                .thenByDescending { it.id == "favorites" }
                .thenByDescending { it.mediaCount }
        )
    }

    // 폴더 별 개수 Count를 수행할 함수
    suspend fun getFolderFileCounts(): Map<String, Pair<String, Int>> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val folderCounts = mutableMapOf<String, Pair<String, Int>>()

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn)
                val bucketName = cursor.getString(bucketDisplayNameColumn)

                // 폴더 ID를 키로 하여 개수 카운트
                val currentCount = folderCounts[bucketId]?.second ?: 0
                folderCounts[bucketId] = Pair(bucketName, currentCount + 1)
            }
        }

        // 최근 폴더 추가 (전체 이미지 개수)
        val recentCursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            null,
            null,
            null
        )

        recentCursor?.use {
            folderCounts["recent"] = Pair("최근", it.count)
        }

        // 즐겨찾기 폴더 추가 (IS_FAVORITE 필터 적용)
        val favoritesCursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            "${MediaStore.Images.Media.IS_FAVORITE} = 1",
            null,
            null
        )

        favoritesCursor?.use {
            folderCounts["favorites"] = Pair("즐겨찾기", it.count)
        }

        return@withContext folderCounts
    }

    // 최근 4개의 이미지를 Load할 함수
    suspend fun getRecentImagesInFolders(): Map<String, List<MediaFile>> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATA
        )

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

        val recentImagesMap = mutableMapOf<String, MutableList<MediaFile>>()

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn)
                val id = cursor.getString(idColumn)
                val name = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val contentUri = ContentUris.withAppendedId(uri, id.toLong())
                val filePath = cursor.getString(dataColumn)

                val mediaType = when {
                    mimeType.startsWith("image") -> MediaType.IMAGE
                    mimeType.startsWith("video") -> MediaType.VIDEO
                    else -> continue
                }

                val mediaFile = MediaFile(id, name, contentUri.toString(), filePath, mediaType)

                // 각 폴더에 최대 4개의 이미지만 추가
                val imageList = recentImagesMap.getOrPut(bucketId) { mutableListOf() }
                if (imageList.size < 4) {
                    imageList.add(mediaFile)
                }
            }
        }
        // 최근 폴더 추가 (최근 4개 이미지 가져오기)
        val recentCursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        recentCursor?.use {
            val recentImageList = mutableListOf<MediaFile>()
            val idColumn = recentCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = recentCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = recentCursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dataColumn = recentCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (recentCursor.moveToNext() && recentImageList.size < 4) {
                val id = recentCursor.getString(idColumn)
                val name = recentCursor.getString(nameColumn)
                val mimeType = recentCursor.getString(mimeTypeColumn)
                val contentUri = ContentUris.withAppendedId(uri, id.toLong())
                val filePath = recentCursor.getString(dataColumn)

                val mediaType = when {
                    mimeType.startsWith("image") -> MediaType.IMAGE
                    mimeType.startsWith("video") -> MediaType.VIDEO
                    else -> continue
                }

                recentImageList.add(MediaFile(id, name, contentUri.toString(), filePath, mediaType))
            }
            recentImagesMap["recent"] = recentImageList
        }

        // 즐겨찾기 폴더 추가 (즐겨찾기 4개 이미지 가져오기)
        val favoritesCursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Images.Media.IS_FAVORITE} = 1",
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        favoritesCursor?.use {
            val favoritesImageList = mutableListOf<MediaFile>()
            val idColumn = favoritesCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = favoritesCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeTypeColumn = favoritesCursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dataColumn = favoritesCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (favoritesCursor.moveToNext() && favoritesImageList.size < 4) {
                val id = favoritesCursor.getString(idColumn)
                val name = favoritesCursor.getString(nameColumn)
                val mimeType = favoritesCursor.getString(mimeTypeColumn)
                val contentUri = ContentUris.withAppendedId(uri, id.toLong())
                val filePath = favoritesCursor.getString(dataColumn)

                val mediaType = when {
                    mimeType.startsWith("image") -> MediaType.IMAGE
                    mimeType.startsWith("video") -> MediaType.VIDEO
                    else -> continue
                }

                favoritesImageList.add(MediaFile(id, name, contentUri.toString(), filePath, mediaType))
            }
            recentImagesMap["favorites"] = favoritesImageList
        }

        return@withContext recentImagesMap
    }
}