package com.example.mini_project.mockData

import com.example.mini_project.R
import com.example.mini_project.model.Album

object MockData {

    private val data = listOf(
        Album(1, "After Hours", "The Weeknd", 59.90, R.drawable.after_hours),
        Album(2, "Dawn FM", "The Weeknd", 62.90, R.drawable.dawn_fm),
        Album(3, "Starboy", "The Weeknd", 54.90, R.drawable.starboy),
        Album(4, "Hurry Up Tomorrow", "The Weeknd", 69.90, R.drawable.hurry_up_tomorrow),
        Album(5, "ASTROWORLD", "Travis Scott", 64.90, R.drawable.astroworld),
        Album(6, "Birds in the Trap", "Travis Scott", 58.90, R.drawable.birds),
        Album(7, "UTOPIA", "Travis Scott", 69.90, R.drawable.utopia),
        Album(8, "Scorpion", "Drake", 61.90, R.drawable.scorpion),
        Album(9, "Ice Man", "Drake", 57.90, R.drawable.ice_man),
        Album(10, "Take Care", "Drake", 55.90, R.drawable.take_care),
        Album(11, "Luv Is Rage 2", "Lil Uzi Vert", 63.90, R.drawable.luv_is_rage_2),
        Album(12, "Eternal Atake", "Lil Uzi Vert", 66.90, R.drawable.eternal_atake),
        Album(13, "Pink Tape", "Lil Uzi Vert", 72.90, R.drawable.pink_tape),
        Album(14, "House of Balloons", "The Weeknd", 49.90, R.drawable.house_of_balloons),
        Album(15, "Thursday", "The Weeknd", 49.90, R.drawable.thursday),
        Album(16, "Echoes of Silence", "The Weeknd", 49.90, R.drawable.echoes_of_silence),
        Album(17, "Kiss Land", "The Weeknd", 56.90, R.drawable.kiss_land),
        Album(18, "Beauty Behind the Madness", "The Weeknd", 59.90, R.drawable.beauty_behind_the_madness),
        Album(19, "My Dear Melancholy,", "The Weeknd", 44.90, R.drawable.my_dear_melancholy),
        Album(20, "The Highlights", "The Weeknd", 52.90, R.drawable.the_highlights),
        Album(21, "Trilogy", "The Weeknd", 79.90, R.drawable.trilogy),
        Album(22, "My World 2.0", "Justin Bieber", 49.90, R.drawable.my_world_2_0),
        Album(23, "Believe", "Justin Bieber", 54.90, R.drawable.believe),
        Album(24, "Changes", "Justin Bieber", 56.90, R.drawable.changes),
        Album(25, "Justice", "Justin Bieber", 62.90, R.drawable.justice),
        Album(26, "I Will Survive", "Gloria Gaynor", 47.90, R.drawable.i_will_survive)
    )

    fun populateData(): List<Album> = data
}