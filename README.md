# JEE Tracker (JEE Main 2027)

An offline-first Material 3 (Material You) Android application built with Kotlin, Jetpack Compose, Room, and Preferences DataStore to track JEE Main 2027 syllabus completion, revision readiness, and daily study pace.

## Syllabus Source

- **Exam:** JEE (Main) 2027 — Paper 1 (B.E./B.Tech.)
- **Official Source:** National Testing Agency (NTA)
  - Portal: `https://jeemain.nta.nic.in/syllabus/`
  - Information Bulletin (Session 1): `https://cdnbbsr.s3waas.gov.in/s3ntaedun/uploads/2026/10/2026-10-24-19.pdf`
- **Date Fetched:** `2026-11-24`
- **Default Exam Date:** `2027-01-20` (JEE Main 2027 Session 1 scheduled for January 20–24 & 28, 2027; editable inside Settings).
- **Total Units:** **54 Official Units**
  - **Physics:** 20 Units (`phy_01` – `phy_20`)
  - **Chemistry:** 20 Units (`chem_01` – `chem_20`) grouped into **Physical** (Units 1–8), **Inorganic** (Units 9–12), and **Organic** (Units 13–20)
  - **Maths:** 14 Units (`math_01` – `math_14`)

## Updating `assets/syllabus.json`

The syllabus is bundled offline at `app/src/main/assets/syllabus.json` and parsed at runtime by `SyllabusAssetLoader`:

```json
{
  "examName": "JEE (Main) 2027 - Paper 1 (B.E./B.Tech.)",
  "syllabusYear": 2027,
  "sourceName": "National Testing Agency (NTA)",
  "sourceUrl": "https://jeemain.nta.nic.in/syllabus/",
  "bulletinUrl": "...",
  "fetchedDate": "2026-11-24",
  "session1ExamStartDate": "2027-01-20",
  "session1ExamDatesNote": "...",
  "subjects": [
    {
      "id": "physics | chemistry | maths",
      "name": "Physics | Chemistry | Maths",
      "branches": [
        {
          "id": "branch_id",
          "name": "Physical | Organic | Inorganic | All Units",
          "chapters": [
            {
              "id": "unique_stable_chapter_id",
              "unitNumber": 1,
              "name": "Chapter Name",
              "topics": ["Topic 1", "Topic 2"]
            }
          ]
        }
      ]
    }
  ]
}
```

### Guidelines when editing `syllabus.json`
1. Keep each chapter's `"id"` stable (`phy_01`, `chem_01`, `math_01`, etc.) so existing Room progress rows stay linked to the same chapter.
2. For Chemistry, preserve the three branch objects (`"physical"`, `"organic"`, `"inorganic"`) so the 3-tab Chemistry screen stays populated.
3. Update `"syllabusYear"`, `"sourceUrl"`, and `"fetchedDate"` whenever NTA publishes a newer syllabus revision.
