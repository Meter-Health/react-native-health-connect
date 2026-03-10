package dev.matinzd.healthconnect.permissions

import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableNativeArray
import dev.matinzd.healthconnect.utils.InvalidRecordType
import dev.matinzd.healthconnect.utils.reactRecordTypeToClassMap

class PermissionUtils {
  companion object {
fun parsePermissions(reactPermissions: ReadableArray): Set<String> {
  return reactPermissions.toArrayList().mapNotNull {
    it as HashMap<*, *>
    val recordType = it["recordType"]
    val accessType = it["accessType"]

    if (accessType == "write" && recordType == "ExerciseRoute") {
      android.util.Log.d("HC_PERMS", "recordType=ExerciseRoute accessType=write permission=${HealthPermission.PERMISSION_WRITE_EXERCISE_ROUTE}")
      return@mapNotNull HealthPermission.PERMISSION_WRITE_EXERCISE_ROUTE
    }

    if (accessType == "read" && recordType == "ReadHealthDataHistory") {
      android.util.Log.d("HC_PERMS", "recordType=ReadHealthDataHistory accessType=read permission=${HealthPermission.PERMISSION_READ_HEALTH_DATA_HISTORY}")
      return@mapNotNull HealthPermission.PERMISSION_READ_HEALTH_DATA_HISTORY
    }

    if (accessType == "read" && recordType == "BackgroundAccessPermission") {
      android.util.Log.d("HC_PERMS", "recordType=BackgroundAccessPermission accessType=read permission=${HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND}")
      return@mapNotNull HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND
    }

    val recordClass = reactRecordTypeToClassMap[recordType] ?: throw InvalidRecordType()

    val permission = when (accessType) {
      "write" -> HealthPermission.getWritePermission(recordClass)
      "read" -> HealthPermission.getReadPermission(recordClass)
      else -> null
    }

    android.util.Log.d(
      "HC_PERMS",
      "recordType=$recordType accessType=$accessType recordClass=${recordClass.qualifiedName} permission=$permission"
    )

    permission
  }.toSet()
}

suspend fun getGrantedPermissions(permissionController: PermissionController): WritableNativeArray {
  val granted = permissionController.getGrantedPermissions()
  android.util.Log.d("HC_PERMS", "Granted permissions=$granted")
  return mapPermissionResult(granted)
}

    fun mapPermissionResult(grantedPermissions: Set<String>): WritableNativeArray {
      return WritableNativeArray().apply {
        // Handle regular permissions
        for ((recordType, recordClass) in reactRecordTypeToClassMap) {
          val readPermissionForRecord = HealthPermission.getReadPermission(recordClass)
          val writePermissionForRecord = HealthPermission.getWritePermission(recordClass)

          if (grantedPermissions.contains(readPermissionForRecord)) {
            pushMap(ReactPermission(AccessType.READ, recordType).toReadableMap())
          }

          if (grantedPermissions.contains(writePermissionForRecord)) {
            pushMap(ReactPermission(AccessType.WRITE, recordType).toReadableMap())
          }
        }

        // Handle special permissions
        if (grantedPermissions.contains(HealthPermission.PERMISSION_WRITE_EXERCISE_ROUTE)) {
          pushMap(ReactPermission(AccessType.WRITE, "ExerciseRoute").toReadableMap())
        }

        if (grantedPermissions.contains(HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND)
        ) {
          pushMap(ReactPermission(AccessType.READ, "BackgroundAccessPermission").toReadableMap())
        }
      }
    }
  }
}
