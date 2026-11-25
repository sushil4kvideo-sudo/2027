package com.example.data.local

import android.content.Context
import com.example.data.model.BranchSpec
import com.example.data.model.ChapterSpec
import com.example.data.model.ImportanceTier
import com.example.data.model.SubjectSpec
import com.example.data.model.SyllabusData
import org.json.JSONObject

class SyllabusAssetLoader(private val context: Context) {

    fun loadSyllabus(): SyllabusData {
        val rawJson = context.assets.open("syllabus.json").bufferedReader().use { it.readText() }
        val root = JSONObject(rawJson)

        val subjectsArray = root.getJSONArray("subjects")
        val subjects = buildList {
            for (sIdx in 0 until subjectsArray.length()) {
                val subjectObj = subjectsArray.getJSONObject(sIdx)
                val branchesArray = subjectObj.getJSONArray("branches")
                val branches = buildList {
                    for (bIdx in 0 until branchesArray.length()) {
                        val branchObj = branchesArray.getJSONObject(bIdx)
                        val chaptersArray = branchObj.getJSONArray("chapters")
                        val chapters = buildList {
                            for (cIdx in 0 until chaptersArray.length()) {
                                val chapterObj = chaptersArray.getJSONObject(cIdx)
                                val topicsArray = chapterObj.getJSONArray("topics")
                                val topics = buildList {
                                    for (tIdx in 0 until topicsArray.length()) {
                                        add(topicsArray.getString(tIdx))
                                    }
                                }
                                val tierString = chapterObj.optString("importanceTier", "").ifBlank { null }
                                val tier = tierString?.let { runCatching { ImportanceTier.valueOf(it) }.getOrNull() }
                                val weightage = chapterObj.optString("weightageTag", "").ifBlank { null }
                                add(
                                    ChapterSpec(
                                        id = chapterObj.getString("id"),
                                        unitNumber = chapterObj.getInt("unitNumber"),
                                        name = chapterObj.getString("name"),
                                        topics = topics,
                                        isImportant = chapterObj.optBoolean("isImportant", false),
                                        importanceTier = tier,
                                        weightageTag = weightage
                                    )
                                )
                            }
                        }
                        add(
                            BranchSpec(
                                id = branchObj.getString("id"),
                                name = branchObj.getString("name"),
                                chapters = chapters
                            )
                        )
                    }
                }
                add(
                    SubjectSpec(
                        id = subjectObj.idOrString("id"),
                        name = subjectObj.getString("name"),
                        branches = branches
                    )
                )
            }
        }

        return SyllabusData(
            examName = root.getString("examName"),
            syllabusYear = root.getInt("syllabusYear"),
            sourceName = root.getString("sourceName"),
            sourceUrl = root.getString("sourceUrl"),
            bulletinUrl = root.getString("bulletinUrl"),
            fetchedDate = root.getString("fetchedDate"),
            session1ExamStartDate = root.getString("session1ExamStartDate"),
            session1ExamDatesNote = root.getString("session1ExamDatesNote"),
            subjects = subjects
        )
    }

    private fun JSONObject.idOrString(key: String): String = getString(key)
}
