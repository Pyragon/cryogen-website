var Animation = Animation || {};

Animation.Animation = function Animation() {

    this.frameManager = new FrameManager.FrameManager();

    this.setupAnimation = function(id, defs, speed = 0, i_3 = 0, bool_4 = false, bool_5 = true) {
        if (id == this.getCurrentAnimation()) return;
        if (id == -1) {
            this.id = -1;
            this.defs = null;
            this.resetFrames();
            return;
        }
        if (this.defs != null && this.defs.Id == id) {
            if (this.defs.ReplayMode == 0) return;
        } else
            this.defs = defs;
        this.anInt5459 = 0;
        this.speed = speed;
        this.anInt5461 = i_3;
        this.aBool5456 = bool_5;
        if (bool_4) {
            this.frame1Index = (Math.random() * this.defs.FrameHashes.length);
            this.frame1Duration = (Math.random() * this.defs.FrameDurations[this.frame1Index]);
        } else {
            this.frame1Index = 0;
            this.frame1Duration = 0;
        }
        this.frame2Index = this.frame1Index + 1;
        if (this.frame2Index >= this.defs.FrameHashes.length)
            this.frame2Index = -1;
        if (this.speed == 0) {
            //play animation sound
        }
        this.aBool5462 = false;
        this.resetFrames();
    }

    this.increaseIndex = function() {
        this.frame1Index++;
        this.frame2Index++;
        if (this.frame2Index >= this.defs.FrameHashes.length)
            this.frame2Index = 0;
    }

    this.getABool5462 = function() {
        return this.aBool5462 == true;
    }

    this.setupLoop = function(loopStart) {
        if (this.defs == null || loopStart == 0) return false;
        if (this.speed > 0) {
            if (this.speed >= loopStart) {
                this.speed -= loopStart;
                return false;
            }
            loopStart -= this.speed;
            this.speed = 0;
            //play animation sound
        }
        loopStart += this.frame1Duration;
        let bool_3 = this.defs.Tweened;
        if (loopStart > 100 && this.defs.LoopDelay > 0) {
            let i_4;
            for (let i_4 = this.defs.FrameHashes.length - this.defs.LoopDelay; this.frame1Index < i_4 && loopStart > this.defs.FrameDurations[this.frame1Index]; this.frame1Index++)
                loopStart -= this.defs.FrameDurations[this.frame1Index];
            if (this.frame1Index >= i_4) {
                let totalDuration = 0;
                for (let i = i_4; i < this.defs.FrameHashes.length; i++)
                    totalDuration += this.defs.FrameDurations[i];
                if (this.anInt5461 == 0)
                    this.anInt5459 += loopStart / totalDuration;
                loopStart %= totalDuration;
            }
            this.frame2Index = this.frame1Index + 1;
            if (this.frame2Index >= this.defs.FrameHashes.length) {
                if (this.defs.LoopDelay == -1 && this.aBool5456 == true)
                    this.frame2Index = 0;
                else
                    this.frame2Index -= this.defs.LoopDelay;
                if (this.frame2Index < 0 || this.frame2Index >= this.defs.FrameHashes.length)
                    this.frame2Index = -1;
            }
            bool_3 = true;
        }
        while (loopStart > this.defs.FrameDurations[this.frame1Index]) {
            bool_3 = true;
            loopStart -= this.defs.FrameDurations[this.frame1Index++];
            if (this.frame1Index >= this.defs.FrameHashes.length) {
                if (this.defs.LoopDelay != -1 && this.anInt5461 != 2) {
                    this.frame1Index -= this.defs.LoopDelay;
                    if (this.anInt5461 == 0)
                        this.anInt5459++;
                }
                if (this.anInt5459 >= this.defs.MaxLoops || this.frame1Index < 0 || this.frame1Index >= this.defs.FrameHashes.length) {
                    this.aBool5462 = true;
                    break;
                }
            }
            //play sound
            this.frame2Index = this.frame1Index + 1;
            if (this.frame2Index >= this.defs.FrameHashes.length) {
                if (this.defs.LoopDelay == -1 && this.aBool5456 == true)
                    this.frame2Index = 0;
                else
                    this.frame2Index -= this.defs.LoopDelay;
                if (this.frame2Index < 0 || this.frame2Index >= this.defs.FrameHashes.length)
                    this.frame2Index = -1;
            }
        }
        this.frame1Duration = loopStart;
        if (bool_3)
            this.resetFrames();
        return bool_3;
    }

    this.resetFrames = function() {
        this.frameManager.resetFrames();
    }

    this.resetAnimation = function() {
        this.frame1Index = 0;
        this.frame2Index = this.defs.FrameHashes.length > 1 ? 1 : -1;
        this.frame1Duration = 0;
        this.aBool5462 = false;
        this.speed = 0;
        this.anInt5459 = 0;
        this.resetFrames();
    }

    this.rasterize = async function(rasterizer, i_2) {
        if (this.defs == null || this.defs.FrameHashes == null) return;
        let setup = await this.setupAnimationFrames();
        if (setup == true)
            rasterizer.rasterize(this.frameManager.frameSet1, this.frameManager.frame1Id, this.frameManager.frameSet2, this.frameManager.frame2Id, this.frame1Duration, this.defs.FrameDurations[this.frame1Index], i_2, this.defs.ABool5923);
    }

    this.setupAnimationFrames = async function() {
        if (this.defs == null) return false;
        let setup = await this.frameManager.setupAnimationFrame(this.defs, this.frame1Index, this.frame2Index, this.defs.FrameHashes);
        return setup;
    }

    this.getCurrentAnimation = function() {
        return this.defs != null ? this.defs.Id : -1;
    }

}