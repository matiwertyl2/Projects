from moviepy.editor import *
import os
#clip1 = VideoFileClip("lines.mp4")
#clip2 = VideoFileClip("lines.mp4")
#final_clip = concatenate_videoclips([clip1,clip2])
#final_clip.write_videofile("my_concatenation.mp4")


def learning_movie(path_root):

    track_cnt = 4
    #path_root = '../images/sim/'

    files = os.listdir(path_root)
    print files

    track_movies = [[] for _ in range(track_cnt)]
    clips = [[] for _  in range(track_cnt)]

    for i in range(track_cnt):
        pref = "track_" + str(i+1)
        tracks_pairs = sorted( [(int(f.split("_")[-1].split(".mp4")[0]), f) for f in files if pref in f ] )
        track_movies[i] = [p[1] for p in tracks_pairs]
        print track_movies[i]
        for movie in track_movies[i]:
            clips[i].append(VideoFileClip(path_root+movie))

    merged_clips = []

    for i in range(len(clips[0])):
        max_duration = max(clips[0][i].duration, clips[1][i].duration, clips[2][i].duration, clips[3][i].duration)

        ### making all clips equal length
        for j in range(4):
            duration = clips[j][i].duration
            fr= clips[j][i].get_frame(duration-0.1)
            image_clip =  ImageClip(fr, duration=max_duration - duration).set_fps(10)
            clips[j][i] = concatenate_videoclips([clips[j][i], image_clip]).margin(2)

        merged_clips.append(clips_array([ [clips[0][i], clips[1][i]],
                                        [clips[2][i], clips[3][i]] ]))


    clip = concatenate_videoclips(merged_clips)
    clip.write_videofile(path_root + "LEARNING" + ".mp4")

def dag_brain_movie(path_root):

    files = os.listdir(path_root)

    brains = []
    clips = []
    pref = "brain"
    brain_pairs = sorted( [(int(f.split("brain")[-1].split(".png")[0]), f) for f in files if pref in f ] )
    brains = [ p[1] for p in brain_pairs]
    print brains
    for brain in brains:
        clips.append(ImageClip(path_root + brain, duration = 1.5).set_fps(10))
    clip = concatenate_videoclips(clips)
    clip.write_videofile(path_root + "BRAINS.mp4")

def benchmark_movie(path_root):
    #path_root = '../images/sim/'

    files = os.listdir(path_root)
    print files

    benchmark_movies = []
    clips = []

    pref = "benchmark_"
    benchmark_pairs = sorted( [(int(f.split("_")[-1].split(".mp4")[0]), f) for f in files if pref in f ] )
    benchmark_movies = [p[1] for p in benchmark_pairs]
    print benchmark_movies
    for movie in benchmark_movies:
        clips.append(VideoFileClip(path_root+movie))
    clip = concatenate_videoclips(clips)
    clip.write_videofile(path_root + "BENCHMARKS" + ".mp4")
