/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tivi.data.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import app.tivi.data.entities.Season

@DatabaseView(
    viewName = "followed_next_to_watch",
    value = """
        SELECT
          fs.show_id AS show_id,
          s.id AS season_id,
          eps.id AS episode_id,
          MIN((1000 * s.number) + eps.number) AS next_ep_to_watch_abs_number
        FROM
          myshows_entries as fs
          INNER JOIN seasons AS s ON fs.show_id = s.show_id
          INNER JOIN episodes AS eps ON eps.season_id = s.id
          LEFT JOIN episode_watch_entries as ew ON ew.episode_id = eps.id
          LEFT JOIN followed_last_watched AS lw ON lw.id = fs.id
        WHERE
          s.number != ${Season.NUMBER_SPECIALS}
          AND s.ignored = 0
          AND watched_at IS NULL
          AND datetime(first_aired) < datetime('now')
          AND ((1000 * s.number) + eps.number) > coalesce(last_watched_abs_number, 0)
        GROUP BY fs.id
    """,
)
data class FollowedShowsNextToWatch(
    @ColumnInfo(name = "show_id") val showId: Long,
    @ColumnInfo(name = "season_id") val seasonId: Long,
    @ColumnInfo(name = "episode_id") val episodeId: Long,
)
