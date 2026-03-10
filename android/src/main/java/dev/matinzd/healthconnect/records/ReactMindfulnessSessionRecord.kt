package dev.matinzd.healthconnect.records

import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.aggregate.AggregationResultGroupedByDuration
import androidx.health.connect.client.aggregate.AggregationResultGroupedByPeriod
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.records.MindfulnessSessionRecord
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import dev.matinzd.healthconnect.utils.convertMetadataFromJSMap
import dev.matinzd.healthconnect.utils.convertMetadataToJSMap
import dev.matinzd.healthconnect.utils.getSafeInt
import dev.matinzd.healthconnect.utils.toMapList
import java.time.Instant
import java.time.ZoneOffset

class ReactMindfulnessSessionRecord : ReactHealthRecordImpl<MindfulnessSessionRecord> {

  override fun parseWriteRecord(records: ReadableArray): List<MindfulnessSessionRecord> {
    return records.toMapList().map { map ->
      MindfulnessSessionRecord(
        startTime = Instant.parse(map.getString("startTime")),
        startZoneOffset = map.getString("startZoneOffset")?.let { ZoneOffset.of(it) },
        endTime = Instant.parse(map.getString("endTime")),
        endZoneOffset = map.getString("endZoneOffset")?.let { ZoneOffset.of(it) },
        mindfulnessSessionType = map.getSafeInt(
          "mindfulnessSessionType",
          MindfulnessSessionRecord.MINDFULNESS_SESSION_TYPE_UNKNOWN
        ),
        title = map.getString("title"),
        notes = map.getString("notes"),
        metadata = convertMetadataFromJSMap(map.getMap("metadata"))
      )
    }
  }

  override fun parseRecord(record: MindfulnessSessionRecord): WritableNativeMap {
    return WritableNativeMap().apply {
      putString("startTime", record.startTime.toString())
      putString("endTime", record.endTime.toString())
      record.startZoneOffset?.let { putString("startZoneOffset", it.id) }
      record.endZoneOffset?.let { putString("endZoneOffset", it.id) }
      putInt("mindfulnessSessionType", record.mindfulnessSessionType)
      record.title?.let { putString("title", it) }
      record.notes?.let { putString("notes", it) }
      putMap("metadata", convertMetadataToJSMap(record.metadata))
    }
  }

  // MindfulnessSession has no aggregatable metrics — these are required by the interface
  // but not applicable to this record type.

  override fun getAggregateRequest(record: ReadableMap): AggregateRequest {
    throw UnsupportedOperationException("Aggregation is not supported for MindfulnessSession")
  }

  override fun getAggregateGroupByDurationRequest(record: ReadableMap): AggregateGroupByDurationRequest {
    throw UnsupportedOperationException("Aggregation is not supported for MindfulnessSession")
  }

  override fun getAggregateGroupByPeriodRequest(record: ReadableMap): AggregateGroupByPeriodRequest {
    throw UnsupportedOperationException("Aggregation is not supported for MindfulnessSession")
  }

  override fun parseAggregationResult(record: AggregationResult): WritableNativeMap {
    return WritableNativeMap()
  }

  override fun parseAggregationResultGroupedByDuration(
    record: List<AggregationResultGroupedByDuration>
  ): WritableNativeArray {
    return WritableNativeArray()
  }

  override fun parseAggregationResultGroupedByPeriod(
    record: List<AggregationResultGroupedByPeriod>
  ): WritableNativeArray {
    return WritableNativeArray()
  }
}
