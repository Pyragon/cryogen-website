var Rasterizer = Rasterizer || {};

Rasterizer.Rasterizer = function Rasterizer(model, cube) {

    this.model = model;
    this.cube = cube;
    this.aBool7023 = false;
    this.aBool8589 = false;
    this.aBool8609 = false;

    this.vertexCount = model.VertexCount;
    this.faceCount = model.FaceCount;
    this.vertexX = JSON.parse(JSON.stringify(model.VertexX));
    this.vertexY = JSON.parse(JSON.stringify(model.VertexY));
    this.vertexZ = JSON.parse(JSON.stringify(model.VertexZ));

    this.xOffset = 0;
    this.yOffset = 0;
    this.zOffset = 0;

    this.count = 0;

    this.bones = this.model.AnimationBones;

    this.trig = new Trig.Trig();

    this.rasterize = function(frameSet1, frame1Id, frameSet2, frame2Id, i_5, i_6, i_7, bool_8) {
        if (frame1Id == -1) return;
        this.vertexX = JSON.parse(JSON.stringify(this.model.VertexX));
        this.vertexY = JSON.parse(JSON.stringify(this.model.VertexY));
        this.vertexZ = JSON.parse(JSON.stringify(this.model.VertexZ));
        if (this.ea()) {
            let frame1 = frameSet1.Frames[frame1Id];
            let frameBase = frame1.FrameBase;
            let frame2 = null;
            if (frameSet2 != null) {
                frame2 = frameSet2.Frames[frame2Id];
                if (frameBase.Id != frame2.FrameBase.Id)
                    frame2 = null;
            }
            this.beginTransformation(frameBase, frame1, frame2, i_5, i_6, i_7, null, false, bool_8, 65535, null);
            console.log(this.vertexX);
            this.ka();
            // this.roundToInts();
            this.updateModel();
            console.log(this.vertexX);
        }
    }

    this.updateModel = function() {
        let vertices = [];

        let hasAlpha = this.model.FaceAlphas != null;
        let hasFaceTypes = this.model.FaceType != null;

        for (let i = 0; i < this.model.FaceCount; i++) {

            let alpha = hasAlpha ? this.model.FaceAlphas[i] : 0;
            if (alpha == -1) continue;

            let faceType = hasFaceTypes ? this.model.FaceType[i] & 0x3 : 0;

            let faceA, faceB, faceC;
            switch (faceType) {
                case 0:
                case 1:
                    faceA = this.model.TriangleX[i];
                    faceB = this.model.TriangleY[i];
                    faceC = this.model.TriangleZ[i];
                    break;
                case 2:
                case 3:
                    faceA = this.model.TexTriX[i];
                    faceB = this.model.TexTriY[i];
                    faceC = this.model.TexTriZ[i];
                    break;
                default:
                    throw new Error('Unknown face type=' + faceType);
            }

            vertices.push({ pos: [this.vertexX[faceA], this.vertexY[faceA], this.vertexZ[faceA]], norm: [faceA, faceB, faceC] });
            vertices.push({ pos: [this.vertexX[faceB], this.vertexY[faceB], this.vertexZ[faceB]], norm: [faceA, faceB, faceC] });
            vertices.push({ pos: [this.vertexX[faceC], this.vertexY[faceC], this.vertexZ[faceC]], norm: [faceA, faceB, faceC] });
        }

        let positions = [];
        let normals = [];
        for (let vertex of vertices) {
            positions.push(...vertex.pos);
            normals.push(...vertex.norm);
        }

        this.cube.geometry.attributes.position.array = new Float32Array(positions);
        this.cube.geometry.attributes.position.needsUpdate = true;
    }

    this.roundToInts = function() {
        for (let i = 0; i < this.vertexX.length; i++)
            this.vertexX[i] = Math.floor(this.vertexX[i]);

        for (let i = 0; i < this.vertexY.length; i++)
            this.vertexY[i] = Math.floor(this.vertexY[i]);

        for (let i = 0; i < this.vertexZ.length; i++)
            this.vertexZ[i] = Math.floor(this.vertexZ[i]);
    }

    this.ka = function() {
        if (this.aBool8589 == true) {
            for (let i = 0; i < this.vertexCount; i++) {
                this.vertexX[i] = this.vertexX[i] + 7 >> 4;
                this.vertexY[i] = this.vertexY[i] + 7 >> 4;
                this.vertexZ[i] = this.vertexZ[i] + 7 >> 4;
            }
            this.aBool8589 = false;
        }

        if (this.aBool8609 == true) {
            this.method13801();
            this.aBool8609 = false;
        }

        this.aBool8621 = false;
    }

    this.method13801 = function() {
        //missing some shit
        this.method13802();
    }

    this.method13802 = function() {
        for (let i = 0; i < faceCount; i++) {

        }
    }

    this.ea = function() {
        if (this.bones == null)
            return false;
        this.xOffset = 0;
        this.yOffset = 0;
        this.zOffset = 0;
        return true;
    }

    this.beginTransformation = function(frameBase, frame1, frame2, i_4, i_5, i_6, bools_7, bool_8, bool_9, modelIndex, ints_11) {
        let i_12;
        if (frame2 != null && i_4 != 0) {
            i_12 = 0;
            let i_35 = 0;
            for (let index = 0; index < frameBase.Count; index++) {
                let bool_15 = false;
                if (i_12 < frame1.TransformationCount && index == frame1.TransformationIndices[i_12])
                    bool_15 = true;

                let bool_16 = false;
                if (i_35 < frame2.TransformationCount && index == frame2.TransformationIndices[i_35])
                    bool_16 = true;

                if (bool_15 == true || bool_16 == true) {
                    if (bools_7 != null && bools_7[index] != bool_8 && frameBase.TransformationTypes[index] != 0) {
                        if (bool_15)
                            i_12++;

                        if (bool_16)
                            i_35++;
                    } else {
                        let s_17 = 0;
                        let type = frameBase.TransformationTypes[index];
                        if (type == 3 || type == 10)
                            s_17 = 128;

                        let frame1TransformX, frame1TransformY, frame1TransformZ, frame1Skip, frame1Flag;
                        let frame2TransformX, frame2TransformY, frame2TransformZ, frame2Skip, frame2Flag;

                        if (bool_15) {
                            frame1TransformX = frame1.TransformationX[i_12];
                            frame1TransformY = frame1.TransformationY[i_12];
                            frame1TransformZ = frame1.TransformationZ[i_12];
                            frame1Skip = frame1.SkippedReferences[i_12];
                            frame1Flag = frame1.TransformationFlags[i_12];
                            i_12++;
                        } else {
                            frame1TransformX = s_17;
                            frame1TransformY = s_17;
                            frame1TransformZ = s_17;
                            frame1Skip = -1;
                            frame1Flag = 0;
                        }

                        if (bool_16) {
                            frame2TransformX = frame2.TransformationX[i_35];
                            frame2TransformY = frame2.TransformationY[i_35];
                            frame2TransformZ = frame2.TransformationZ[i_35];
                            frame2Skip = frame2.SkippedReferences[i_35];
                            frame2Flag = frame2.TransformationFlags[i_35];
                            i_35++;
                        } else {
                            frame2TransformX = s_17;
                            frame2TransformY = s_17;
                            frame2TransformZ = s_17;
                            frame2Skip = -1;
                            frame2Flag = 0;
                        }

                        let x, y, z;
                        if ((frame1Flag & 0x2) == 0 && (frame2Flag & 0x1) == 0) {
                            let i_32;
                            if (type == 2) {
                                i_32 = frame2TransformX - frame1TransformX & 0x3FFF;
                                let i_33 = frame2TransformY - frame1TransformY & 0x3FFF;
                                let i_34 = frame2TransformZ - frame1TransformZ & 0x3FFF;
                                if (i_32 >= 8192)
                                    i_32 -= 16384;

                                if (i_33 >= 8192)
                                    i_33 -= 16384;

                                if (i_34 >= 8192)
                                    i_34 -= 16384;

                                x = frame1TransformX + i_32 * i_4 / i_5 & 0x3FFF;
                                y = frame1TransformY + i_33 * i_4 / i_5 & 0x3FFF;
                                z = frame1TransformZ + i_34 * i_4 / i_5 & 0x3FFF;
                            } else if (type == 7) {
                                i_32 = frame2TransformX - frame1TransformX & 0x3F;
                                if (i_32 >= 32)
                                    i_32 -= 64;

                                x = frame1TransformX + i_32 * i_4 / i_5 & 0x3F;
                                y = frame1TransformY + (frame2TransformY - frame1TransformY) * i_4 / i_5;
                                z = frame1TransformZ + (frame2TransformZ - frame1TransformZ) * i_4 / i_5;
                            } else if (type == 9) {
                                i_32 = frame2TransformX - frame1TransformX & 0x3FFF;
                                if (i_32 >= 8192)
                                    i_32 -= 16384;

                                x = frame1TransformX + i_32 * i_4 / i_5 & 0x3FFF;
                                z = 0;
                                y = 0;
                            } else {
                                x = frame1TransformX + (frame2TransformX - frame1TransformX) * i_4 / i_5;
                                y = frame1TransformY + (frame2TransformY - frame1TransformY) * i_4 / i_5;
                                z = frame1TransformZ + (frame2TransformZ - frame1TransformZ) * i_4 / i_5;
                            }
                        } else {
                            x = frame1TransformX;
                            y = frame1TransformY;
                            z = frame1TransformZ;
                        }

                        if (frame1Skip != -1)
                            this.transform(0, frameBase.Labels[frame1Skip], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[frame1Skip], ints_11);
                        else if (frame2Skip != -1)
                            this.transform(0, frameBase.Labels[frame2Skip], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[frame2Skip], ints_11);

                        this.transform(type, frameBase.Labels[index], x, y, z, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[index], ints_11);
                    }
                }
            }
        } else {
            for (let i = 0; i < frame1.TransformationCount; i++) {
                let index = frame1.TransformationIndices[i];
                if (bools_7 == null || bools_7[index] == bool_8 || frameBase.TransformationTypes[index] == 0) {
                    let skip = frame1.SkippedReferences[i];
                    if (skip != -1)
                        this.transform(0, frameBase.Labels[skip], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[skip], ints_11);

                    this.transform(frameBase.TransformationTypes[index], frameBase.Labels[index], frame1.TransformationX[i], frame1.TransformationY[i], frame1.TransformationZ[i], i_6, bool_9, modelIndex & frameBase.AnIntArray7561[index], ints_11);
                }
            }
        }
    }

    this.transform = function(type, labels, transformX, transformY, transformZ, i_6, bool_7, i_8, ints_9) {
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
                buffer = transformedY;
                transformedX = transformedZ & 0x3FFF;
                transformedZ = -buffer & 0x3FFF;
            } else if (type == 3) {
                buffer = transformedX;
                transformedX = transformedZ;
                transformedZ = buffer;
            }
        }

        if (i_8 != 65535)
            this.transformWithExtra();
        else
            this.finishTransform(type, labels, transformedX, transformY, transformedZ, i_6, bool_7);
    }

    this.transformWithExtra = function() {
        console.error('TRANSFORMED WITH EXTRA!!!! CODY!!!!');
    }

    this.finishTransform = function(type, labels, transformedX, transformedY, transformedZ, i_6, bool_7) {
        let labelCount = labels.length;
        if (type == 0) {
            transformedX <<= 4;
            transformedY <<= 4;
            transformedZ <<= 4;
            if (this.aBool8589 == false) {
                for (let i = 0; i < this.vertexCount; i++) {
                    this.vertexX[i] <<= 4;
                    this.vertexY[i] <<= 4;
                    this.vertexZ[i] <<= 4;
                }

                this.aBool8589 = true;
            }

            let transformed = 0;
            this.xOffset = 0;
            this.yOffset = 0;
            this.zOffset = 0;

            for (let i = 0; i < labelCount; i++) {
                let label = labels[i];
                if (label < this.bones.length) {
                    let bones = this.bones[label];
                    for (let j = 0; j < bones.length; j++) {
                        let bone = bones[j];
                        this.xOffset += this.vertexX[bone];
                        this.yOffset += this.vertexY[bone];
                        this.zOffset += this.vertexZ[bone];
                        transformed++;
                    }
                }
            }

            if (transformed > 0) {
                this.xOffset = transformedX + this.xOffset / transformed;
                this.yOffset = this.yOffset / transformed + transformedY;
                this.zOffset = transformedZ + this.zOffset / transformed;
            } else {
                this.xOffset = transformedX;
                this.yOffset = transformedY;
                this.zOffset = transformedZ;
            }
        } else if (type == 1) {
            transformedX <<= 4;
            transformedY <<= 4;
            transformedZ <<= 4;
            if (this.aBool8589 == false) {
                for (let i = 0; i < this.vertexCount; i++) {
                    this.vertexX[i] <<= 4;
                    this.vertexY[i] <<= 4;
                    this.vertexZ[i] <<= 4;
                }

                this.aBool8589 = true;
            }

            for (let i = 0; i < labelCount; i++) {
                let label = labels[i];
                if (label < this.bones.length) {
                    let bones = this.bones[label];
                    for (let j = 0; j < bones.length; j++) {
                        let bone = bones[j];
                        this.vertexX[bone] += transformedX;
                        this.vertexY[bone] += transformedY;
                        this.vertexZ[bone] += transformedZ;
                    }
                }
            }
        } else if (type == 2) {
            for (let i = 0; i < labelCount; i++) {
                let label = labels[i];
                if (label < this.bones.length) {
                    let bones = this.bones[label];
                    if ((i_6 & 0x1) == 0) {
                        for (let j = 0; j < bones.length; j++) {
                            let bone = bones[j];
                            this.vertexX[bone] -= this.xOffset;
                            this.vertexY[bone] -= this.yOffset;
                            this.vertexZ[bone] -= this.zOffset;

                            if (transformedZ != 0) {
                                let sine = this.trig.getSine(transformedZ);
                                let cosine = this.trig.getCosine(transformedZ);
                                let newX = sine * this.vertexY[bone] + cosine * this.vertexX[bone] + 16383 >> 14;
                                this.vertexY[bone] = cosine * this.vertexY[bone] - sine * this.vertexX[bone] + 16383 >> 14;
                                this.vertexX[bone] = newX;
                            }

                            if (transformedX != 0) {
                                let sine = this.trig.getSine(transformedX);
                                let cosine = this.trig.getCosine(transformedX);
                                let newY = cosine * this.vertexY[bone] - sine * this.vertexZ[bone] + 16383 >> 14;
                                this.vertexZ[bone] = sine * this.vertexY[bone] + cosine * this.vertexZ[bone] + 16383 >> 14;
                                this.vertexY[bone] = newY;
                            }

                            if (transformedY != 0) {
                                let sine = this.trig.getSine(transformedY);
                                let cosine = this.trig.getCosine(transformedY);
                                let x = sine * this.vertexZ[bone] + cosine * this.vertexX[bone] + 16383 >> 14;
                                this.vertexZ[bone] = cosine * this.vertexZ[bone] - sine * this.vertexX[bone] + 16383 >> 14;
                                this.vertexX[bone] = x;
                            }

                            this.vertexX[bone] += this.xOffset;
                            this.vertexY[bone] += this.yOffset;
                            this.vertexZ[bone] += this.zOffset;

                        }
                    } else {
                        for (let j = 0; j < bones.length; j++) {
                            let bone = bones[j];
                            this.vertexX[bone] -= this.xOffset;
                            this.vertexY[bone] -= this.yOffset;
                            this.vertexZ[bone] -= this.zOffset;

                            if (transformedX != 0) {
                                let sine = this.trig.getSine(transformedX);
                                let cosine = this.trig.getCosine(transformedX);
                                this.vertexZ[bone] = sine * this.vertexY[bone] + cosine * this.vertexZ[bone] + 16383 >> 14;
                                this.vertexY[bone] = cosine * this.vertexY[bone] - sine * this.vertexZ[bone] + 16383 >> 14;
                            }

                            if (transformedZ != 0) {
                                let sine = this.trig.getSine(transformedZ);
                                let cosine = this.trig.getCosine(transformedZ);
                                this.vertexY[bone] = cosine * this.vertexY[bone] - sine * this.vertexX[bone] + 16383 >> 14;
                                this.vertexX[bone] = sine * this.vertexY[bone] + cosine * this.vertexX[bone] + 16383 >> 14;
                            }

                            if (transformedY != 0) {
                                let sine = this.trig.getSine(transformedY);
                                let cosine = this.trig.getCosine(transformedY);
                                this.vertexZ[bone] = cosine * this.vertexZ[bone] - sine * this.vertexX[bone] + 16383 >> 14;
                                this.vertexX[bone] = sine * this.vertexZ[bone] + cosine * this.vertexX[bone] + 16383 >> 14;
                            }

                            this.vertexX[bone] += this.xOffset;
                            this.vertexY[bone] += this.yOffset;
                            this.vertexZ[bone] += this.zOffset;
                        }
                    }
                }
            }
        } else {
            console.error('UNHANDLED TYPE: ' + type);
        }
    }

}