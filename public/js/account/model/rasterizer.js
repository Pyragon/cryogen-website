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

    this.textureDefinitions = [];

    this.getTextureDefinitions = async function(id) {
        if (this.textureDefinitions[id]) return this.textureDefinitions[id];
        let set = await Promise.resolve($.post('/textures/defs/' + id, { visitorId }));
        let data = parseJSON(set);
        if (data == null)
            return null;
        this.textureDefinitions[id] = JSON.parse(data.defs);
        return this.textureDefinitions[id];
    }

    this.init = async function(height) {

        this.height = height;
        this.resize(0, -height << 2, 0);

        let faceIndices = Array(this.model.FaceCount);
        for (let index = 0; index < this.model.FaceCount; faceIndices[index] = index++) {

        }
        let longs_53 = Array(this.model.FaceCount);
        let i_11, i_14, b_17, i_64, textureId;
        for (let i = 0; i < this.model.FaceCount; i++) {
            let details;
            i_14 = 0;
            let b_16 = 0;
            b_17 = 0;
            if (this.model.IsolatedVertexNormals != null) {
                // let bool_18 = false;
                // for (let j = 0; j < this.model.IsolatedVertexNormals.length; j++) {

                // }
            }

            textureId = -1;
            if (this.model.FaceTextures != null) {
                textureId = this.model.FaceTextures[i];
                if (textureId != -1) {
                    details = await this.getTextureDefinitions(textureId);
                    b_16 = details.EffectId;
                    b_17 = details.EffectParam1;
                }
            }

            let bool_71 = this.model.FaceAlphas != null && this.model.FaceAlphas[i] != 0 || details != null && details.blendType == 2;
            if (bool_71 == true && this.model.FacePriorities != null)
                i_14 += this.model.FacePriorities[i] << 17;

            if (bool_71 == true)
                i_14 += 65536;

            i_14 += (b_16 & 0xff) << 8;
            i_14 += b_17 & 0xff;
            i_64 = ((textureId & 0xffff) << 16);
            i_64 += i_11 & 0xffff;
            longs_53[i_11] = (i_14 << 32) + i_64;
            this.aBool8630 |= bool_71;
        }

        // this.method8316(longs_53, faceIndices, 0, longs_53.length - 1);

        this.faceAlphas = Array(this.model.FaceCount);
        if (this.model.FaceAlphas == null) {
            for (let i = 0; i < this.model.FaceCount; i++)
                this.faceAlphas[i] = 0;
        }

        if (this.model.IsolatedVertexNormals != null) {
            console.error('ADD THIS SHIT CODY');
        }


        this.aFloatArrayArray8635 = Array(this.model.FaceCount);
        this.aFloatArrayArray8591 = Array(this.model.FaceCount);
        let aFloatArray2338 = Array(2);
        let bool_66 = false;

        let i_67;
        for (let i = 0; i < this.model.FaceCount; i++) {
            if (this.model.FaceAlphas != null)
                this.faceAlphas[i] = this.model.FaceAlphas[i];
            if (this.model.TexturePos == null)
                b_17 = -1;
            else
                b_17 = this.model.TexturePos[i];

            textureId = this.model.FaceTextures == null ? -1 : this.model.FaceTextures[i];
            if (textureId != -1) {
                bool_66 = true;
                this.aFloatArrayArray8635[i] = Array(3);
                this.aFloatArrayArray8591[i] = Array(3);
                if (b_17 == -1) {
                    this.aFloatArrayArray8635[i][0] = 0.0;
                    this.aFloatArrayArray8591[i][0] = 1.0;
                    this.aFloatArrayArray8635[i][1] = 1.0;
                    this.aFloatArrayArray8591[i][1] = 1.0;
                    this.aFloatArrayArray8635[i][2] = 0.0;
                    this.aFloatArrayArray8591[i][2] = 0.0;
                } else {
                    let i_69 = b_17 & 0xFF;
                    let b_58 = this.model.TextureRenderTypes[i_69];
                    let s_23;
                    let s_24;
                    let f_30;
                    let f_31;
                    let f_32;
                    let f_42;
                    let f_43;
                    let f_44;
                    let f_45;
                    let f_46;
                    let f_47;
                    let s_59;
                    if (b_58 == 0) {
                        s_59 = this.model.TriangleX[i_67];
                        s_23 = this.model.TriangleY[i_67];
                        s_24 = this.model.TriangleZ[i_67];
                        let s_25 = this.model.TexTriX[i_69];
                        let s_26 = this.model.TexTriY[i_69];
                        let s_27 = this.model.TexTriZ[i_69];
                        let f_28 = this.vertexX[s_25];
                        let f_29 = this.vertexY[s_25];
                        f_30 = this.vertexZ[s_25];
                        f_31 = this.vertexX[s_26] - f_28;
                        f_32 = this.vertexY[s_26] - f_29;
                        let f_33 = this.vertexZ[s_26] - f_30;
                        let f_34 = this.vertexX[s_27] - f_28;
                        let f_35 = this.vertexY[s_27] - f_29;
                        let f_36 = this.vertexZ[s_27] - f_30;
                        let f_37 = this.vertexX[s_59] - f_28;
                        let f_38 = this.vertexY[s_59] - f_29;
                        let f_39 = this.vertexZ[s_59] - f_30;
                        let f_40 = this.vertexX[s_23] - f_28;
                        let f_41 = this.vertexY[s_23] - f_29;
                        f_42 = this.vertexZ[s_23] - f_30;
                        f_43 = this.vertexX[s_24] - f_28;
                        f_44 = this.vertexY[s_24] - f_29;
                        f_45 = this.vertexZ[s_24] - f_30;
                        f_46 = f_32 * f_36 - f_33 * f_35;
                        f_47 = f_33 * f_34 - f_31 * f_36;
                        let f_48 = f_31 * f_35 - f_32 * f_34;
                        let f_49 = f_35 * f_48 - f_36 * f_47;
                        let f_50 = f_36 * f_46 - f_34 * f_48;
                        let f_51 = f_34 * f_47 - f_35 * f_46;
                        let f_52 = 1.0 / (f_49 * f_31 + f_50 * f_32 + f_51 * f_33);
                        this.aFloatArrayArray8635[i][0] = (f_49 * f_37 + f_50 * f_38 + f_51 * f_39) * f_52;
                        this.aFloatArrayArray8635[i][1] = (f_49 * f_40 + f_50 * f_41 + f_51 * f_42) * f_52;
                        this.aFloatArrayArray8635[i][2] = (f_49 * f_43 + f_50 * f_44 + f_51 * f_45) * f_52;
                        f_49 = f_32 * f_48 - f_33 * f_47;
                        f_50 = f_33 * f_46 - f_31 * f_48;
                        f_51 = f_31 * f_47 - f_32 * f_46;
                        f_52 = 1.0 / (f_49 * f_34 + f_50 * f_35 + f_51 * f_36);
                        this.aFloatArrayArray8591[i][0] = (f_49 * f_37 + f_50 * f_38 + f_51 * f_39) * f_52;
                        this.aFloatArrayArray8591[i][1] = (f_49 * f_40 + f_50 * f_41 + f_51 * f_42) * f_52;
                        this.aFloatArrayArray8591[i][2] = (f_49 * f_43 + f_50 * f_44 + f_51 * f_45) * f_52;
                    } else {

                    }
                }
            }

        }

        if (this.model.TextureSkins != null) {
            let i_79 = 0;
            let ints_68 = Array(256);

            for (let i = 0; i < this.model.FaceCount; i++) {
                let skin = this.model.TextureSkins[i];
                if (skin >= 0) {
                    if (typeof ints_68[skin] === 'undefined')
                        ints_68[skin] = 0;
                    else
                        ints_68[skin]++;
                    if (skin > i_79)
                        i_79 = skin;
                }
            }

            this.anIntArrayArray8924 = Array(i_79 + 1);
            for (let i = 0; i <= i_79; i++) {
                this.anIntArrayArray8924[i] = Array(ints_68[i]);
                ints_68[i] = 0;
            }

            for (let i = 0; i < this.model.FaceCount; i++) {
                let skin = this.model.TextureSkins[i];
                if (skin >= 0)
                    this.anIntArrayArray8924[skin][ints_68[skin]++] = i;
            }
        }
    }

    this.rasterize = function(id, frameSet1, frame1Id, frameSet2, frame2Id, i_5, i_6, i_7, bool_8) {
        if (frame1Id == -1) return;
        this.resetModel();
        if (this.ea()) {
            let frame1 = frameSet1.Frames[frame1Id];
            let frameBase = frame1.FrameBase;
            let frame2 = null;
            if (frameSet2 != null) {
                frame2 = frameSet2.Frames[frame2Id];
                if (frameBase.Id != frame2.FrameBase.Id)
                    frame2 = null;
            }
            this.beginTransformation(id, frameBase, frame1, frame2, i_5, i_6, i_7, null, false, bool_8, 65535, null);
            this.ka();
            this.render();
        } else console.log('ea was false');
    }

    this.resetModel = function() {
        this.vertexX = JSON.parse(JSON.stringify(this.model.VertexX));
        this.vertexY = JSON.parse(JSON.stringify(this.model.VertexY));
        this.vertexZ = JSON.parse(JSON.stringify(this.model.VertexZ));
        this.resize(0, -this.height << 2, 0);
        if (this.model.FaceAlphas == null) {
            for (let i = 0; i < this.model.FaceCount; i++) {
                if (typeof this.faceAlphas[i] == 'undefined') {
                    console.log(this.faceAlphas, i);
                    continue;
                }
                this.faceAlphas[i] = 0;
            }
        } else
            this.faceAlphas = this.model.FaceAlphas;
    }

    this.render = function() {
        let vertices = [];

        let hasFaceTypes = this.model.FaceType != null;

        for (let i = 0; i < this.model.FaceCount; i++) {

            let alpha = this.faceAlphas[i];
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

            let colour = model.RealFaceColour[i];
            let r = (colour >> 16) & 0xFF;
            let g = (colour >> 8) & 0xFF;
            let b = colour & 0xFF;
            vertices.push({ pos: [this.vertexX[faceA], this.vertexY[faceA], this.vertexZ[faceA]], colors: [r, g, b, alpha] });
            vertices.push({ pos: [this.vertexX[faceB], this.vertexY[faceB], this.vertexZ[faceB]], colors: [r, g, b, alpha] });
            vertices.push({ pos: [this.vertexX[faceC], this.vertexY[faceC], this.vertexZ[faceC]], colors: [r, g, b, alpha] });
        }

        let positions = [];
        let colors = [];
        for (let vertex of vertices) {
            positions.push(...vertex.pos);
            colors.push(...vertex.colors);
        }

        // this.cube.geometry.attributes.position.array = new Float32Array(positions);
        // this.cube.geometry.attributes.position.needsUpdate = true;
        this.cube.geometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(positions), 3));
        this.cube.geometry.setAttribute('color', new THREE.BufferAttribute(new Uint8Array(colors), 4, true));
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

    this.resize = function(x, y, z) {
        for (let i = 0; i < this.model.MaxDepth; i++) {
            if (x != 0)
                this.vertexX[i] += x;

            if (y != 0)
                this.vertexY[i] += y;

            if (z != 0)
                this.vertexZ[i] += z;
        }
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

    this.beginTransformation = function(id, frameBase, frame1, frame2, i_4, i_5, i_6, bools_7, bool_8, bool_9, modelIndex, ints_11) {
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
                            this.transform(id, 0, frameBase.Labels[frame1Skip], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[frame1Skip], ints_11);
                        else if (frame2Skip != -1)
                            this.transform(id, 0, frameBase.Labels[frame2Skip], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[frame2Skip], ints_11);

                        this.transform(id, type, frameBase.Labels[index], x, y, z, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[index], ints_11);
                    }
                }
            }
        } else {
            for (let i = 0; i < frame1.TransformationCount; i++) {
                let index = frame1.TransformationIndices[i];
                if (bools_7 == null || bools_7[index] == bool_8 || frameBase.TransformationTypes[index] == 0) {
                    let skip = frame1.SkippedReferences[i];
                    if (skip != -1)
                        this.transform(id, 0, frameBase.Labels[skip], 0, 0, 0, i_6, bool_9, modelIndex & frameBase.AnIntArray7561[skip], ints_11);

                    this.transform(id, frameBase.TransformationTypes[index], frameBase.Labels[index], frame1.TransformationX[i], frame1.TransformationY[i], frame1.TransformationZ[i], i_6, bool_9, modelIndex & frameBase.AnIntArray7561[index], ints_11);
                }
            }
        }
    }

    this.transform = function(id, type, labels, transformX, transformY, transformZ, i_6, bool_7, i_8, ints_9) {
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
            this.finishTransform(id, type, labels, transformedX, transformY, transformedZ, i_6, bool_7);
    }

    this.transformWithExtra = function() {
        console.error('TRANSFORMED WITH EXTRA!!!! CODY!!!!');
    }

    this.finishTransform = function(id, type, labels, transformedX, transformedY, transformedZ, i_6, bool_7) {
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
        } else if (type == 3) {
            for (let i = 0; i < labelCount; i++) {
                let label = labels[i];
                if (label < this.bones.length) {
                    let bones = this.bones[label];
                    for (let j = 0; j < bones.length; j++) {
                        let bone = bones[j];
                        this.vertexX[bone] -= this.xOffset;
                        this.vertexY[bone] -= this.yOffset;
                        this.vertexZ[bone] -= this.zOffset;
                        this.vertexX[bone] = transformedX * this.vertexX[bone] / 128;
                        this.vertexY[bone] = transformedY * this.vertexY[bone] / 128;
                        this.vertexZ[bone] = transformedZ * this.vertexZ[bone] / 128;
                        this.vertexX[bone] += this.xOffset;
                        this.vertexY[bone] += this.yOffset;
                        this.vertexZ[bone] += this.zOffset;
                    }
                }
            }
        } else if (type == 5) {
            if (this.anIntArrayArray8924 == null)
                return;
            let bool_24 = false;
            for (let i = 0; i < labelCount; i++) {
                let label = labels[i];
                if (label < this.anIntArrayArray8924.length) {
                    let bones = this.anIntArrayArray8924[label];
                    for (let j = 0; j < bones.length; j++) {
                        let bone = bones[j];
                        let i_15 = (this.faceAlphas[bone] & 0xFF) + transformedX * 8;
                        if (i_15 < 0)
                            i_15 = 0;
                        if (i_15 > 255)
                            i_15 = 255;

                        if (i_15 > 127)
                            i_15 -= 256;

                        this.faceAlphas[bone] = i_15;
                    }
                }
            }
        } else {
            console.error('UNHANDLED TYPE: ' + type);
        }
    }

    this.method11256 = function(triangles, triangleCount) {
        let triX = [];
        let triY = [];
        let triZ = [];
        let pixels = [];
        if (this.model.TexturePos != null) {
            let texturedFaceCount = this.model.TexturedFaceCount;
            let lowestVertexX = Array(texturedFaceCount);
            let highestVertexX = Array(texturedFaceCount);
            let lowestVertexY = Array(texturedFaceCount);
            let highestVertexY = Array(texturedFaceCount);
            let lowestVertexZ = Array(texturedFaceCount);
            let highestVertexZ = Array(texturedFaceCount);

            for (let i = 0; i < texturedFaceCount; i++) {
                lowestVertexX[i] = Number.MAX_VALUE;
                highestVertexX[i] = Number.MIN_VALUE;
                lowestVertexY[i] = Number.MAX_VALUE;
                highestVertexY[i] = Number.MIN_VALUE;
                lowestVertexZ[i] = Number.MAX_VALUE;
                highestVertexZ[i] = Number.MIN_VALUE;
            }

            for (let i = 0; i < triangleCount; i++) {
                let face = triangles[i];
                if (this.model.TexturePos[face] == -1)
                    continue;
                let type = this.model.TexturePos[face] & 0xFF;
                for (let side = 0; side < 3; side++) {
                    let triangle;
                    if (side == 0)
                        triangle = this.model.TriangleX[face];
                    else if (side == 1)
                        triangle = this.model.TriangleY[face];
                    else if (side == 2)
                        triangle = this.model.TriangleZ[face];

                    let vertexX = this.vertexX[triangle];
                    let vertexY = this.vertexY[triangle];
                    let vertexZ = this.vertexZ[triangle];
                    if (vertexX < lowestVertexX[type])
                        lowestVertexX[type] = vertexX;
                    if (vertexX > highestVertexX[type])
                        highestVertexX[type] = vertexX;

                    if (vertexY < lowestVertexY[type])
                        lowestVertexY[type] = vertexY;
                    if (vertexY > highestVertexY[type])
                        highestVertexY[type] = vertexY;

                    if (vertexZ < lowestVertexZ[type])
                        lowestVertexZ[type] = vertexZ;
                    if (vertexZ > highestVertexZ[type])
                        highestVertexZ[type] = vertexZ;
                }
            }

            triX = Array(texturedFaceCount);
            triY = Array(texturedFaceCount);
            triZ = Array(texturedFaceCount);
            pixels = Array(texturedFaceCount);

            for (let i = 0; i < texturedFaceCount; i++) {
                let type = this.model.TextureRenderTypes[i];
                if (type > 0) {
                    triX[i] = (lowestVertexX[i] + highestVertexX[i]) / 2;
                    triY[i] = (lowestVertexY[i] + highestVertexY[i]) / 2;
                    triZ[i] = (lowestVertexZ[i] + highestVertexZ[i]) / 2;
                    let x;
                    let y;
                    let z;
                    if (type == 1) {
                        let dirX = this.model.ParticleDirectionX[i];
                        if (dirX == 0) {
                            x = 1.0;
                            z = 1.0;
                        } else if (dirX > 0) {
                            x = 1.0;
                            z = dirX / 1024.0;
                        } else {
                            z = 1.0;
                            x = (-dirX) / 1024.0;
                        }

                        y = 64.0 / this.model.ParticleDirectionY[i];
                    } else if (type == 2) {
                        x = 64.0 / this.model.ParticleDirectionX[i];
                        y = 64.0 / this.model.ParticleDirectionY[i];
                        z = 64.0 / this.model.ParticleDirectionZ[i];
                    } else {
                        x = this.model.ParticleDirectionX[i] / 1024.0;
                        y = this.model.ParticleDirectionY[i] / 1024.0;
                        z = this.model.ParticleDirectionZ[i] / 1024.0;
                    }

                    pixels[i] = this.method11257(this.model.TexTriX[i], this.model.TexTriY[i], this.model.TexTriZ[i], this.model.ParticleLifespanX[i] & 0xFF, x, y, z);
                }
            }
        }
        return {
            triX,
            triY,
            triZ,
            pixels
        };
    }

    this.method11257 = function(texTriX, texTriY, texTriZ, lifespan, x, y, z) {
        let floats_8 = Array(9);
        let floats_9 = Array(9);
        let cosine = Math.cos(lifespan * 0.024543693);
        let sine = Math.sin(lifespan * 0.024543693);
        let f_12;
        floats_8[0] = cosine;
        floats_8[1] = 0.0;
        floats_8[2] = sine;
        floats_8[3] = 0.0;
        floats_8[4] = 1.0;
        floats_8[5] = 0.0;
        floats_8[6] = -sine;
        floats_8[7] = 0.0;
        floats_8[8] = cosine;
        let floats_13 = Array(9);
        let f_14 = 1.0;
        let f_15 = 0.0;
        cosine = texTriY / 32767.0;
        sine = -Math.sqrt(1.0 - cosine * cosine);
        f_12 = 1.0 - cosine;
        let f_16 = Math.sqrt(texTriZ * texTriZ + texTriX + texTriX);
        if (f_16 == 0.0 && cosine == 0.0)
            floats_9 = floats_8;
        else {
            if (f_16 != 0.0) {
                f_14 = (-texTriZ) / f_16;
                f_15 = texTriX / f_16;
            }

            floats_13[0] = cosine + f_14 * f_14 * f_12;
            floats_13[1] = f_15 * sine;
            floats_13[2] = f_15 * f_14 * f_12;
            floats_13[3] = -f_15 * sine;
            floats_13[4] = cosine;
            floats_13[5] = f_14 * sine;
            floats_13[6] = f_14 * f_15 * f_12;
            floats_13[7] = -f_14 * sine;
            floats_13[8] = cosine + f_15 * f_15 * f_12;
            floats_9[0] = floats_8[0] * floats_13[0] + floats_8[1] * floats_13[3] + floats_8[2] * floats_13[6];
            floats_9[1] = floats_8[0] * floats_13[1] + floats_8[1] * floats_13[4] + floats_8[2] * floats_13[7];
            floats_9[2] = floats_8[0] * floats_13[2] + floats_8[1] * floats_13[5] + floats_8[2] * floats_13[8];
            floats_9[3] = floats_8[3] * floats_13[0] + floats_8[4] * floats_13[3] + floats_8[5] * floats_13[6];
            floats_9[4] = floats_8[3] * floats_13[1] + floats_8[4] * floats_13[4] + floats_8[5] * floats_13[7];
            floats_9[5] = floats_8[3] * floats_13[2] + floats_8[4] * floats_13[5] + floats_8[5] * floats_13[8];
            floats_9[6] = floats_8[6] * floats_13[0] + floats_8[7] * floats_13[3] + floats_8[8] * floats_13[6];
            floats_9[7] = floats_8[6] * floats_13[1] + floats_8[7] * floats_13[4] + floats_8[8] * floats_13[7];
            floats_9[8] = floats_8[6] * floats_13[2] + floats_8[7] * floats_13[5] + floats_8[8] * floats_13[8];
        }

        floats_9[0] *= x;
        floats_9[1] *= x;
        floats_9[2] *= x;
        floats_9[3] *= y;
        floats_9[4] *= y;
        floats_9[5] *= y;
        floats_9[6] *= z;
        floats_9[7] *= z;
        floats_9[8] *= z;
        return floats_9;
    }

    this.method8316 = function(longs_0, ints_1, i_2, i_3) {
        if (i_2 >= i_3) return;
        let i_5 = (i_3 + i_2) / 2;
        let i_6 = i_2;
        let long_7 = longs_0[i_5];
        longs_0[i_5] = longs_0[i_3];
        longs_0[i_3] = long_7;
        let i_9 = ints_1[i_5];
        ints_1[i_5] = ints_1[i_3];
        ints_1[i_3] = i_9;
        let i_10 = long_7 == BigInt(9.223372036854799561e18) ? 0 : 1;

        for (let i = i_2; i < i_3; i++) {
            if (longs_0[i] < (i & i_10) + long_7) {
                let long_12 = longs_0[i];
                longs_0[i] = longs_0[i_6];
                longs_0[i_6] = long_12;
                let i_14 = ints_1[i];
                ints_1[i] = ints_1[i_6];
                ints_1[i_6++] = i_14;
            }
        }

        longs_0[i_3] = longs_0[i_6];
        longs_0[i_6] = long_7;
        ints_1[i_3] = ints_1[i_6];
        ints_1[i_6] = i_9;
        this.method8316(longs_0, ints_1, i_2, i_6 - 1);
        this.method8316(longs_0, ints_1, i_6 + 1, i_3);

    }

}