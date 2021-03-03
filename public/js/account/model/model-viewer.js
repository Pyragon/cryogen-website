let renderer;
let scene;
let camera;

let cube;

let controls;

let animationManager;

let animation;
let rasterizer;

let renderId;
let temporaryId = -1;

let loading = false;

function loadScene() {
    post('/account/overview/render', {}, data => {
        if (!data.model) return false;
        loadModel(JSON.parse(data.model));
        return false;
    });
}

function getCube() {
    return cube;
}

async function loadModel(model) {
    console.log(model);

    scene = new THREE.Scene();

    let width = 250;
    let height = 400;

    camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 30000);

    renderer = new THREE.WebGLRenderer();
    renderer.setSize(width, height);

    $('#model-viewer #render').append(renderer.domElement);

    //ADD CUBE
    let vertices = [];

    let hasAlpha = model.FaceAlphas != null;
    let hasFaceTypes = model.FaceType != null;

    //BUILD GEOMETRY
    let size = 0;
    for (let i = 0; i < model.FaceCount; i++) {

        let alpha = hasAlpha ? model.FaceAlphas[i] : 0;
        if (alpha == -1) continue;
        size++;

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
        vertices.push({ pos: [model.VertexX[faceA], model.VertexY[faceA], model.VertexZ[faceA]], norm: [faceA, faceB, faceC], colors: [r, g, b, alpha] });
        vertices.push({ pos: [model.VertexX[faceB], model.VertexY[faceB], model.VertexZ[faceB]], norm: [faceA, faceB, faceC], colors: [r, g, b, alpha] });
        vertices.push({ pos: [model.VertexX[faceC], model.VertexY[faceC], model.VertexZ[faceC]], norm: [faceA, faceB, faceC], colors: [r, g, b, alpha] });
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
    cube = new THREE.Mesh(geometry, material);

    cube.rotation.x = Math.PI; //model renders upside down for some reason

    scene.add(cube);
    camera.position.z = 1000;

    controls = new THREE.TrackballControls(camera, renderer.domElement);
    controls.target.set(0, 500, 0);

    controls.addEventListener('end', function() {

    });

    controls.noPan = true;
    controls.rotateSpeed = 0.03;

    renderId = parseInt('!{animationId}');

    rasterizer = new Rasterizer.Rasterizer(model, cube);
    animation = new Animation.Animation();
    animation.setupAnimation(renderId, model.Animation);

    let playable = false;

    animate();

    setInterval(async function() {
        if (playable) return;

        if (animation.setupLoop(1) && animation.getABool5462()) {
            if (temporaryId != -1) {
                animation.setupAnimation(renderId, model.Animation);
                temporaryId = -1;
            }
            animation.resetAnimation();
        }
        playable = true;
        if (animation != null)
            await animation.rasterize(rasterizer, 0);
        playable = false;
    }, 20);
    listenForAnimation();
}

function listenForAnimation() {
    $('#play-animation').on('click', () => {
        let input = parseInt($('#model-viewer').find('input').val());
        if (!Number.isInteger(input)) {
            sendAlert('Please make sure you enter a number.');
            return false;
        }
        changeAnimation(input);
    });
}

function changeAnimation(id) {
    if (loading == true) {
        sendAlert('Already loading an animation. Please wait for it to load first.');
        return false;
    }
    loading = true;
    post('/animations/' + id, {}, data => {
        temporaryId = id;
        animation.setupAnimation(id, JSON.parse(data.animation));
        animation.resetAnimation();
        loading = false;
    }, () => { loading = false });
}

function animate() {
    requestAnimationFrame(animate);

    controls.update();

    renderer.render(scene, camera);
}