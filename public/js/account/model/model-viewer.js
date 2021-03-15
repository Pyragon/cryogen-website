let renderer;
let scene;
let camera;

let player;
let playerModel;
let spotAnimationModel;

let controls;

let animationManager;
let spotAnimationManager;

let playerAnimation;
let spotAnimation;
let playerRasterizer;
let spotAnimationRasterizer;

let renderId;
let temporaryId = -1;
let toAdd = false;

let spotAnimationId;
let delayRender = false;

let loading = false;
let playable = false;

let animationPulse;

let spotAnimations = [];

function loadViewer() {
    post('/account/overview/render', {}, data => {
        if (!data.model) return false;
        temporaryId = parseInt(data.animationId);
        renderId = parseInt(data.renderId);
        playerModel = JSON.parse(data.model);

        loadScene(playerModel, parseInt(data.height));
        return false;
    });
}

async function loadScene(model, modelHeight) {

    scene = new THREE.Scene();

    let width = 250;
    let height = 400;

    camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 30000);

    renderer = new THREE.WebGLRenderer();
    renderer.setSize(width, height);

    $('#model-viewer #render').append(renderer.domElement);

    player = await loadModel(model, modelHeight, true);
    scene.add(player);

    camera.position.x = 90;
    camera.position.y = 600;
    camera.position.z = 1110;

    controls = new THREE.TrackballControls(camera, renderer.domElement);
    controls.target.set(0, 500, 0);

    controls.addEventListener('end', function() {

    });

    controls.noPan = true;
    controls.rotateSpeed = 0.03;

    if (Number.isInteger(temporaryId)) {
        playerAnimation = new Animation.Animation();
        playerAnimation.setupAnimation(temporaryId, model.Animation);
    } else if (Number.isInteger(renderId)) {
        playerAnimation = new Animation.Animation();
        playerAnimation.setupAnimation(renderId, model.Render);
    }

    playerRasterizer = new Rasterizer.Rasterizer(model, player);
    await playerRasterizer.init(modelHeight);

    // animation.setupLoop(1);
    // animation.rasterize(rasterizer);

    animate();

    animationPulse = setupInterval();
    listenForAnimation();
}

async function loadModel(model, modelHeight, flip = false) {
    console.log(model);

    //ADD CUBE
    let vertices = [];

    let hasAlpha = model.FaceAlphas != null;
    let hasFaceTypes = model.FaceType != null;

    let vertexX = JSON.parse(JSON.stringify(model.VertexX));
    let vertexY = JSON.parse(JSON.stringify(model.VertexY));
    let vertexZ = JSON.parse(JSON.stringify(model.VertexZ));

    let h = -modelHeight << 2;

    for (let i = 0; i < model.MaxDepth; i++)
        vertexY[i] += h;

    //BUILD GEOMETRY
    for (let i = 0; i < model.FaceCount; i++) {

        let alpha = hasAlpha ? model.FaceAlphas[i] : 0;
        if (alpha == -1) continue;

        alpha = ~alpha & 0xFF;
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

        let colour = model.RealFaceColour[i];
        let r = (colour >> 16) & 0xFF;
        let g = (colour >> 8) & 0xFF;
        let b = colour & 0xFF;
        vertices.push({ pos: [vertexX[faceA], vertexY[faceA], vertexZ[faceA]], norm: [faceA, faceB, faceC], colors: [r, g, b, alpha] });
        vertices.push({ pos: [vertexX[faceB], vertexY[faceB], vertexZ[faceB]], norm: [faceA, faceB, faceC], colors: [r, g, b, alpha] });
        vertices.push({ pos: [vertexX[faceC], vertexY[faceC], vertexZ[faceC]], norm: [faceA, faceB, faceC], colors: [r, g, b, alpha] });
    }

    let positions = [];
    let normals = [];
    let colors = [];
    for (let vertex of vertices) {
        positions.push(...vertex.pos);
        normals.push(...vertex.norm);
        colors.push(...vertex.colors);
    }

    let geometry = new THREE.BufferGeometry();
    geometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(positions), 3));
    geometry.setAttribute('normal', new THREE.BufferAttribute(new Float32Array(normals), 3));
    geometry.setAttribute('color', new THREE.BufferAttribute(new Uint8Array(colors), 4, true));

    geometry.normalizeNormals();
    geometry.computeVertexNormals();


    let material = new THREE.MeshBasicMaterial({ vertexColors: THREE.VertexColors });
    let mesh = new THREE.Mesh(geometry, material);

    mesh.rotation.x = Math.PI;

    return mesh;
}

