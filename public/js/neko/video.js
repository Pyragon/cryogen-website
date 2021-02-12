function createVideoObject(sendMessage, onStateChange) {
    return {

        state: {
            index: -1,
            tracks: [],
            streams: [],
            volume: 50,
            horizontal: 16,
            vertical: 9,
            width: 1280,
            height: 720,
            rate: 30,
            playing: false,
            playable: true
        },

        play: function() {
            this.state.playing = true;
        },

        addTrack: function([track, stream]) {
            this.state.tracks = this.state.tracks.concat([track]);
            this.state.streams = this.state.streams.concat([stream]);
        },

        setStream: function(id) {
            this.state.index = id;
            onStateChange();
        },

        getStream: function() {
            return this.state.streams[this.state.index];
        },

        getVolume: function() {
            return this.state.volume;
        },

        setVolume: function(volume) {
            this.state.volume = volume;
        },

        getResolution: function() {
            return { w: this.state.width, h: this.state.height };
        },

        reset: function() {
            this.state.index = -1;
            this.state.tracks = [];
            this.state.streams = [];
            this.state.volume = 50;
        }

    };
}