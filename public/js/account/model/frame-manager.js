var FrameManager = FrameManager || {};

FrameManager.FrameManager = function FrameManager() {

    this.frameLoaded = false;

    this.sets = [];
    this.flag = 0;

    this.getFrameSet = async function(id) {
        if (this.sets[id]) return this.sets[id];
        let set = await Promise.resolve($.post('/animations/sets/' + id, {}));
        let data = parseJSON(set);
        this.sets[id] = JSON.parse(data.set);
        return this.sets[id];
    }

    this.setupAnimationFrame = async function(defs, frame1Index, frame2Index, frames) {
        if (this.frameLoaded) return true;
        if (frame1Index >= frames.length)
            return false;
        this.frame1Id = frames[frame1Index];
        try {
            this.frameSet1 = await this.getFrameSet(this.frame1Id >> 16);
        } catch (error) {
            console.error(error);
            return false;
        }
        this.frame1Id &= 0xFFFF;
        if (this.frameSet1 == null) return false;
        if (defs.Tweened && frame2Index != -1 && frame2Index < frames.length) {
            this.frame2Id = frames[frame2Index];
            try {
                this.frameSet2 = await this.getFrameSet(this.frame2Id >> 16);
                this.frame2Id &= 0xFFFF;
            } catch (error) {
                console.error(error);
            }
        }
        if (defs.ABool5923)
            this.flag |= 0x200;

        if (this.frameSet1.Frames[this.frame1Id].ModifiesColour)
            this.flag |= 0x80;
        if (this.frameSet1.Frames[this.frame1Id].ModifiesAlpha)
            this.flag |= 0x100;
        if (this.frameSet1.Frames[this.frame1Id].ABool988)
            this.flag |= 0x400;

        if (this.frameSet2 != null) {

            if (this.frameSet2.Frames[this.frame1Id].ModifiesColour)
                this.flag |= 0x80;
            if (this.frameSet2.Frames[this.frame1Id].ModifiesAlpha)
                this.flag |= 0x100;
            if (this.frameSet2.Frames[this.frame1Id].ABool988)
                this.flag |= 0x400;

        }

        this.flag |= 0x20;
        this.frameLoaded = true;
        return true;
    }

    this.resetFrames = function() {
        this.frameLoaded = false;
        this.flag = 0;
        this.frameSet1 = null;
        this.frameSet2 = null;
    }

}