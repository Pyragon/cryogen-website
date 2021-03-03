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

FrameManager.Manager = function Manager(model, getCube) {

    var framesReady = false;
    var frame1Id, frame2Id;
    var frameSet1, frameSet2;
    var frame1Index, frame2Index;
    var xOffset, yOffset, zOffset;
    let vertexX = JSON.parse(JSON.stringify(model.VertexX));
    let vertexY = JSON.parse(JSON.stringify(model.VertexY));
    let vertexZ = JSON.parse(JSON.stringify(model.VertexZ));
    var flag = 0;

    let cube;

    let trig = new Trig.Trig();

    let sets = [];

    let count = 0;

    this.getFrameSetData = async function(id) {
        if (sets[id]) return sets[id];
        let set = await Promise.resolve($.post('/animations/sets/' + id, {}));
        let data = parseJSON(set);
        sets[id] = JSON.parse(data.set);
        return sets[id];
    }

    this.setupAnimationFrames = async function(defs, frame1Ind, frame2Ind, frames) {
        if (framesReady == true) return true;
        if (frame1Ind > frames.length)
            return false;
        this.defs = defs;
        frame1Index = frame1Ind;
        frame2Index = frame2Ind;

        frame1Id = frames[frame1Index];
        try {
            frameSet1 = await this.getFrameSetData(frame1Id >> 16);
        } catch (exception) {
            return;
        }

        frame1Id &= 0xFFFF;
        if (frameSet1 == null)
            return false;
        if (defs.Tweened === true && frame2Index != -1 && frame2Index < frames.length) {
            frame2Id = frames[frame2Index];
            try {
                frameSet2 = await this.getFrameSetData(frame2Id >> 16);
            } catch (exception) {
                return false;
            }
            frame2Id &= 0xFFFF;
        }
        if (defs.ABool5923 === true)
            flag |= 0x200;

        if (frameSet1.Frames[frame1Id].ModifiesColour)
            flag |= 0x80;

        if (frameSet1.Frames[frame1Id].ModifiesAlpha)
            flag |= 0x100;

        if (frameSet1.Frames[frame1Id].ABool988)
            flag |= 0x400;

        if (frameSet2 != null) {
            if (!frameSet2.Frames[frame2Id])
                console.log('Frame null: ' + frameSet2.Frames.length, frame2Id);
            else {
                if (frameSet2.Frames[frame2Id].ModifiesColour)
                    flag |= 0x80;
                if (frameSet2.Frames[frame2Id].ModifiesAlpha)
                    flag |= 0x100;
                if (frameSet2.Frames[frame2Id].ABool988)
                    flag |= 0x400;
            }
        }

        flag |= 0x20;
        framesReady = true;
        return true;
    }

    this.playFrame = function(i_5, duration, i_7, bool_8) {
        if (frame1Id == -1) return;
        let frame1 = frameSet1.Frames[frame1Id];
        let frameBase = frame1.FrameBase;
        let frame2 = null;
        if (frameSet2 != null) {
            frame2 = frameSet2.Frames[frame2Id];
            if (frame2.FrameBase.Id != frameBase.Id)
                frame2 = null;
        }
        vertexX = JSON.parse(JSON.stringify(model.VertexX));
        vertexY = JSON.parse(JSON.stringify(model.VertexY));
        vertexZ = JSON.parse(JSON.stringify(model.VertexZ));
        this.setupTransformations(frameBase, frame1, frame2, i_5, duration, i_7, null, false, bool_8, 65535, null);

    }

    this.setupTransformations = function(frameBase, frame1, frame2, i_4, duration, i_6, bools_7, bool_8, bool_9, modelIndex, ints_11) {
        if (frame2 != null && i_4 != 0) {
            let i_12 = 0;
            let i_35 = 0;
            for (let frameIndex = 0; frameIndex < frameBase.Count; frameIndex++) {
                let bool_15 = false;
                if (i_12 < frame1.TransformationCount && frameIndex == frame1.TransformationIndices[i_12])
                    bool_15 = true;

                let bool_16 = false;
                if (i_35 < frame2.TransformationCount && frameIndex == frame2.TransformationIndices[i_35])
                    bool_16 = true;

                if (bool_15 || bool_16) {
                    if (bools_7 != null && bools_7[frameIndex] != bool_8 && frameBase.TransformationTypes[frameIndex] != 0) {
                        if (bool_15)
                            i_12++;

                        if (bool_16)
                            i_35++;
                    } else {
                        let s_17 = 0;
                        let type = frameBase.TransformationTypes[frameIndex];
                        if (type == 3 || type == 10)
                            s_17 = 128;

                        let frame1X, frame1Y, frame1Z, frame1Skip, frame1Flag;
                        if (bool_15) {
                            frame1X = frame1.TransformationX[i_12];
                            frame1Y = frame1.TransformationY[i_12];
                            frame1Z = frame1.TransformationZ[i_12];
                            frame1Skip = frame1.SkippedReferences[i_12];
                            frame1Flag = frame1.TransformationFlags[i_12];
                            i_12++;
                        } else {
                            frame1X = s_17;
                            frame1Y = s_17;
                            frame1Z = s_17;
                            frame1Skip = -1;
                            frame1Flag = 0;
                        }

                        let frame2X, frame2Y, frame2Z, frame2Skip, frame2Flag;
                        if (bool_16) {
                            frame2X = frame2.TransformationX[i_35];
                            frame2Y = frame2.TransformationY[i_35];
                            frame2Z = frame2.TransformationZ[i_35];
                            frame2Skip = frame2.SkippedReferences[i_35];
                            frame2Flag = frame2.TransformationFlags[i_35];
                            i_35++;
                        } else {
                            frame2X = s_17;
                            frame2Y = s_17;
                            frame2Z = s_17;
                            frame2Skip = -1;
                            frame2Flag = 0;
                        }

                        let i_29;
                        let i_30;
                        let i_31;
                        if ((frame1Flag & 0x2) == 0 && (frame2Flag & 0x1) == 0) {
                            let i_32;
                            if (type == 2) {
                                i_32 = frame2X - frame1X & 0x3FFF;
                                let i_33 = frame2Y - frame1Y & 0x3FFF;
                                let i_34 = frame2Z - frame1Z & 0x3FFF;
                                if (i_32 >= 8192)
                                    i_32 -= 16384;
                                if (i_33 >= 8192)
                                    i_33 -= 16384;
                                if (i_34 >= 8192)
                                    i_34 -= 16384;

                                i_29 = frame1X + i_32 * i_4 / duration & 0x3FFF;
                                i_30 = frame1Y + i_33 * i_4 / duration & 0x3FFF;
                                i_31 = frame1Z + i_34 * i_4 / duration & 0x3FFF;
                            } else if (type == 7) {
                                i_32 = frame2X - frame1X & 0x3f;
                                if (i_32 >= 32)
                                    i_32 -= 64;

                                i_29 = frame1X + i_32 * i_4 / duration & 0x3f;
                                i_30 = frame1Y + (frame2Y - frame1Y) * i_4 / duration;
                                i_31 = frame1Z + (frame2Z - frame1Z) * i_4 / duration;
                            } else if (type == 9) {
                                i_32 = frame2X - frame1X & 0x3FFF;
                                if (i_32 >= 8192)
                                    i_32 -= 16384;

                                i_29 = frame1X + i_32 * i_4 / duration & 0x3FFF;
                                i_31 = 0;
                                i_30 = 0;
                            } else {
                                i_29 = frame1X + (frame2X - frame1X) * i_4 / duration;
                                i_30 = frame1Y + (frame2Y - frame1Y) * i_4 / duration;
                                i_31 = frame1Z + (frame2Z - frame1Z) * i_4 / duration;
                            }
                        } else {
                            i_29 = frame1X;
                            i_30 = frame1Y;
                            i_31 = frame1Z;
                        }
                        if (frame1Skip != -1)
                            this.setupTransformation(0, frameBase.Labels[frame1Skip], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[frame1Skip], ints_11);
                        else if (frame2Skip != -1)
                            this.setupTransformation(0, frameBase.Labels[frame2Skip], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[frame2Skip], ints_11);
                        this.setupTransformation(type, frameBase.Labels[frameIndex], i_29, i_30, i_31, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[frameIndex], ints_11);
                    }
                }
            }
        } else {
            for (let i = 0; i < frame1.TransformationCount; i++) {
                let index = frame1.TransformationIndices[i];
                if (bools_7 == null || bools_7[index] == bool_8 || frameBase.TransformationTypes[index] == 0) {
                    let skipped = frame1.SkippedReferences[i];
                    if (skipped != -1)
                        this.setupTransformation(0, frameBase.Labels[skipped], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[skipped], ints_11);
                    this.setupTransformation(frameBase.TransformationTypes[index], frameBase.Labels[index], frame1.TransformationX[i], frame1.TransformationY[i], frame1.TransformationZ[i], i_6, bool_9, modelIndex & frameBase.AnIntArray7561[index], ints_11);
                }
            }
        }
        this.updateModel();
    }

    this.setupTransformation = function(type, labels, transformX, transformY, transformZ, i_6, bool_7, i_8, ints_9) {
        let transformedX = transformX;
        let transformedZ = transformZ;
        let buffer;
        if (i_6 == 1) {
            if (type == 0 || type == 1) {
                buffer = -transformedX;
                transformedX = transformedZ;
                transformedZ = buffer;
            } else if (type == 2) {
                buffer = transformedX;
                transformedX = -transformedZ & 0x3FFF;
                transformedZ = buffer & 0x3FFF;
            } else if (type == 3) {
                buffer = transformedX;
                transformedX = transformedZ;
                transformedZ = buffer;
            }
        } else if (i_6 == 2) {
            if (type == 0 || type == 1) {
                transformedX = -transformedX;
                transformedZ = -transformedZ;
            } else if (type == 2) {
                transformedX = -transformedX & 0x3FFF;
                transformedZ = -transformedZ & 0x3FFF;
            }
        } else if (i_6 == 3) {
            if (type == 0 || type == 1) {
                buffer = transformedX;
                transformedX = -transformedZ;
                transformedZ = buffer;
            } else if (type == 2) {
                buffer = transformedX;
                transformedX = transformedZ & 0x3FFF;
                transformedZ = -buffer & 0x3FFF;
            } else if (type == 3) {
                buffer = transformedX;
                transformedX = transformedZ;
                transformedZ = buffer;
            }
        }

        if (i_8 != 65535) {

        } else {
            this.startRenderingAnimation(type, labels, transformedX, transformY, transformedZ, i_6, bool_7);
        }
    }

    this.startRenderingAnimation = function(type, labels, transformX, transformY, transformZ, i_6, bool_7) {
        if (!cube)
            cube = getCube();
        let labelsCount = labels.length;
        if (type == 0) {
            // transformX <<= 4;
            // transformY <<= 4;
            // transformZ <<= 4;
            if (!this.aBool8589) {
                for (let i = 0; i < model.VertexCount; i++) {
                    // vertexX[i] <<= 4;
                    // vertexY[i] <<= 4;
                    // vertexZ[i] <<= 4;
                }
                this.aBool8589 = true;
            }

            xOffset = 0;
            yOffset = 0;
            zOffset = 0;
            let transformed = 0;

            for (let i = 0; i < labelsCount; i++) {
                let label = labels[i];
                if (label < model.AnimationBones.length) {
                    let bones = model.AnimationBones[label];
                    for (let j = 0; j < bones.length; j++) {
                        let bone = bones[j];
                        xOffset += vertexX[bone];
                        yOffset += vertexY[bone];
                        zOffset += vertexZ[bone];
                        transformed++;
                    }
                }
            }

            if (transformed > 0) {
                xOffset = xOffset / transformed + transformX;
                yOffset = yOffset / transformed + transformY;
                zOffset = zOffset / transformed + transformZ;
            } else {
                xOffset = transformX;
                yOffset = transformY;
                zOffset = transformZ;
            }
        } else if (type == 1) {
            // transformX <<= 4;
            // transformY <<= 4;
            // transformZ <<= 4;
            if (!this.aBool8589) {
                for (let i = 0; i < model.VertexCount; i++) {
                    // vertexX[i] <<= 4;
                    // vertexY[i] <<= 4;
                    // vertexZ[i] <<= 4;
                }
                this.aBool8589 = true;
            }

            for (let i = 0; i < labelsCount; i++) {
                let label = labels[i];
                if (label < model.AnimationBones.length) {
                    let bones = model.AnimationBones[label];
                    for (let j = 0; j < bones.length; j++) {
                        let bone = bones[j];
                        vertexX[bone] += transformX;
                        vertexY[bone] += transformY;
                        vertexZ[bone] += transformZ;
                    }
                }
            }
        } else if (type == 2) {
            for (let i = 0; i < labelsCount; i++) {
                let label = labels[i];
                if (label < model.AnimationBones.length) {
                    let bones = model.AnimationBones[label];
                    if ((i_6 & 0x1) == 0) {
                        for (let j = 0; j < bones.length; j++) {
                            let bone = bones[j];
                            vertexX[bone] -= xOffset;
                            vertexY[bone] -= yOffset;
                            vertexZ[bone] -= zOffset;
                            if (transformZ != 0) {
                                let sine = trig.getSine(transformZ);
                                let cosine = trig.getCosine(transformZ);
                                vertexY[bone] = cosine * vertexY[bone] - sine * vertexX[bone] + 16383 >> 14;
                                vertexX[bone] = sine * vertexY[bone] + cosine * vertexX[bone] + 16383 >> 14;
                            }
                            if (transformX != 0) {
                                let sine = trig.getSine(transformX);
                                let cosine = trig.getCosine(transformX);
                                let z = sine * vertexY[bone] + cosine * vertexZ[bone] + 16383 >> 14;
                                let y = cosine * vertexY[bone] - sine * vertexZ[bone] + 16383 >> 14;
                                vertexZ[bone] = z;
                                vertexY[bone] = y;
                            }
                            if (transformY != 0) {
                                let sine = trig.getSine(transformY);
                                let cosine = trig.getCosine(transformY);
                                let z = cosine * vertexZ[bone] - sine * vertexX[bone] + 16383 >> 14;
                                let x = sine * vertexZ[bone] + cosine * vertexX[bone] + 16383 >> 14;
                                vertexZ[bone] = z;
                                vertexX[bone] = x;
                            }

                            vertexX[bone] += xOffset;
                            vertexY[bone] += yOffset;
                            vertexZ[bone] += zOffset;
                        }
                    } else {
                        for (let j = 0; j < bones.length; j++) {
                            let bone = bones[j];
                            vertexX[bone] -= xOffset;
                            vertexY[bone] -= yOffset;
                            vertexZ[bone] -= zOffset;
                            if (transformZ != 0) {
                                let sine = trig.getSine(transformZ);
                                let cosine = trig.getCosine(transformZ);
                                vertexY[bone] = cosine * vertexY[bone] - sine * vertexX[bone] + 16383 >> 14;
                                vertexX[bone] = sine * vertexY[bone] + cosine * vertexX[bone] + 16383 >> 14;
                            }
                            if (transformX != 0) {
                                let sine = trig.getSine(transformX);
                                let cosine = trig.getCosine(transformX);
                                vertexZ[bone] = sine * vertexY[bone] + cosine * vertexZ[bone] + 16383 >> 14;
                                vertexY[bone] = cosine * vertexY[bone] - sine * vertexZ[bone] + 16383 >> 14;
                            }
                            if (transformY != 0) {
                                let sine = trig.getSine(transformY);
                                let cosine = trig.getCosine(transformY);
                                vertexZ[bone] = cosine * vertexZ[bone] - sine * vertexX[bone] + 16383 >> 14;
                                vertexX[bone] = sine * vertexZ[bone] + cosine * vertexX[bone] + 16383 >> 14;
                            }

                            vertexX[bone] += xOffset;
                            vertexY[bone] += yOffset;
                            vertexZ[bone] += zOffset;
                        }
                    }
                }
            }
        } else if (type == 3) {
            for (let i = 0; i < labelsCount; i++) {
                let label = labels[i];
                if (label < model.AnimationBones.length) {
                    let bones = model.AnimationBones[label];
                    for (let j = 0; j < bones.length; j++) {
                        let bone = bones[j];
                        vertexX[bone] -= xOffset;
                        vertexY[bone] -= yOffset;
                        vertexZ[bone] -= zOffset;
                        vertexX[bone] = transformX * vertexX[bone] / 128;
                        vertexY[bone] = transformY * vertexY[bone] / 128;
                        vertexZ[bone] = transformZ * vertexZ[bone] / 128;
                        vertexX[bone] += xOffset;
                        vertexY[bone] += yOffset;
                        vertexZ[bone] += zOffset;
                    }
                }
            }
        } else if (type == 5) {

        }
        this.roundToInts();
    }

    this.roundToInts = function() {
        for (let i = 0; i < vertexX.length; i++)
            vertexX[i] = Math.floor(vertexX[i]);

        for (let i = 0; i < vertexY.length; i++)
            vertexY[i] = Math.floor(vertexY[i]);

        for (let i = 0; i < vertexZ.length; i++)
            vertexZ[i] = Math.floor(vertexZ[i]);
    }

    this.resetModel = function() {
        vertexX = JSON.parse(JSON.stringify(model.VertexX));
        vertexY = JSON.parse(JSON.stringify(model.VertexY));
        vertexZ = JSON.parse(JSON.stringify(model.VertexZ));
    }

    this.updateModel = function() {
        let vertices = [];

        let hasAlpha = model.FaceAlphas != null;
        let hasFaceTypes = model.FaceType != null;

        for (let i = 0; i < model.FaceCount; i++) {

            let alpha = hasAlpha ? model.FaceAlphas[i] : 0;
            if (alpha == -1) continue;

            let faceType = hasFaceTypes ? model.FaceType[i] & 0x3 : 0;

            let faceA, faceB, faceC;
            switch (faceType) {
                case 0:
                case 1:
                    faceA = model.TriangleX[i];
                    faceB = model.TriangleY[i];
                    faceC = model.TriangleZ[i];
                    break;
                case 2:
                case 3:
                    faceA = model.TexTriX[i];
                    faceB = model.TexTriY[i];
                    faceC = model.TexTriZ[i];
                    break;
                default:
                    throw new Error('Unknown face type=' + faceType);
            }

            vertices.push({ pos: [vertexX[faceA], vertexY[faceA], vertexZ[faceA]], norm: [faceA, faceB, faceC] });
            vertices.push({ pos: [vertexX[faceB], vertexY[faceB], vertexZ[faceB]], norm: [faceA, faceB, faceC] });
            vertices.push({ pos: [vertexX[faceC], vertexY[faceC], vertexZ[faceC]], norm: [faceA, faceB, faceC] });
        }

        let positions = [];
        let normals = [];
        for (let vertex of vertices) {
            positions.push(...vertex.pos);
            normals.push(...vertex.norm);
        }

        // console.log(positions);

        cube.geometry.attributes.position.array = new Float32Array(positions);
        cube.geometry.attributes.position.needsUpdate = true;
    }

    this.resetFrames = function() {
        framesReady = false;
        flag = 0;
        frameSet1 = null;
        frameSet2 = null;
    }

}