function setupInterval() {
    return setInterval(async function() {
        if (playable || playerAnimation == null) return;

        if (playerAnimation.setupLoop(1) && playerAnimation.getABool5462()) {
            if (Number.isInteger(temporaryId) && temporaryId != -1) {
                temporaryId = -1;
                if (!Number.isInteger(renderId)) {
                    playerAnimation.resetAnimation();
                    playerRasterizer.resetModel();
                    playerRasterizer.render();
                    playerAnimation = null;
                    clearInterval(animationPulse);
                    animationPulse = null;
                    return false;
                }
                playerAnimation.setupAnimation(renderId, playerModel.Render);
            }
            playerAnimation.resetAnimation();
        }
        if (toAdd == true) {
            delayRender = true;
            scene.add(spotAnimationModel);
            toAdd = false;
        }
        if (spotAnimation != null) {
            if (spotAnimation.setupLoop(1) && spotAnimation.getABool5462()) {
                scene.remove(spotAnimationModel);
                spotAnimation = null;
                spotAnimationRasterizer = null;
            }
        }
        playable = true;
        await playerAnimation.rasterize(playerRasterizer, 0);
        if (spotAnimation != null)
            await spotAnimation.rasterize(spotAnimationRasterizer, 0);
        delayRender = false;
        playable = false;
    }, 20);
}

function listenForAnimation() {
    $('#play-animation').on('click', changeAnimation);
    $('#play-spot-animation').on('click', startSpotAnimation);
    $('#animation-id input, #spot-animation-id input').on('keydown', function(e) {
        if (e.which == 13) {
            if ($(this).parent().attr('id').includes('spot'))
                startSpotAnimation();
            else
                changeAnimation();
        }
    });
    $('#get-camera-coords').on('click', () => {
        sendAlert('X: ' + camera.position.x + ' Y: ' + camera.position.y + ' Z: ' + camera.position.z);
    });
}

async function loadAnimation(id) {
    if (spotAnimations[id]) return spotAnimations[id];
    let animation = await Promise.resolve($.post('/animations/spot/' + id, { visitorId }));
    let data = parseJSON(animation);
    if (data == null)
        return null;
    spotAnimations[id] = data;
    return spotAnimations[id];
}

async function startSpotAnimation() {
    if (spotAnimation != null) {
        sendAlert('A spot animation is already playing. Please wait for it to finish first.');
        return false;
    }
    let input = $('#spot-animation-id').find('input').val();
    let id;
    let height = 0;
    if (input.includes(',')) {
        let vals = input.split(/\, ?/);
        if (!vals) {
            sendAlert('Please ensure the animation id/height is typed correctly (id, height)');
            return false;
        }
        id = parseInt(vals[0]);
        height = parseInt(vals[1]);
    } else
        id = parseInt(input);
    if (!Number.isInteger(id)) {
        sendAlert('Please make sure you enter a number.');
        return false;
    }
    let spot = await loadAnimation(id);
    if (!spot) return false;
    let model = JSON.parse(spot.model);
    let animation = JSON.parse(spot.animation);
    spotAnimationModel = await loadModel(model, height);

    spotAnimationRasterizer = new Rasterizer.Rasterizer(model, spotAnimationModel);
    spotAnimationRasterizer.init(height);

    spotAnimation = new Animation.Animation();
    spotAnimation.setupAnimation(id, animation);
    changeAnimation();
    toAdd = true;
}

function changeAnimation() {
    if (loading == true) {
        sendAlert('Already loading an animation. Please wait for it to load first.');
        return false;
    }
    let id = parseInt($('#animation-id').find('input').val());
    if (!Number.isInteger(id)) {
        sendAlert('Please make sure you enter a number.');
        return false;
    }
    loading = true;
    post('/animations/' + id, {}, data => {
        temporaryId = id;
        if (playerAnimation == null)
            playerAnimation = new Animation.Animation();
        playerAnimation.setupAnimation(id, JSON.parse(data.animation));
        playerAnimation.resetAnimation();
        if (animationPulse == null)
            animationPulse = setupInterval();
        loading = false;
    }, () => { loading = false });
}

function animate() {
    requestAnimationFrame(animate);

    controls.update();

    if (!delayRender)
        renderer.render(scene, camera);
}