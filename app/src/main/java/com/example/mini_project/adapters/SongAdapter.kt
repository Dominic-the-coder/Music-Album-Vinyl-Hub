package com.example.mini_project.adapters

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.R
import com.example.mini_project.backend.SongDTO

class SongAdapter(
    private var songs: List<SongDTO>
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    private var playingPosition =
        RecyclerView.NO_POSITION

    private val handler =
        Handler(Looper.getMainLooper())

    /*
     * Stores the real duration of each song.
     * Duration is stored in seconds.
     */
    private val calculatedDurations =
        mutableMapOf<Int, Int>()

    /*
     * Stores the current playback position
     * of each song in seconds.
     */
    private val currentPositions =
        mutableMapOf<Int, Int>()

    class SongViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val trackNumber: TextView =
            itemView.findViewById(
                R.id.txtTrackNumber
            )

        val songName: TextView =
            itemView.findViewById(
                R.id.txtSongName
            )

        val songArtist: TextView =
            itemView.findViewById(
                R.id.txtSongArtist
            )

        val songDuration: TextView =
            itemView.findViewById(
                R.id.txtSongDuration
            )

        val previewButton: ImageButton =
            itemView.findViewById(
                R.id.btnPreview
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SongViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_song,
                    parent,
                    false
                )

        return SongViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int
    ) {

        val song =
            songs[position]

        holder.trackNumber.text =
            (position + 1).toString()

        holder.songName.text =
            song.title

        holder.songArtist.text =
            song.artist

        /*
         * If this song is currently playing,
         * show ONLY elapsed time.
         *
         * Example:
         * 0:15
         *
         * Otherwise show total duration.
         *
         * Example:
         * 3:33
         */
        if (position == playingPosition) {

            val currentTime =
                currentPositions[position] ?: 0

            holder.songDuration.text =
                formatDuration(currentTime)

        } else {

            val totalDuration =
                calculatedDurations[position]
                    ?: song.duration

            holder.songDuration.text =
                formatDuration(totalDuration)
        }

        /*
         * Play / Pause icon.
         */
        if (
            position == playingPosition &&
            mediaPlayer != null &&
            mediaPlayer!!.isPlaying
        ) {

            holder.previewButton.setImageResource(
                R.drawable.ic_pause
            )

        } else {

            holder.previewButton.setImageResource(
                R.drawable.ic_play
            )
        }

        holder.previewButton.setOnClickListener {

            val currentPosition =
                holder.bindingAdapterPosition

            if (
                currentPosition ==
                RecyclerView.NO_POSITION
            ) {
                return@setOnClickListener
            }

            playSong(
                songs[currentPosition],
                currentPosition
            )
        }
    }

    override fun getItemCount(): Int =
        songs.size


    private fun playSong(
        song: SongDTO,
        position: Int
    ) {

        /*
         * Clicking the currently playing song
         * pauses or resumes it.
         */
        if (
            position == playingPosition &&
            mediaPlayer != null
        ) {

            if (mediaPlayer!!.isPlaying) {

                mediaPlayer!!.pause()

                stopProgressUpdates()

            } else {

                mediaPlayer!!.start()

                startProgressUpdates()
            }

            notifyItemChanged(position)

            return
        }

        /*
         * Stop any previous song.
         */
        stopMusic()

        val audioUrl =
            song.audioUrl

        if (audioUrl.isNullOrBlank()) {
            return
        }

        val player =
            MediaPlayer()

        mediaPlayer =
            player

        try {

            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(
                        AudioAttributes.CONTENT_TYPE_MUSIC
                    )
                    .setUsage(
                        AudioAttributes.USAGE_MEDIA
                    )
                    .build()
            )

            player.setDataSource(
                audioUrl
            )

            /*
             * Audio is prepared.
             */
            player.setOnPreparedListener {

                /*
                 * MediaPlayer duration is
                 * returned in milliseconds.
                 */
                val durationMs =
                    player.duration

                val durationSeconds =
                    durationMs / 1000

                if (durationSeconds > 0) {

                    calculatedDurations[position] =
                        durationSeconds
                }

                /*
                 * Start elapsed time at 0.
                 */
                currentPositions[position] =
                    0

                playingPosition =
                    position

                /*
                 * Start playback.
                 */
                player.start()

                /*
                 * Start updating the elapsed time.
                 */
                startProgressUpdates()

                notifyItemChanged(position)
            }

            /*
             * Song finished.
             */
            player.setOnCompletionListener {

                stopProgressUpdates()

                val oldPosition =
                    playingPosition

                /*
                 * Reset playback time.
                 */
                if (
                    oldPosition !=
                    RecyclerView.NO_POSITION
                ) {

                    currentPositions[
                        oldPosition
                    ] = 0
                }

                playingPosition =
                    RecyclerView.NO_POSITION

                if (mediaPlayer === player) {
                    mediaPlayer = null
                }

                player.release()

                /*
                 * Refresh the row.
                 *
                 * It will now show the total duration
                 * again.
                 */
                if (
                    oldPosition !=
                    RecyclerView.NO_POSITION
                ) {

                    notifyItemChanged(
                        oldPosition
                    )
                }
            }

            /*
             * Playback error.
             */
            player.setOnErrorListener {
                    mp,
                    _,
                    _ ->

                stopProgressUpdates()

                val oldPosition =
                    playingPosition

                playingPosition =
                    RecyclerView.NO_POSITION

                if (
                    oldPosition !=
                    RecyclerView.NO_POSITION
                ) {

                    currentPositions[
                        oldPosition
                    ] = 0
                }

                if (mediaPlayer === mp) {
                    mediaPlayer = null
                }

                mp.release()

                if (
                    oldPosition !=
                    RecyclerView.NO_POSITION
                ) {

                    notifyItemChanged(
                        oldPosition
                    )
                }

                true
            }

            /*
             * Prepare without blocking the UI.
             */
            player.prepareAsync()

        } catch (e: Exception) {

            if (mediaPlayer === player) {
                mediaPlayer = null
            }

            player.release()

            playingPosition =
                RecyclerView.NO_POSITION
        }
    }


    /*
     * Continuously update the elapsed
     * playback time.
     */
    private val progressRunnable =
        object : Runnable {

            override fun run() {

                val player =
                    mediaPlayer

                val position =
                    playingPosition

                if (
                    player != null &&
                    position !=
                    RecyclerView.NO_POSITION &&
                    player.isPlaying
                ) {

                    /*
                     * currentPosition is milliseconds.
                     */
                    val currentSeconds =
                        player.currentPosition / 1000

                    currentPositions[position] =
                        currentSeconds

                    /*
                     * Update only the currently
                     * playing row.
                     */
                    notifyItemChanged(position)

                    /*
                     * Update again after 500ms.
                     */
                    handler.postDelayed(
                        this,
                        500
                    )
                }
            }
        }


    private fun startProgressUpdates() {

        handler.removeCallbacks(
            progressRunnable
        )

        handler.post(
            progressRunnable
        )
    }


    private fun stopProgressUpdates() {

        handler.removeCallbacks(
            progressRunnable
        )
    }


    /*
     * Completely stop the music.
     */
    fun stopMusic() {

        stopProgressUpdates()

        val oldPosition =
            playingPosition

        val player =
            mediaPlayer

        mediaPlayer =
            null

        playingPosition =
            RecyclerView.NO_POSITION

        /*
         * Reset displayed playback time.
         */
        if (
            oldPosition !=
            RecyclerView.NO_POSITION
        ) {

            currentPositions[
                oldPosition
            ] = 0
        }

        if (player != null) {

            try {
                player.stop()
            } catch (_: Exception) {
            }

            try {
                player.release()
            } catch (_: Exception) {
            }
        }

        /*
         * Refresh the old row so it shows
         * the total duration again.
         */
        if (
            oldPosition !=
            RecyclerView.NO_POSITION
        ) {

            notifyItemChanged(
                oldPosition
            )
        }
    }


    fun updateSongs(
        newSongs: List<SongDTO>
    ) {

        stopMusic()

        songs =
            newSongs

        calculatedDurations.clear()

        currentPositions.clear()

        notifyDataSetChanged()
    }


    private fun formatDuration(
        duration: Int?
    ): String {

        if (
            duration == null ||
            duration < 0
        ) {

            return "--:--"
        }

        val minutes =
            duration / 60

        val seconds =
            duration % 60

        return String.format(
            "%d:%02d",
            minutes,
            seconds
        )
    }
}